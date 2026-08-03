package com.firstapp.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.firstapp.myapplication.database.AppDatabase
import com.firstapp.myapplication.database.entity.UserProfile
import com.firstapp.myapplication.database.relation.ExpenseWithCategory
import com.firstapp.myapplication.repository.ExpenseRepository
import com.firstapp.myapplication.repository.UserProfileRepository
import com.firstapp.myapplication.utils.AnalyticsPeriod
import com.firstapp.myapplication.utils.CategoryVisuals
import com.firstapp.myapplication.utils.DateUtils
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/** Aggregate data for one category used by the Analytics screen. */
data class CategorySpending(
    val name: String,
    val iconResId: Int,
    val colorResId: Int,
    val amount: Double
)

/** All calculated values shown on the Analytics screen. */
data class AnalyticsData(
    val totalExpenses: Double,
    val transactionCount: Int,
    val monthlyIncome: Double,
    val remainingBalance: Double,
    val categorySpending: List<CategorySpending>,
    val monthlyTotals: List<Pair<String, Double>>,
    val topCategory: String,
    val averageDaily: Double,
    val largestExpense: Double
)

/**
 * Computes the Analytics screen values from the Room database instead of
 * using placeholder/sample data. The selected period (week / month / year)
 * determines the range over which the aggregates are calculated. The user
 * profile supplies the monthly income used for the remaining-balance card.
 */
class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application.applicationContext)
    private val repository = ExpenseRepository(database.expenseDao())
    private val profileRepository = UserProfileRepository(database.userProfileDao())

    private val _selectedPeriod = MutableLiveData(AnalyticsPeriod.THIS_MONTH)
    val selectedPeriod: LiveData<AnalyticsPeriod> get() = _selectedPeriod

    /** Whether any expense exists at all — drives the empty state. */
    val hasExpenses: LiveData<Boolean> = repository.getExpenseCount()
        .map { it > 0 }
        .asLiveData()

    /**
     * Recomputes analytics automatically whenever the selected period or the
     * user profile changes. Each period queries the database for that range,
     * combines it with the profile and maps the result into an [AnalyticsData].
     */
    val analytics: LiveData<AnalyticsData> = combine(
        _selectedPeriod.switchMap { period ->
            val (start, end) = DateUtils.periodRange(period)
            repository.getExpensesBetweenDate(start, end)
                .map { expenses -> period to expenses }
                .asLiveData()
        }.asFlow(),
        profileRepository.getProfile()
    ) { (period, expenses), profile ->
        computeAnalytics(period, expenses, profile)
    }.asLiveData()

    fun selectPeriod(period: AnalyticsPeriod) {
        if (_selectedPeriod.value != period) {
            _selectedPeriod.value = period
        }
    }

    /**
     * Computes all analytics values for [period] from the given [expenses]
     * and the user [profile].
     */
    private fun computeAnalytics(
        period: AnalyticsPeriod,
        expenses: List<ExpenseWithCategory>,
        profile: UserProfile?
    ): AnalyticsData {
        val monthlyIncome = profile?.monthlyIncome ?: 0.0

        if (expenses.isEmpty()) {
            return AnalyticsData(
                totalExpenses = 0.0,
                transactionCount = 0,
                monthlyIncome = monthlyIncome,
                remainingBalance = monthlyIncome,
                categorySpending = emptyList(),
                monthlyTotals = DateUtils.monthsOfYear().map { (start, _) ->
                    DateUtils.formatMonthYearLabel(start) to 0.0
                },
                topCategory = "—",
                averageDaily = 0.0,
                largestExpense = 0.0
            )
        }

        val total = expenses.sumOf { it.expense.amount }
        val count = expenses.size

        // Spending per category (sorted highest first)
        val categorySpending = expenses
            .groupBy { it.category?.name ?: "Other" }
            .map { (name, list) ->
                val category = list.firstNotNullOfOrNull { it.category }
                CategorySpending(
                    name = name,
                    iconResId = CategoryVisuals.iconResId(category?.icon ?: "ic_category_outline"),
                    colorResId = CategoryVisuals.colorResId(category?.color ?: "primary_container"),
                    amount = list.sumOf { it.expense.amount }
                )
            }
            .sortedByDescending { it.amount }

        // Monthly totals for every month of the current year
        val monthlyTotals = DateUtils.monthsOfYear().map { (start, end) ->
            val monthTotal = expenses
                .filter { it.expense.transactionDate in start until end }
                .sumOf { it.expense.amount }
            DateUtils.formatMonthYearLabel(start) to monthTotal
        }

        // Insights
        val topCategory = categorySpending.firstOrNull()?.name ?: "—"

        val (rangeStart, rangeEnd) = DateUtils.periodRange(period)
        val daysInPeriod = DateUtils.daysInRange(rangeStart, rangeEnd).coerceAtLeast(1)
        val averageDaily = total / daysInPeriod

        val largestExpense = expenses.maxOfOrNull { it.expense.amount } ?: 0.0

        return AnalyticsData(
            totalExpenses = total,
            transactionCount = count,
            monthlyIncome = monthlyIncome,
            remainingBalance = monthlyIncome - total,
            categorySpending = categorySpending,
            monthlyTotals = monthlyTotals,
            topCategory = topCategory,
            averageDaily = averageDaily,
            largestExpense = largestExpense
        )
    }
}
