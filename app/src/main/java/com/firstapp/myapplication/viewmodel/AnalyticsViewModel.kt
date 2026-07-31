package com.firstapp.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.firstapp.myapplication.database.AppDatabase
import com.firstapp.myapplication.database.relation.ExpenseWithCategory
import com.firstapp.myapplication.repository.ExpenseRepository
import com.firstapp.myapplication.utils.AnalyticsPeriod
import com.firstapp.myapplication.utils.CategoryVisuals
import com.firstapp.myapplication.utils.DateUtils
import kotlinx.coroutines.flow.map

/** Aggregate data for one category used by the Analytics screen. */
data class CategorySpending(
    val name: String,
    val iconResId: Int,
    val amount: Double
)

/** All calculated values shown on the Analytics screen. */
data class AnalyticsData(
    val totalExpenses: Double,
    val transactionCount: Int,
    val averageExpense: Double,
    val categorySpending: List<CategorySpending>,
    val monthlyTotals: List<Pair<String, Double>>,
    val topCategory: String,
    val averageDaily: Double,
    val highestDay: String,
    val largestExpense: Double
)

/**
 * Computes the Analytics screen values from the Room database instead of
 * using placeholder/sample data. The selected period (week / month / year)
 * determines the range over which the aggregates are calculated.
 */
class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository =
        ExpenseRepository(AppDatabase.getInstance(application.applicationContext).expenseDao())

    private val _selectedPeriod = MutableLiveData(AnalyticsPeriod.THIS_MONTH)
    val selectedPeriod: LiveData<AnalyticsPeriod> get() = _selectedPeriod

    /** Whether any expense exists at all — drives the empty state. */
    val hasExpenses: LiveData<Boolean> = repository.getExpenseCount()
        .map { it > 0 }
        .asLiveData()

    /**
     * Recomputes analytics automatically whenever the selected period changes.
     * Each period queries the database for that range and maps the result
     * into an [AnalyticsData] value.
     */
    val analytics: LiveData<AnalyticsData> = _selectedPeriod.switchMap { period ->
        val (start, end) = DateUtils.periodRange(period)
        repository.getExpensesBetweenDate(start, end)
            .map { expenses -> computeAnalytics(period, expenses) }
            .asLiveData()
    }

    fun selectPeriod(period: AnalyticsPeriod) {
        if (_selectedPeriod.value != period) {
            _selectedPeriod.value = period
        }
    }

    /**
     * Computes all analytics values for [period] from the given [expenses].
     */
    private fun computeAnalytics(
        period: AnalyticsPeriod,
        expenses: List<ExpenseWithCategory>
    ): AnalyticsData {
        if (expenses.isEmpty()) {
            return AnalyticsData(
                totalExpenses = 0.0,
                transactionCount = 0,
                averageExpense = 0.0,
                categorySpending = emptyList(),
                monthlyTotals = DateUtils.firstSixMonthsOfYear().map { (start, _) ->
                    DateUtils.formatMonthLabel(start) to 0.0
                },
                topCategory = "—",
                averageDaily = 0.0,
                highestDay = "—",
                largestExpense = 0.0
            )
        }

        val total = expenses.sumOf { it.expense.amount }
        val count = expenses.size

        // Spending per category (sorted highest first)
        val categorySpending = expenses
            .groupBy { it.category?.name ?: "Other" }
            .map { (name, list) ->
                val iconName = list.firstNotNullOfOrNull { it.category }?.icon
                    ?: "ic_category_outline"
                CategorySpending(
                    name = name,
                    iconResId = CategoryVisuals.iconResId(iconName),
                    amount = list.sumOf { it.expense.amount }
                )
            }
            .sortedByDescending { it.amount }

        // Monthly totals for the first six months of the current year
        val monthlyTotals = DateUtils.firstSixMonthsOfYear().map { (start, end) ->
            val monthTotal = expenses
                .filter { it.expense.transactionDate in start until end }
                .sumOf { it.expense.amount }
            DateUtils.formatMonthLabel(start) to monthTotal
        }

        // Insights
        val topCategory = categorySpending.firstOrNull()?.name ?: "—"

        val (rangeStart, rangeEnd) = DateUtils.periodRange(period)
        val daysInPeriod = DateUtils.daysInRange(rangeStart, rangeEnd).coerceAtLeast(1)
        val averageDaily = total / daysInPeriod

        val highestDay = expenses
            .groupBy { DateUtils.formatDayName(it.expense.transactionDate) }
            .maxByOrNull { it.value.sumOf { e -> e.expense.amount } }
            ?.key ?: "—"

        val largestExpense = expenses.maxOfOrNull { it.expense.amount } ?: 0.0

        return AnalyticsData(
            totalExpenses = total,
            transactionCount = count,
            averageExpense = if (count == 0) 0.0 else total / count,
            categorySpending = categorySpending,
            monthlyTotals = monthlyTotals,
            topCategory = topCategory,
            averageDaily = averageDaily,
            highestDay = highestDay,
            largestExpense = largestExpense
        )
    }
}
