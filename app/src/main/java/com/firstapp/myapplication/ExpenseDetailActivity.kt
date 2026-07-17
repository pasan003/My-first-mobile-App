package com.firstapp.myapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firstapp.myapplication.databinding.ActivityExpenseDetailBinding
import java.text.NumberFormat

/**
 * Activity that displays the details of a single expense.
 *
 * Accepts transaction data via Intent extras (keys defined below).
 * Falls back to sample data when extras are not provided.
 * Prepared for future Room Database integration — each data field
 * has a dedicated view ID that can be populated from a Transaction object.
 */
class ExpenseDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseDetailBinding

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_AMOUNT = "extra_amount"
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_DATE = "extra_date"
        const val EXTRA_ICON_RES_ID = "extra_icon_res_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        populateData()
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
                showPlaceholderToast(getString(R.string.edit_expense))
                true
            }
            R.id.action_delete -> {
                showPlaceholderToast(getString(R.string.delete_expense))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Populates all views with data from the incoming Intent (if available)
     * or falls back to sample/placeholder values.
     */
    private fun populateData() {
        val title = intent.getStringExtra(EXTRA_TITLE) ?: getString(R.string.sample_detail_title)
        val amount = intent.getDoubleExtra(EXTRA_AMOUNT, -1.0)
        val category = intent.getStringExtra(EXTRA_CATEGORY) ?: getString(R.string.sample_detail_category)
        val date = intent.getStringExtra(EXTRA_DATE) ?: getString(R.string.sample_detail_date)
        val iconResId = intent.getIntExtra(EXTRA_ICON_RES_ID, -1)

        // Format the amount as currency, or use the sample string as fallback
        val formattedAmount = if (amount >= 0) {
            "Rs. ${String.format("%,.2f", amount)}"
        } else {
            getString(R.string.sample_detail_amount)
        }

        binding.tvDetailTitle.text = title
        binding.tvDetailAmount.text = formattedAmount
        binding.tvDetailDate.text = date

        binding.chipDetailCategory.text = category

        // Dynamically set the category chip icon if one was passed
        if (iconResId != -1) {
            binding.chipDetailCategory.setChipIconResource(iconResId)
        }

        binding.tvInfoCategory.text = category
        binding.tvInfoPayment.text = getString(R.string.sample_detail_payment)
        binding.tvInfoDate.text = date
        binding.tvInfoTime.text = getString(R.string.sample_detail_time)
        binding.tvInfoNotes.text = getString(R.string.sample_detail_notes)
    }

    /**
     * Shows a short toast as a placeholder for future CRUD operations.
     */
    private fun showPlaceholderToast(action: String) {
        Toast.makeText(
            this,
            getString(R.string.sample_toast_placeholder, action),
            Toast.LENGTH_SHORT
        ).show()
    }
}
