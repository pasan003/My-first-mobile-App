package com.firstapp.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.firstapp.myapplication.databinding.ActivityExpenseHistoryBinding

/**
 * Activity that displays the full Expense History screen.
 *
 * Features:
 * - Material Top App Bar with back arrow and filter icon
 * - Summary card showing total expenses and transaction count
 * - Search bar (UI only — for future implementation)
 * - Horizontally scrollable filter chips (UI only — for future implementation)
 * - RecyclerView populated with sample expense data
 * - Empty state layout (hidden by default, for future database integration)
 * - Floating Action Button (UI only — no navigation yet)
 *
 * This screen will later become the **Read** part of CRUD operations
 * backed by Room Database. The RecyclerView adapter and item layout
 * are designed to be easily swapped to a database-backed data source.
 */
class ExpenseHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
    }

    /**
     * Sets up the toolbar with back navigation and filter menu.
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_expense_history, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_filter -> {
                showPlaceholderToast(getString(R.string.cd_filter))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Returns an intent to open [ExpenseDetailActivity] populated with
     * the given [transaction] data.
     */
    private fun getExpenseDetailIntent(transaction: Transaction): Intent {
        return Intent(this, ExpenseDetailActivity::class.java).apply {
            putExtra(ExpenseDetailActivity.EXTRA_TITLE, transaction.title)
            putExtra(ExpenseDetailActivity.EXTRA_AMOUNT, transaction.amount)
            putExtra(ExpenseDetailActivity.EXTRA_CATEGORY, transaction.category)
            putExtra(ExpenseDetailActivity.EXTRA_DATE, transaction.date)
            putExtra(ExpenseDetailActivity.EXTRA_ICON_RES_ID, transaction.iconResId)
        }
    }

    /**
     * Shows a short toast as a placeholder for future filter functionality.
     */
    private fun showPlaceholderToast(action: String) {
        Toast.makeText(
            this,
            getString(R.string.sample_toast_placeholder, action),
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Sets up the RecyclerView with sample expense data.
     * Will later be replaced with Room Database queries.
     */
    private fun setupRecyclerView() {
        val sampleData = getSampleHistoryData()
        val adapter = ExpenseHistoryAdapter { transaction ->
            startActivity(getExpenseDetailIntent(transaction))
        }
        adapter.submitList(sampleData)
        binding.rvExpenseHistory.adapter = adapter

        binding.rvExpenseHistory.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
                setDrawable(resources.getDrawable(R.drawable.divider_transaction, theme))
            }
        )
    }

    /**
     * Returns a list of 12 sample transactions for UI demonstration.
     * Will be replaced by Room Database queries in a future update.
     */
    private fun getSampleHistoryData(): List<Transaction> {
        return listOf(
            Transaction(
                title = getString(R.string.sample_history_title_1),
                category = getString(R.string.sample_history_cat_1),
                amount = 850.00,
                date = getString(R.string.sample_history_date_1),
                iconResId = R.drawable.ic_food
            ),
            Transaction(
                title = getString(R.string.sample_history_title_2),
                category = getString(R.string.sample_history_cat_2),
                amount = 3500.00,
                date = getString(R.string.sample_history_date_2),
                iconResId = R.drawable.ic_transport
            ),
            Transaction(
                title = getString(R.string.sample_history_title_3),
                category = getString(R.string.sample_history_cat_3),
                amount = 2750.00,
                date = getString(R.string.sample_history_date_3),
                iconResId = R.drawable.ic_shopping
            ),
            Transaction(
                title = getString(R.string.sample_history_title_4),
                category = getString(R.string.sample_history_cat_4),
                amount = 5200.00,
                date = getString(R.string.sample_history_date_4),
                iconResId = R.drawable.ic_bills
            ),
            Transaction(
                title = getString(R.string.sample_history_title_5),
                category = getString(R.string.sample_history_cat_5),
                amount = 1200.00,
                date = getString(R.string.sample_history_date_5),
                iconResId = R.drawable.ic_entertainment
            ),
            Transaction(
                title = getString(R.string.sample_history_title_6),
                category = getString(R.string.sample_history_cat_6),
                amount = 2000.00,
                date = getString(R.string.sample_history_date_6),
                iconResId = R.drawable.ic_health
            ),
            Transaction(
                title = getString(R.string.sample_history_title_7),
                category = getString(R.string.sample_history_cat_7),
                amount = 8500.00,
                date = getString(R.string.sample_history_date_7),
                iconResId = R.drawable.ic_education
            ),
            Transaction(
                title = getString(R.string.sample_history_title_8),
                category = getString(R.string.sample_history_cat_8),
                amount = 1450.00,
                date = getString(R.string.sample_history_date_8),
                iconResId = R.drawable.ic_food
            ),
            Transaction(
                title = getString(R.string.sample_history_title_9),
                category = getString(R.string.sample_history_cat_9),
                amount = 2500.00,
                date = getString(R.string.sample_history_date_9),
                iconResId = R.drawable.ic_transport
            ),
            Transaction(
                title = getString(R.string.sample_history_title_10),
                category = getString(R.string.sample_history_cat_10),
                amount = 4200.00,
                date = getString(R.string.sample_history_date_10),
                iconResId = R.drawable.ic_shopping
            ),
            Transaction(
                title = getString(R.string.sample_history_title_11),
                category = getString(R.string.sample_history_cat_11),
                amount = 1899.00,
                date = getString(R.string.sample_history_date_11),
                iconResId = R.drawable.ic_bills
            ),
            Transaction(
                title = getString(R.string.sample_history_title_12),
                category = getString(R.string.sample_history_cat_12),
                amount = 1200.00,
                date = getString(R.string.sample_history_date_12),
                iconResId = R.drawable.ic_entertainment
            )
        )
    }
}
