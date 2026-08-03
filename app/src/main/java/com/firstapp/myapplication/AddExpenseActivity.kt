package com.firstapp.myapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import com.firstapp.myapplication.databinding.ActivityAddExpenseBinding
import com.firstapp.myapplication.database.entity.Expense
import com.firstapp.myapplication.utils.DateUtils
import com.firstapp.myapplication.utils.UiAnimations
import com.firstapp.myapplication.viewmodel.CategoryViewModel
import com.firstapp.myapplication.viewmodel.ExpenseViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import java.util.Date
import java.util.TimeZone

class AddExpenseActivity : BaseActivity() {

    private lateinit var binding: ActivityAddExpenseBinding

    private val expenseViewModel: ExpenseViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()

    private var categoryItems: List<com.firstapp.myapplication.CategoryItem> = emptyList()
    private var selectedCategoryId: Long? = null
    private var selectedDateMillis: Long = System.currentTimeMillis()

    /** When non-null, this activity is editing an existing expense. */
    private var editingExpenseId: Long? = null

    /** Original createdAt of the expense being edited (preserved on update). */
    private var originalCreatedAt: Long = System.currentTimeMillis()

    /** Guards against double-tapping Save, which would insert duplicate rows. */
    private var isSaving = false

    companion object {
        const val EXTRA_EXPENSE_ID = "extra_expense_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editingExpenseId = intent.getLongExtra(EXTRA_EXPENSE_ID, -1L).takeIf { it > 0 }

        setupToolbar()
        setupPaymentMethodDropdown()
        setupDatePicker()
        setupClickListeners()
        observeCategories()
        UiAnimations.pressFeedback(binding.btnSaveExpense)

        if (editingExpenseId != null) {
            setupEditMode()
        }
    }

    /**
     * Sets up the toolbar with back navigation and save menu.
     * In edit mode the title becomes "Edit Expense".
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.title = if (editingExpenseId != null) {
            getString(R.string.edit_expense)
        } else {
            getString(R.string.add_expense)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_expense, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_save -> {
                handleSave()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Loads the categories from Room and populates the dropdown.
     */
    private fun observeCategories() {
        categoryViewModel.categories.observe(this) { items ->
            categoryItems = items
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                items.map { it.name }
            )
            binding.actvCategory.setAdapter(adapter)
        }
    }

    /**
     * Sets up the payment method exposed dropdown menu with string array resource.
     */
    private fun setupPaymentMethodDropdown() {
        val methods = resources.getStringArray(R.array.payment_methods)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, methods)
        binding.actvPaymentMethod.setAdapter(adapter)
    }

    /**
     * Sets up the date picker with today's date as default.
     * Shows MaterialDatePicker dialog when the date field is clicked.
     */
    private fun setupDatePicker() {
        // Set today's date as default
        binding.etDate.setText(DateUtils.formatShortDate(selectedDateMillis))

        binding.etDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.date_hint))
                .setSelection(selectedDateMillis)
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                // MaterialDatePicker returns the selected day as UTC midnight;
                // shift it to local midnight so date display and analytics
                // ranges match in any timezone.
                selectedDateMillis = selection + TimeZone.getDefault().getOffset(selection)
                binding.etDate.setText(DateUtils.formatShortDate(selectedDateMillis))
            }

            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        // Also allow clicking the calendar icon
        binding.tilDate.setEndIconOnClickListener {
            binding.etDate.performClick()
        }
    }

    /**
     * Sets up click listeners for the Save and Cancel buttons.
     */
    private fun setupClickListeners() {
        binding.btnSaveExpense.setOnClickListener {
            handleSave()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.actvCategory.setOnItemClickListener { _, _, position, _ ->
            val item = binding.actvCategory.adapter.getItem(position) as String
            selectedCategoryId = categoryItems.firstOrNull { it.name == item }?.id?.toLong()
        }
    }

    /**
     * Pre-fills the form when editing an existing expense.
     */
    private fun setupEditMode() {
        expenseViewModel.getExpenseWithCategory(editingExpenseId!!).observe(this) { item ->
            item ?: return@observe
            binding.etExpenseTitle.setText(item.expense.title)
            binding.etAmount.setText(if (item.expense.amount == item.expense.amount.toLong().toDouble()) {
                item.expense.amount.toLong().toString()
            } else {
                item.expense.amount.toString()
            })
            binding.actvCategory.setText(item.category?.name ?: "")
            binding.actvPaymentMethod.setText(item.expense.paymentMethod)
            binding.etNotes.setText(item.expense.notes)
            selectedDateMillis = item.expense.transactionDate
            selectedCategoryId = item.expense.categoryId
            binding.etDate.setText(DateUtils.formatShortDate(item.expense.transactionDate))
            originalCreatedAt = item.expense.createdAt
        }
    }

    /**
     * Validates the form and saves the expense to the Room database.
     * Shows a success message and returns to the previous screen.
     * Concurrent taps are ignored so a single save cannot insert duplicate rows.
     */
    private fun handleSave() {
        if (isSaving) return

        val title = binding.etExpenseTitle.text?.toString()?.trim().orEmpty()
        val amountText = binding.etAmount.text?.toString()?.trim().orEmpty()
        val categoryName = binding.actvCategory.text?.toString()?.trim().orEmpty()
        val paymentMethod = binding.actvPaymentMethod.text?.toString()?.trim().orEmpty()
        val notes = binding.etNotes.text?.toString()?.trim().orEmpty()

        // Validate required fields
        var valid = true

        if (title.isEmpty()) {
            binding.tvTitleError.visibility = android.view.View.VISIBLE
            valid = false
        } else {
            binding.tvTitleError.visibility = android.view.View.GONE
        }

        val amount = amountText.toDoubleOrNull()
        if (amountText.isEmpty() || amount == null || amount <= 0) {
            binding.tvAmountError.visibility = android.view.View.VISIBLE
            valid = false
        } else {
            binding.tvAmountError.visibility = android.view.View.GONE
        }

        if (categoryName.isEmpty()) {
            binding.tvCategoryError.visibility = android.view.View.VISIBLE
            valid = false
        } else {
            binding.tvCategoryError.visibility = android.view.View.GONE
        }

        if (!valid) {
            Snackbar.make(
                binding.root,
                getString(R.string.error_fill_required_fields),
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        val categoryId = selectedCategoryId
            ?: categoryItems.firstOrNull { it.name == categoryName }?.id?.toLong()
            ?: run {
                Toast.makeText(this, R.string.error_select_valid_category, Toast.LENGTH_SHORT).show()
                return
            }

        isSaving = true
        binding.btnSaveExpense.isEnabled = false

        if (editingExpenseId != null) {
            // Update existing expense
            val existing = Expense(
                id = editingExpenseId!!,
                title = title,
                amount = amount!!,
                categoryId = categoryId,
                notes = notes,
                paymentMethod = paymentMethod,
                transactionDate = selectedDateMillis,
                createdAt = originalCreatedAt
            )
            expenseViewModel.update(existing) {
                runOnUiThread {
                    setResult(RESULT_OK)
                    Snackbar.make(binding.root, getString(R.string.expense_updated), Snackbar.LENGTH_SHORT).show()
                    binding.root.postDelayed({ finish() }, 500)
                }
            }
        } else {
            // Insert new expense
            val expense = Expense(
                title = title,
                amount = amount!!,
                categoryId = categoryId,
                notes = notes,
                paymentMethod = paymentMethod,
                transactionDate = selectedDateMillis
            )
            expenseViewModel.insert(expense) {
                runOnUiThread {
                    setResult(RESULT_OK)
                    Snackbar.make(binding.root, getString(R.string.expense_saved), Snackbar.LENGTH_SHORT).show()
                    binding.root.postDelayed({ finish() }, 500)
                }
            }
        }
    }
}
