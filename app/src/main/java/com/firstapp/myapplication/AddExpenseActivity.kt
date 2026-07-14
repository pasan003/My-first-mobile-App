package com.firstapp.myapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.firstapp.myapplication.databinding.ActivityAddExpenseBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupCategoryDropdown()
        setupPaymentMethodDropdown()
        setupDatePicker()
        setupClickListeners()
    }

    /**
     * Sets up the toolbar with back navigation and save menu.
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
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
     * Sets up the category exposed dropdown menu with string array resource.
     */
    private fun setupCategoryDropdown() {
        val categories = resources.getStringArray(R.array.expense_categories)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(adapter)
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
        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        binding.etDate.setText(dateFormat.format(Date()))

        binding.etDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.date_hint))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                val selectedDate = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
                    .format(Date(selection))
                binding.etDate.setText(selectedDate)
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
     * UI only — no database operations yet.
     */
    private fun setupClickListeners() {
        binding.btnSaveExpense.setOnClickListener {
            handleSave()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    /**
     * Placeholder save handler. Displays a snackbar with the entered data.
     * Will later be replaced with Room Database operations.
     */
    private fun handleSave() {
        val title = binding.tilExpenseTitle.editText?.text?.toString()
        val amount = binding.tilAmount.editText?.text?.toString()
        val category = binding.actvCategory.text.toString()
        val date = binding.etDate.text.toString()
        val paymentMethod = binding.actvPaymentMethod.text.toString()
        val notes = binding.tilNotes.editText?.text?.toString()

        // UI-only: show a snackbar with the captured data
        val summary = buildString {
            append(getString(R.string.save_expense))
            append(": ")
            if (title.isNullOrBlank()) {
                append(getString(R.string.error_title_required))
            } else {
                append(title)
                append(" - Rs.")
                append(amount ?: "0")
            }
        }

        Snackbar.make(binding.root, summary, Snackbar.LENGTH_SHORT).show()

        // Validation placeholders — toggle error visibility based on input
        binding.tvTitleError.visibility =
            if (title.isNullOrBlank()) android.view.View.VISIBLE else android.view.View.GONE
        binding.tvAmountError.visibility =
            if (amount.isNullOrBlank()) android.view.View.VISIBLE else android.view.View.GONE
        binding.tvCategoryError.visibility =
            if (category.isBlank()) android.view.View.VISIBLE else android.view.View.GONE
    }
}
