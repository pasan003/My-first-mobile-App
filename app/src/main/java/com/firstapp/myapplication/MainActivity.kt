package com.firstapp.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.DividerItemDecoration
import com.firstapp.myapplication.databinding.ActivityMainBinding
import com.firstapp.myapplication.utils.CurrencyUtils
import com.firstapp.myapplication.utils.UiAnimations
import com.firstapp.myapplication.viewmodel.ExpenseViewModel
import com.firstapp.myapplication.viewmodel.UserProfileViewModel
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ExpenseViewModel by viewModels()
    private val profileViewModel: UserProfileViewModel by viewModels()
    private lateinit var adapter: TransactionAdapter

    /** Latest values used to compute the balance card (income − expenses). */
    private var monthlyIncome = 0.0
    private var totalExpenses = 0.0

    /** Currency symbol from the user profile, used for all balance formatting. */
    private var currencySymbol = CurrencyUtils.DEFAULT_SYMBOL

    /** Guards against redirecting to setup more than once. */
    private var isRedirectingToSetup = false

    /**
     * Launches the Add Expense screen and, when an expense was actually saved
     * (RESULT_OK), confirms it on the dashboard with a Snackbar. The balance,
     * totals and recent transactions refresh automatically because they observe
     * the Room database.
     */
    private val addExpenseLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Snackbar.make(binding.root, R.string.expense_added_success, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupClickListeners()
        setupBottomNavigation()
        observeData()
        UiAnimations.pressFeedback(binding.fabAddExpense)
    }

    /**
     * Sets up click listeners, including the FAB to navigate to AddExpenseActivity,
     * transaction item taps to open ExpenseDetailActivity,
     * and the View All link to open ExpenseHistoryActivity.
     */
    private fun setupClickListeners() {
        binding.fabAddExpense.setOnClickListener {
            addExpenseLauncher.launch(Intent(this, AddExpenseActivity::class.java))
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
     * Sets up the RecyclerView once. List updates go through [ListAdapter.submitList]
     * so the adapter always reflects the current Room snapshot — never appends.
     */
    private fun setupRecyclerView() {
        adapter = TransactionAdapter { transaction ->
            startActivity(getExpenseDetailIntent(transaction))
        }
        binding.rvRecentTransactions.adapter = adapter
        // Avoid stacking DiffUtil insert animations on top of item entrance fades.
        binding.rvRecentTransactions.itemAnimator = null

        // Add subtle divider between items
        binding.rvRecentTransactions.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
                setDrawable(resources.getDrawable(R.drawable.divider_transaction, theme))
            }
        )
    }

    /**
     * Observes the ViewModels and updates the dashboard with real database data.
     * Observers are registered once in [onCreate]; Room Flows push every change
     * so totals and recent transactions stay in sync after add / edit / delete.
     */
    private fun observeData() {
        // Recent transactions (top 5) from Room — replace the list, never append.
        viewModel.getRecentExpenses(5).observe(this) { transactions ->
            adapter.submitList(transactions.toList())
        }

        // Profile drives the first-launch gate, greeting, name and income.
        profileViewModel.profile.observe(this) { profile ->
            if (profile == null) {
                openFirstTimeSetup()
                return@observe
            }

            monthlyIncome = profile.monthlyIncome
            currencySymbol = CurrencyUtils.symbolFor(profile.currency)

            binding.tvGreeting.setText(greetingForCurrentTime())
            binding.tvUserName.text = profile.fullName
            binding.tvIncomeAmount.text = CurrencyUtils.format(monthlyIncome, currencySymbol)
            updateBalanceCard()
        }

        // Total expenses from Room (recomputed automatically after every change)
        viewModel.totalExpenses.observe(this) { total ->
            totalExpenses = total
            binding.tvExpensesAmount.text = CurrencyUtils.format(total, currencySymbol)
            updateBalanceCard()
        }
    }

    /**
     * Remaining Balance = Monthly Income − Total Expenses.
     * Always formats with the profile currency symbol, so either observer can
     * refresh the card independently without clobbering the currency.
     */
    private fun updateBalanceCard() {
        binding.tvBalanceAmount.text =
            CurrencyUtils.format(monthlyIncome - totalExpenses, currencySymbol)
    }

    /**
     * Returns the greeting string resource for the current device time:
     * morning (5–11), afternoon (12–16) and evening otherwise.
     */
    private fun greetingForCurrentTime(): Int {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> R.string.greeting_morning
            in 12..16 -> R.string.greeting_afternoon
            else -> R.string.greeting_evening
        }
    }

    /**
     * First launch: no profile exists yet, so redirect to the setup screen
     * and clear the back stack so the dashboard is not reachable without a
     * profile. The setup screen is only shown again after app data is cleared.
     */
    private fun openFirstTimeSetup() {
        if (isRedirectingToSetup) return
        isRedirectingToSetup = true
        val intent = Intent(this, SetupActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
        finishWithoutAnimation()
    }
}
