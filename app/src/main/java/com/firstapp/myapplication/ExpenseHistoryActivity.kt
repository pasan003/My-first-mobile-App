package com.firstapp.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.firstapp.myapplication.databinding.ActivityExpenseHistoryBinding
import com.firstapp.myapplication.utils.AnalyticsPeriod
import com.firstapp.myapplication.utils.CurrencyUtils
import com.firstapp.myapplication.utils.DateUtils
import com.firstapp.myapplication.utils.UiAnimations
import com.firstapp.myapplication.viewmodel.CategoryViewModel
import com.firstapp.myapplication.viewmodel.ExpenseViewModel
import com.google.android.material.chip.Chip

/**
 * Activity that displays the full Expense History screen, backed by Room.
 *
 * - Summary card showing total expenses (this month) and transaction count
 * - Category filter chips generated from the Room categories table
 * - RecyclerView filtered via Room queries through [ExpenseViewModel.filteredExpenses]
 * - Empty state shown when there are no expenses for the active filter
 * - FAB navigates to the Add Expense screen
 */
class ExpenseHistoryActivity : BaseActivity() {

    private lateinit var binding: ActivityExpenseHistoryBinding
    private val viewModel: ExpenseViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()
    private lateinit var adapter: ExpenseHistoryAdapter

    /** Prevents chip rebuilds from re-applying an identical chip set. */
    private var lastCategorySignature: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupFab()
        setupFilterChips()
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
                binding.hsvFilterChips.smoothScrollTo(0, 0)
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
     * Sets up the RecyclerView with expenses loaded from Room.
     * Adapter and decoration are created once; list updates go through [ListAdapter.submitList].
     */
    private fun setupRecyclerView() {
        adapter = ExpenseHistoryAdapter { transaction ->
            startActivity(getExpenseDetailIntent(transaction))
        }
        binding.rvExpenseHistory.adapter = adapter
        // DiffUtil already drives list updates; a second item animator causes
        // cards to appear stacked / overlapping while entrance animations run.
        binding.rvExpenseHistory.itemAnimator = null

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
     * Builds category filter chips from Room and wires selection to the ViewModel.
     * Custom categories (e.g. "Pets") appear automatically when added.
     */
    private fun setupFilterChips() {
        categoryViewModel.categories.observe(this) { categories ->
            val signature = categories.joinToString(separator = "|") { "${it.id}:${it.name}" }
            if (signature == lastCategorySignature) return@observe
            lastCategorySignature = signature
            rebuildFilterChips(categories)
        }
    }

    /**
     * Rebuilds the chip row as: All + every category from the database.
     * Preserves the currently selected filter when that category still exists.
     */
    private fun rebuildFilterChips(categories: List<CategoryItem>) {
        val selectedId = viewModel.selectedCategoryId.value
        val chipGroup = binding.chipGroupFilters

        // Avoid firing the listener while chips are being recreated.
        chipGroup.setOnCheckedStateChangeListener(null)
        chipGroup.removeAllViews()

        chipGroup.addView(createFilterChip(getString(R.string.chip_all), categoryId = null))
        categories.forEach { category ->
            chipGroup.addView(
                createFilterChip(category.name, categoryId = category.id.toLong())
            )
        }

        val chips = (0 until chipGroup.childCount).map { chipGroup.getChildAt(it) as Chip }
        val chipToCheck = chips.firstOrNull { chipCategoryId(it) == selectedId } ?: chips.first()
        chipToCheck.isChecked = true

        // If the previously selected category was deleted, fall back to All.
        if (chipCategoryId(chipToCheck) != selectedId) {
            viewModel.selectCategoryFilter(null)
        }

        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val checkedId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            val chip = group.findViewById<Chip>(checkedId) ?: return@setOnCheckedStateChangeListener
            viewModel.selectCategoryFilter(chipCategoryId(chip))
        }
    }

    /**
     * Creates a Material Filter chip for [text].
     * [categoryId] is null for the "All" chip; otherwise it is the Room category id.
     */
    private fun createFilterChip(text: String, categoryId: Long?): Chip {
        val chip = layoutInflater.inflate(
            R.layout.item_filter_chip,
            binding.chipGroupFilters,
            false
        ) as Chip
        chip.text = text
        chip.tag = categoryId
        return chip
    }

    /** Reads the Room category id stored on a filter chip (`null` = All). */
    private fun chipCategoryId(chip: Chip): Long? = chip.tag as? Long

    /**
     * Observes the Room database and updates the filtered list + summary card.
     * Observers are registered once in [onCreate].
     */
    private fun observeData() {
        viewModel.filteredExpenses.observe(this) { transactions ->
            // Defensive copy so ListAdapter always receives a new list instance.
            adapter.submitList(transactions.toList())
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
        binding.rvExpenseHistory.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
}
