package com.firstapp.myapplication

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.firstapp.myapplication.databinding.ActivityAnalyticsBinding
import com.firstapp.myapplication.databinding.ItemCategorySpendingBinding
import com.firstapp.myapplication.databinding.ItemMonthlySpendingBinding
import com.firstapp.myapplication.utils.AnalyticsPeriod
import com.firstapp.myapplication.utils.CurrencyUtils
import com.firstapp.myapplication.viewmodel.AnalyticsData
import com.firstapp.myapplication.viewmodel.AnalyticsViewModel
import com.firstapp.myapplication.viewmodel.CategorySpending
import com.firstapp.myapplication.viewmodel.UserProfileViewModel

/**
 * Analytics screen backed by real Room data.
 *
 * The four summary cards (total expenses, monthly income, remaining balance,
 * total transactions), the spending-by-category list, the monthly analysis,
 * the top categories ranking and the insights are all calculated from the
 * database for the selected period (This Week / This Month / This Year).
 *
 * Currency formatting follows the user profile's stored currency.
 */
class AnalyticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalyticsBinding
    private val viewModel: AnalyticsViewModel by viewModels()
    private val profileViewModel: UserProfileViewModel by viewModels()

    /** Currency symbol used for all formatted amounts on this screen. */
    private var currencySymbol: String = CurrencyUtils.DEFAULT_SYMBOL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupPeriodSelector()
        setupClickListeners()
        observeData()
    }

    /**
     * Sets up the toolbar with back navigation and calendar menu.
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_analytics, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_calendar -> {
                showPlaceholderToast(getString(R.string.analytics_calendar))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Shows a short toast as a placeholder for future functionality.
     */
    private fun showPlaceholderToast(action: String) {
        Toast.makeText(
            this,
            getString(R.string.sample_toast_placeholder, action),
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Wires the period chips (This Week / This Month / This Year).
     */
    private fun setupPeriodSelector() {
        binding.chipGroupPeriod.setOnCheckedStateChangeListener { _, checkedIds ->
            when (checkedIds.firstOrNull()) {
                R.id.chipThisWeek -> viewModel.selectPeriod(AnalyticsPeriod.THIS_WEEK)
                R.id.chipThisMonth -> viewModel.selectPeriod(AnalyticsPeriod.THIS_MONTH)
                R.id.chipThisYear -> viewModel.selectPeriod(AnalyticsPeriod.THIS_YEAR)
                else -> viewModel.selectPeriod(AnalyticsPeriod.THIS_MONTH)
            }
        }
    }

    /**
     * Sets up click listeners. The empty-state "Add Expense" button
     * navigates to the Add Expense screen.
     */
    private fun setupClickListeners() {
        binding.btnEmptyAddExpense.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }
    }

    /**
     * Observes analytics data, the empty state and the profile currency.
     */
    private fun observeData() {
        // Profile currency — formatting follows the user's preference
        profileViewModel.profile.observe(this) { profile ->
            currencySymbol = profile?.let { CurrencyUtils.symbolFor(it.currency) }
                ?: CurrencyUtils.DEFAULT_SYMBOL
            // Re-render everything with the new symbol
            viewModel.analytics.value?.let { render(it) }
        }

        viewModel.hasExpenses.observe(this) { hasExpenses ->
            binding.layoutEmptyState.visibility = if (hasExpenses) View.GONE else View.VISIBLE
            binding.contentContainer.visibility = if (hasExpenses) View.VISIBLE else View.GONE
        }

        viewModel.analytics.observe(this) { data ->
            render(data)
        }
    }

    /**
     * Renders every analytics section from the computed [data].
     */
    private fun render(data: AnalyticsData) {
        populateSummary(data)
        populateCategoryList(data)
        populateMonthlyTotals(data)
        populateTopCategories(data)
        populateInsights(data)
    }

    // ---------- SECTION: FINANCIAL SUMMARY CARDS ----------

    private fun populateSummary(data: AnalyticsData) {
        binding.tvTotalExpenses.text = CurrencyUtils.format(data.totalExpenses, currencySymbol)
        binding.tvMonthlyIncome.text = CurrencyUtils.format(data.monthlyIncome, currencySymbol)
        binding.tvRemainingBalance.text = CurrencyUtils.format(data.remainingBalance, currencySymbol)
        binding.tvTransactionCount.text = data.transactionCount.toString()
    }

    // ---------- SECTION: SPENDING BY CATEGORY (dynamic rows) ----------

    private fun populateCategoryList(data: AnalyticsData) {
        binding.layoutCategoryList.removeAllViews()

        data.categorySpending.forEach { spending ->
            val row = ItemCategorySpendingBinding.inflate(
                layoutInflater,
                binding.layoutCategoryList,
                false
            )
            row.tvCategoryName.text = spending.name
            row.tvCategoryAmount.text = CurrencyUtils.format(spending.amount, currencySymbol)

            // Colored dot from the category's stored color
            val dot = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(ContextCompat.getColor(this@AnalyticsActivity, spending.colorResId))
            }
            row.viewDot.background = dot

            binding.layoutCategoryList.addView(row.root)
        }
    }

    // ---------- SECTION: MONTHLY SPENDING (dynamic rows + bars) ----------

    private fun populateMonthlyTotals(data: AnalyticsData) {
        binding.layoutMonthlyList.removeAllViews()

        val maxAmount = data.monthlyTotals.maxOfOrNull { it.second } ?: 0.0

        data.monthlyTotals.forEach { (label, total) ->
            val row = ItemMonthlySpendingBinding.inflate(
                layoutInflater,
                binding.layoutMonthlyList,
                false
            )
            row.tvMonthLabel.text = label
            row.tvMonthAmount.text = CurrencyUtils.format(total, currencySymbol)
            row.progressMonth.progress =
                if (maxAmount > 0) (total / maxAmount * 100).toInt() else 0
            binding.layoutMonthlyList.addView(row.root)
        }
    }

    // ---------- SECTION: TOP CATEGORIES ----------

    /**
     * Rank rows contain: [0] medal/emoji, [1] name, [2] amount.
     */
    private fun populateTopCategories(data: AnalyticsData) {
        val rows = listOf(
            binding.layoutRank1,
            binding.layoutRank2,
            binding.layoutRank3,
            binding.layoutRank4,
            binding.layoutRank5
        )
        val topFive = data.categorySpending.take(5)

        rows.forEachIndexed { index, row ->
            val spending = topFive.getOrNull(index)
            val nameView = row.getChildAt(1) as? TextView
            val amountView = row.getChildAt(2) as? TextView

            nameView?.text = spending?.name ?: "—"
            amountView?.text = spending?.let { CurrencyUtils.format(it.amount, currencySymbol) } ?: "—"
        }
    }

    // ---------- SECTION: INSIGHTS ----------

    /**
     * Insight rows contain: [0] bullet, [1] text. Rows without content are hidden.
     */
    private fun populateInsights(data: AnalyticsData) {
        val insights = mutableListOf<String>()

        if (data.categorySpending.isNotEmpty()) {
            insights.add(
                getString(
                    R.string.analytics_insight_most_spent_on,
                    data.topCategory,
                    periodLabel(viewModel.selectedPeriod.value ?: AnalyticsPeriod.THIS_MONTH)
                )
            )
        }
        if (data.monthlyIncome > 0) {
            insights.add(
                getString(
                    R.string.analytics_insight_remaining_balance,
                    CurrencyUtils.format(data.remainingBalance, currencySymbol)
                )
            )
        }
        if (data.transactionCount > 0) {
            insights.add(
                getString(
                    R.string.analytics_insight_avg_daily,
                    CurrencyUtils.format(data.averageDaily, currencySymbol)
                )
            )
            insights.add(
                getString(
                    R.string.analytics_insight_largest_expense,
                    CurrencyUtils.format(data.largestExpense, currencySymbol)
                )
            )
        }

        val insightRows = listOf(
            binding.layoutInsight1,
            binding.layoutInsight2,
            binding.layoutInsight3,
            binding.layoutInsight4
        )

        insightRows.forEachIndexed { index, row ->
            val textView = row.getChildAt(1) as? TextView
            val hasInsight = index < insights.size
            row.visibility = if (hasInsight) View.VISIBLE else View.GONE
            textView?.text = insights.getOrNull(index) ?: ""
        }
    }

    /**
     * Returns a lowercase period label such as "this month" for the insights.
     */
    private fun periodLabel(period: AnalyticsPeriod): String = when (period) {
        AnalyticsPeriod.THIS_WEEK -> getString(R.string.period_insight_this_week)
        AnalyticsPeriod.THIS_MONTH -> getString(R.string.period_insight_this_month)
        AnalyticsPeriod.THIS_YEAR -> getString(R.string.period_insight_this_year)
    }
}
