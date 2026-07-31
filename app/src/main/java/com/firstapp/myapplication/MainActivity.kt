package com.firstapp.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.DividerItemDecoration
import com.firstapp.myapplication.databinding.ActivityMainBinding
import com.firstapp.myapplication.utils.CurrencyUtils
import com.firstapp.myapplication.viewmodel.ExpenseViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ExpenseViewModel by viewModels()
    private lateinit var adapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupClickListeners()
        setupBottomNavigation()
        observeData()
    }

    /**
     * Sets up click listeners, including the FAB to navigate to AddExpenseActivity,
     * transaction item taps to open ExpenseDetailActivity,
     * and the View All link to open ExpenseHistoryActivity.
     */
    private fun setupClickListeners() {
        binding.fabAddExpense.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }

        binding.tvViewAll.setOnClickListener {
            val intent = Intent(this, ExpenseHistoryActivity::class.java)
            startActivity(intent)
        }

        // Profile avatar (top-right) opens Profile & Settings screen
        binding.cardProfileAvatar.setOnClickListener {
            val intent = Intent(this, ProfileSettingsActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Sets up the bottom navigation bar to switch between screens.
     * - Home: Stays on the current screen
     * - Categories: Opens CategoryManagerActivity
     * - Analytics: Opens AnalyticsActivity
     * - Profile: Opens ProfileSettingsActivity
     */
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Already on home — do nothing
                    true
                }
                R.id.nav_categories -> {
                    val intent = Intent(this, CategoryManagerActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_analytics -> {
                    val intent = Intent(this, AnalyticsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileSettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        // Preselect the home tab
        binding.bottomNavigation.selectedItemId = R.id.nav_home
    }

    /**
     * Returns an intent to open [ExpenseDetailActivity] for the given transaction,
     * passing the database id so the detail screen can load the real record.
     */
    private fun getExpenseDetailIntent(transaction: Transaction): Intent {
        return Intent(this, ExpenseDetailActivity::class.java).apply {
            putExtra(ExpenseDetailActivity.EXTRA_EXPENSE_ID, transaction.id)
        }
    }

    /**
     * Sets up the RecyclerView and observes the Room database for changes.
     */
    private fun setupRecyclerView() {
        adapter = TransactionAdapter { transaction ->
            startActivity(getExpenseDetailIntent(transaction))
        }
        binding.rvRecentTransactions.adapter = adapter

        // Add subtle divider between items
        binding.rvRecentTransactions.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
                setDrawable(resources.getDrawable(R.drawable.divider_transaction, theme))
            }
        )
    }

    /**
     * Observes the ViewModel and updates the dashboard with real database data.
     */
    private fun observeData() {
        // Recent transactions (top 5) from Room
        viewModel.getRecentExpenses(5).observe(this) { transactions ->
            adapter.submitList(transactions)
        }

        // Balance card calculations using real data.
        // No income is tracked yet, so total income is 0 and the balance is -expenses.
        viewModel.totalExpenses.observe(this) { totalExpenses ->
            binding.tvExpensesAmount.text = CurrencyUtils.format(totalExpenses)
            binding.tvIncomeAmount.text = CurrencyUtils.format(0.0)
            binding.tvBalanceAmount.text = CurrencyUtils.format(0.0 - totalExpenses)
        }
    }
}
