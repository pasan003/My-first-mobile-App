package com.firstapp.myapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firstapp.myapplication.databinding.ActivityAnalyticsBinding

/**
 * Activity that displays the Analytics screen with spending insights.
 *
 * Features:
 * - Material Top App Bar with back arrow and calendar icon (UI only)
 * - Horizontal period selector chips (This Week, This Month, This Year — UI only)
 * - Three financial summary cards (Total Expenses, Total Transactions, Average Expense)
 * - Spending by Category section with pie chart placeholder and legend
 * - Monthly Spending section with bar chart placeholder and month labels
 * - Top Categories ranking card
 * - Insights card with sample spending insights
 * - Empty state layout (hidden by default, ready for future database integration)
 *
 * This screen is designed to later receive real data from Room Database.
 * All chart placeholders (pie and bar) use FrameLayout containers that can
 * be replaced with MPAndroidChart (or similar) views without changing the
 * surrounding layout. All important views have content descriptions for
 * accessibility and IDs for future data binding.
 *
 * Future features prepared for (no UI redesign needed):
 * - Real Pie Charts (replace FrameLayout content)
 * - Monthly Bar Charts (replace FrameLayout content)
 * - Weekly, Monthly, Yearly Reports (period chips + data)
 * - Date Range Selection (calendar menu item)
 * - Category Filtering (chip selection + data filtering)
 * - Export Reports (overflow menu)
 */
class AnalyticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalyticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupClickListeners()
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
     * Sets up click listeners for interactive elements.
     * Currently only the empty state "Add Expense" button is wired as placeholder.
     */
    private fun setupClickListeners() {
        binding.btnEmptyAddExpense.setOnClickListener {
            showPlaceholderToast(getString(R.string.analytics_empty_add_expense))
        }
    }
}
