package com.firstapp.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.DividerItemDecoration
import com.firstapp.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupClickListeners()
        setupBottomNavigation()
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
    }

    /**
     * Sets up the bottom navigation bar to switch between screens.
     * - Home: Stays on the current screen
     * - Categories: Opens CategoryManagerActivity
     * - Analytics: Placeholder toast (future implementation)
     * - Profile: Placeholder toast (future implementation)
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
                    showComingSoonToast(getString(R.string.nav_analytics))
                    true
                }
                R.id.nav_profile -> {
                    showComingSoonToast(getString(R.string.nav_profile))
                    true
                }
                else -> false
            }
        }
        // Preselect the home tab
        binding.bottomNavigation.selectedItemId = R.id.nav_home
    }

    /**
     * Shows a short toast indicating the feature is coming soon.
     */
    private fun showComingSoonToast(feature: String) {
        Toast.makeText(
            this,
            getString(R.string.sample_toast_placeholder, feature),
            Toast.LENGTH_SHORT
        ).show()
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
     * Sets up the RecyclerView with sample transaction data.
     */
    private fun setupRecyclerView() {
        val sampleTransactions = getSampleTransactions()
        val adapter = TransactionAdapter { transaction ->
            startActivity(getExpenseDetailIntent(transaction))
        }
        adapter.submitList(sampleTransactions)
        binding.rvRecentTransactions.adapter = adapter

        // Add subtle divider between items
        binding.rvRecentTransactions.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
                setDrawable(resources.getDrawable(R.drawable.divider_transaction, theme))
            }
        )
    }

    /**
     * Returns a list of sample transactions for UI demonstration.
     */
    private fun getSampleTransactions(): List<Transaction> {
        return listOf(
            Transaction(
                title = getString(R.string.sample_lunch),
                category = getString(R.string.cat_food),
                amount = 850.00,
                date = getString(R.string.date_today),
                iconResId = R.drawable.ic_food
            ),
            Transaction(
                title = getString(R.string.sample_groceries),
                category = getString(R.string.cat_shopping),
                amount = 3450.00,
                date = getString(R.string.date_today),
                iconResId = R.drawable.ic_shopping
            ),
            Transaction(
                title = getString(R.string.sample_bus_fare),
                category = getString(R.string.cat_transport),
                amount = 320.00,
                date = getString(R.string.date_yesterday),
                iconResId = R.drawable.ic_transport
            ),
            Transaction(
                title = getString(R.string.sample_netflix),
                category = getString(R.string.cat_entertainment),
                amount = 1200.00,
                date = "Dec 12",
                iconResId = R.drawable.ic_entertainment
            ),
            Transaction(
                title = getString(R.string.sample_electricity),
                category = getString(R.string.cat_bills),
                amount = 5200.00,
                date = "Dec 10",
                iconResId = R.drawable.ic_bills
            )
        )
    }
}
