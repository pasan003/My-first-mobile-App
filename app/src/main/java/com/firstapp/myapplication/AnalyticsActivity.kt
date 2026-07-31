package com.firstapp.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.firstapp.myapplication.databinding.ActivityAnalyticsBinding
import com.firstapp.myapplication.utils.AnalyticsPeriod
import com.firstapp.myapplication.utils.CurrencyUtils
import com.firstapp.myapplication.viewmodel.AnalyticsData
import com.firstapp.myapplication.viewmodel.AnalyticsViewModel
import com.firstapp.myapplication.viewmodel.CategorySpending

/**
 * Analytics screen backed by real Room data.
 *
 * The summary cards, spending-by-category legend, monthly totals, top
 * categories and insights are all calculated from the database for the
 * selected period (This Week / This Month / This Year).
 *
 * The legend/ranking/insight rows keep their existing layout; their child
 * TextViews (which have no ids in the XML) are populated by position —
 * [0] is the leading icon/medal, [1] the name and [2] the amount.
 */
class AnalyticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalyticsBinding
    private val viewModel: AnalyticsViewModel by viewModels()

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
     * Observes analytics data and the empty state from the ViewModel.
     */
    private fun observeData() {
        viewModel.hasExpenses.observe(this) { hasExpenses ->
            // The empty state and the analytics content live inside the same
            // scroll view, so we toggle them independently.
            binding.layoutEmptyState.visibility = if (hasExpenses) View.GONE else View.VISIBLE
            binding.contentContainer.visibility = if (hasExpenses) View.VISIBLE else View.GONE
        }

        viewModel.analytics.observe(this) { data ->
            populateSummary(data)
            populateLegend(data)
            populateMonthlyTotals(data)
            populateTopCategories(data)
            populateInsights(data)
        }
    }

    // ---------- SECTION: FINANCIAL SUMMARY CARDS ----------

    private fun populateSummary(data: AnalyticsData) {
        binding.tvTotalExpenses.text = CurrencyUtils.format(data.totalExpenses)
        binding.tvTransactionCount.text = data.transactionCount.toString()
        binding.tvAverageExpense.text = CurrencyUtils.format(data.averageExpense)
    }

    // ---------- SECTION: SPENDING BY CATEGORY (legend rows) ----------

    /**
     * Legend rows contain: [0] color dot, [1] name, [2] amount.
     */
    private fun populateLegend(data: AnalyticsData) {
        val rows = listOf(
            binding.layoutLegendFood,
            binding.layoutLegendTransport,
            binding.layoutLegendShopping,
            binding.layoutLegendBills
        )
        val topFour = data.categorySpending.take(4)

        rows.forEachIndexed { index, row ->
            val spending = topFour.getOrNull(index)
            setLegendRow(row, spending)
        }
    }

    private fun setLegendRow(row: LinearLayout, spending: CategorySpending?) {
        val nameView = row.getChildAt(1) as? TextView
        val amountView = row.getChildAt(2) as? TextView

        nameView?.text = spending?.name ?: "—"
        amountView?.text = spending?.let { CurrencyUtils.format(it.amount) } ?: "—"
    }

    // ---------- SECTION: MONTHLY SPENDING (month labels) ----------

    /**
     * The bar chart remains a placeholder; the six month labels are updated
     * with the real monthly totals for the first half of the current year.
     */
    private fun populateMonthlyTotals(data: AnalyticsData) {
        val labels = listOf(
            binding.tvMonthJan,
            binding.tvMonthFeb,
            binding.tvMonthMar,
            binding.tvMonthApr,
            binding.tvMonthMay,
            binding.tvMonthJun
        )
        data.monthlyTotals.forEachIndexed { index, (month, total) ->
            if (index < labels.size) {
                labels[index].text = "$month\n${CurrencyUtils.formatCompact(total)}"
            }
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
            amountView?.text = spending?.let { CurrencyUtils.format(it.amount) } ?: "—"
        }
    }

    // ---------- SECTION: INSIGHTS ----------

    /**
     * Insight rows contain: [0] bullet, [1] text.
     */
    private fun populateInsights(data: AnalyticsData) {
        val insightViews = listOf(
            binding.layoutInsight1,
            binding.layoutInsight2,
            binding.layoutInsight3,
            binding.layoutInsight4
        ).mapNotNull { it.getChildAt(1) as? TextView }

        val insights = listOf(
            getString(R.string.analytics_insight_highest_category, data.topCategory),
            getString(
                R.string.analytics_insight_avg_daily,
                CurrencyUtils.format(data.averageDaily)
            ),
            getString(R.string.analytics_insight_highest_day, data.highestDay),
            getString(
                R.string.analytics_insight_largest_expense,
                CurrencyUtils.format(data.largestExpense)
            )
        )

        insightViews.forEachIndexed { index, textView ->
            if (index < insights.size) {
                textView.text = insights[index]
            }
        }
    }
}
