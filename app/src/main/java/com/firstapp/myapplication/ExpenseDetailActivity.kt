package com.firstapp.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import com.firstapp.myapplication.databinding.ActivityExpenseDetailBinding
import com.firstapp.myapplication.database.relation.ExpenseWithCategory
import com.firstapp.myapplication.utils.CategoryVisuals
import com.firstapp.myapplication.utils.CurrencyUtils
import com.firstapp.myapplication.utils.DateUtils
import com.firstapp.myapplication.utils.UiAnimations
import com.firstapp.myapplication.viewmodel.ExpenseViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/**
 * Displays the details of a single expense loaded from the Room database.
 *
 * The activity receives the database [EXTRA_EXPENSE_ID], loads the full
 * record (including category, notes, payment method and time) and supports
 * editing (via AddExpenseActivity) and deleting the expense.
 */
class ExpenseDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityExpenseDetailBinding
    private val viewModel: ExpenseViewModel by viewModels()

    private var expenseId: Long = 0L
    private var loadedExpense: ExpenseWithCategory? = null

    companion object {
        const val EXTRA_EXPENSE_ID = "extra_expense_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        expenseId = intent.getLongExtra(EXTRA_EXPENSE_ID, 0L)

        setupToolbar()
        setupButtons()
        observeExpense()
        UiAnimations.pressFeedback(binding.btnEditExpense)
        UiAnimations.pressFeedback(binding.btnDeleteExpense)
    }

    /**
     * Sets up the toolbar with back navigation and overflow menu.
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_expense_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_edit -> {
                openEditScreen()
                true
            }
            R.id.action_delete -> {
                confirmDelete()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Loads the expense from Room by its id and populates the views.
     */
    private fun observeExpense() {
        if (expenseId <= 0L) {
            Toast.makeText(this, R.string.error_expense_not_found, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel.getExpenseWithCategory(expenseId).observe(this) { item ->
            if (item == null) {
                Toast.makeText(this, R.string.error_expense_not_found, Toast.LENGTH_SHORT).show()
                finish()
                return@observe
            }
            loadedExpense = item
            populateViews(item)
        }
    }

    /**
     * Populates all views from the loaded expense + category.
     */
    private fun populateViews(item: ExpenseWithCategory) {
        val expense = item.expense
        val category = item.category

        binding.tvDetailTitle.text = expense.title
        binding.tvDetailAmount.text = CurrencyUtils.format(expense.amount)
        binding.tvDetailDate.text = DateUtils.formatDate(expense.transactionDate)

        binding.chipDetailCategory.text = category?.name ?: getString(R.string.cat_other)
        binding.chipDetailCategory.setChipIconResource(
            CategoryVisuals.iconResId(category?.icon ?: "ic_category_outline")
        )

        binding.tvInfoCategory.text = category?.name ?: getString(R.string.cat_other)
        binding.tvInfoPayment.text = expense.paymentMethod.ifEmpty { getString(R.string.not_specified) }
        binding.tvInfoDate.text = DateUtils.formatDate(expense.transactionDate)
        binding.tvInfoTime.text = DateUtils.formatTime(expense.createdAt)
        binding.tvInfoNotes.text = expense.notes.ifEmpty { getString(R.string.not_specified) }
    }

    /**
     * Wires the Edit and Delete buttons.
     */
    private fun setupButtons() {
        binding.btnEditExpense.setOnClickListener {
            openEditScreen()
        }
        binding.btnDeleteExpense.setOnClickListener {
            confirmDelete()
        }
    }

    /**
     * Opens the Add Expense screen in edit mode for this expense.
     */
    private fun openEditScreen() {
        val intent = Intent(this, AddExpenseActivity::class.java).apply {
            putExtra(AddExpenseActivity.EXTRA_EXPENSE_ID, expenseId)
        }
        startActivity(intent)
    }

    /**
     * Shows a confirmation dialog and deletes the expense from Room.
     */
    private fun confirmDelete() {
        val expense = loadedExpense?.expense ?: return

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_expense)
            .setMessage(R.string.delete_expense_confirm_message)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.delete(expense) {
                    runOnUiThread {
                        Snackbar.make(
                            binding.root,
                            R.string.expense_deleted,
                            Snackbar.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
            .show()
    }
}
