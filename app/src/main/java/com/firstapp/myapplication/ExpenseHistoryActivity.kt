package com.firstapp.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.firstapp.myapplication.databinding.ActivityExpenseHistoryBinding
import com.firstapp.myapplication.utils.AnalyticsPeriod
import com.firstapp.myapplication.utils.CurrencyUtils
import com.firstapp.myapplication.utils.DateUtils
import com.firstapp.myapplication.utils.UiAnimations
import com.firstapp.myapplication.viewmodel.ExpenseViewModel

/**
 * Activity that displays the full Expense History screen, backed by Room.
 *
 * - Summary card showing total expenses (this month) and transaction count
 * - RecyclerView populated from the Room database
 * - Empty state shown when there are no expenses
 * - FAB navigates to the Add Expense screen
 */
class ExpenseHistoryActivity : BaseActivity() {

    private lateinit var binding: ActivityExpenseHistoryBinding
    private val viewModel: ExpenseViewModel by viewModels()
    private lateinit var adapter: ExpenseHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupFab()
        observeData()
        UiAnimations.pressFeedback(binding.fabAddExpense)
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
     * Returns an intent to open [ExpenseDetailActivity] for the given transaction,
     * passing the database id so the detail screen loads the real record.
     */
    private fun getExpenseDetailIntent(transaction: Transaction): Intent {
        return Intent(this, ExpenseDetailActivity::class.java).apply {
            putExtra(ExpenseDetailActivity.EXTRA_EXPENSE_ID, transaction.id)
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
     * Sets up the RecyclerView with expenses loaded from Room.
     */
    private fun setupRecyclerView() {
        adapter = ExpenseHistoryAdapter { transaction ->
            startActivity(getExpenseDetailIntent(transaction))
        }
        binding.rvExpenseHistory.adapter = adapter

        binding.rvExpenseHistory.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
                setDrawable(resources.getDrawable(R.drawable.divider_transaction, theme))
            }
        )
    }

    /**
     * Wires the FAB to the Add Expense screen.
     */
    private fun setupFab() {
        binding.fabAddExpense.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }
        binding.btnEmptyAddExpense.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }
    }

    /**
     * Observes the Room database and updates the list + summary card.
     */
    private fun observeData() {
        viewModel.allExpenses.observe(this) { transactions ->
            adapter.submitList(transactions)
            updateEmptyState(transactions.isEmpty())
        }

        // Transaction count with plural support
        viewModel.expenseCount.observe(this) { count ->
            binding.tvSummaryCount.text = resources.getQuantityString(
                R.plurals.transaction_count,
                count,
                count
            )
        }

        // Total for the current month
        val monthRange = DateUtils.periodRange(AnalyticsPeriod.THIS_MONTH)
        viewModel.getMonthlyExpenses(monthRange.first, monthRange.second).observe(this) { total ->
            binding.tvSummaryAmount.text = CurrencyUtils.format(total)
        }
    }

    /**
     * Shows or hides the empty state based on the list contents.
     */
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.layoutEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
}
