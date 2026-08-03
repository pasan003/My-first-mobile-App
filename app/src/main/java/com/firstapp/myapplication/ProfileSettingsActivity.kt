package com.firstapp.myapplication

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.firstapp.myapplication.databinding.ActivityProfileSettingsBinding
import com.firstapp.myapplication.databinding.DialogEditProfileBinding
import com.firstapp.myapplication.database.entity.UserProfile
import com.firstapp.myapplication.utils.CurrencyUtils
import com.firstapp.myapplication.utils.DateUtils
import com.firstapp.myapplication.utils.UiAnimations
import com.firstapp.myapplication.viewmodel.CategoryViewModel
import com.firstapp.myapplication.viewmodel.ExpenseViewModel
import com.firstapp.myapplication.viewmodel.UserProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/**
 * Profile & Settings screen backed by the Room-stored user profile.
 *
 * - **Profile header**: default avatar, full name, monthly income and the
 *   member-since date (the profile creation date stored in Room).
 * - **Financial summary**: monthly income, total expenses, remaining balance
 *   and total transactions — all observed from Room, so every card updates
 *   automatically when expenses or the profile change.
 * - **Edit Profile**: a dialog lets the user update full name, monthly income
 *   and currency, saving straight into the Room database.
 * - **Application settings**: Currency, About SpendWise, Help & Support,
 *   Privacy Policy and App Version. Logout is intentionally not present
 *   because the app works completely offline.
 * - **Storage information**: total categories, total expenses and the local
 *   database status, all read from Room.
 */
class ProfileSettingsActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileSettingsBinding

    private val profileViewModel: UserProfileViewModel by viewModels()
    private val expenseViewModel: ExpenseViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()

    /** The profile currently displayed; used as the base for edits. */
    private var currentProfile: UserProfile? = null

    /** Latest total expenses, used to compute the remaining balance. */
    private var totalExpenses = 0.0

    /** Currency symbol from the profile, used for all formatted amounts. */
    private var currencySymbol = CurrencyUtils.DEFAULT_SYMBOL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupSettingRows()
        setupClickListeners()
        observeData()
        UiAnimations.pressFeedback(binding.btnEditProfile)
    }

    /**
     * Sets up the toolbar with back navigation.
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Configures the Application Settings rows with icons, titles and values.
     */
    private fun setupSettingRows() {
        // Currency — the stored value is shown and edited from the profile dialog
        binding.rowCurrency.ivRowIcon.setImageResource(R.drawable.ic_payment)
        binding.rowCurrency.tvRowTitle.setText(R.string.profile_currency)

        // About SpendWise
        binding.rowAbout.ivRowIcon.setImageResource(R.drawable.ic_info)
        binding.rowAbout.tvRowTitle.setText(R.string.profile_about_spendwise)

        // Help & Support
        binding.rowHelpSupport.ivRowIcon.setImageResource(R.drawable.ic_help)
        binding.rowHelpSupport.tvRowTitle.setText(R.string.profile_help_support)

        // Privacy Policy
        binding.rowPrivacyPolicy.ivRowIcon.setImageResource(R.drawable.ic_shield)
        binding.rowPrivacyPolicy.tvRowTitle.setText(R.string.profile_privacy_policy)

        // App Version — display-only row with the version as its value
        binding.rowAppVersion.ivRowIcon.setImageResource(R.drawable.ic_more_vert)
        binding.rowAppVersion.tvRowTitle.setText(R.string.profile_app_version)
        binding.rowAppVersion.tvRowDescription.setText(R.string.profile_version_value)
        binding.rowAppVersion.tvRowDescription.visibility = View.VISIBLE
        binding.rowAppVersion.root.apply {
            // Informational row — remove the clickable ripple affordance
            isClickable = false
            isFocusable = false
            background = null
        }
    }

    /**
     * Observes the profile, expenses and categories from Room and updates the
     * whole screen automatically whenever any of them changes.
     */
    private fun observeData() {
        profileViewModel.profile.observe(this) { profile ->
            if (profile == null) return@observe

            currentProfile = profile
            currencySymbol = CurrencyUtils.symbolFor(profile.currency)

            binding.tvUserName.text = profile.fullName
            binding.tvIncomeValue.text = getString(
                R.string.profile_income_value,
                CurrencyUtils.format(profile.monthlyIncome, currencySymbol)
            )
            binding.tvMemberSince.text = getString(
                R.string.profile_member_since_format,
                DateUtils.formatDate(profile.createdAt)
            )

            // Currency row shows the friendly display name of the stored code
            binding.rowCurrency.tvRowDescription.text =
                CurrencyUtils.currencyDisplay(profile.currency)
            binding.rowCurrency.tvRowDescription.visibility = View.VISIBLE

            updateSummary()
        }

        // Total expenses drive the summary card and refresh automatically
        expenseViewModel.totalExpenses.observe(this) { total ->
            totalExpenses = total
            updateSummary()
        }

        // Transaction count (summary card + storage info)
        expenseViewModel.expenseCount.observe(this) { count ->
            binding.tvSummaryTransactions.text = count.toString()
            binding.tvStorageExpenses.text = count.toString()
        }

        // Category count (storage info)
        categoryViewModel.categoryCount.observe(this) { count ->
            binding.tvStorageCategories.text = count.toString()
        }
    }

    /**
     * Recomputes the financial summary cards: income, expenses and the
     * remaining balance (income − expenses).
     */
    private fun updateSummary() {
        val income = currentProfile?.monthlyIncome ?: 0.0
        binding.tvSummaryIncome.text = CurrencyUtils.format(income, currencySymbol)
        binding.tvSummaryExpenses.text = CurrencyUtils.format(totalExpenses, currencySymbol)
        binding.tvSummaryBalance.text =
            CurrencyUtils.format(income - totalExpenses, currencySymbol)
    }

    /**
     * Sets up click listeners: edit profile (button, pencil icon and the
     * Currency row) plus the About / Help / Privacy rows.
     */
    private fun setupClickListeners() {
        // Edit Profile — via the button, the pencil icon or the Currency row
        binding.btnEditProfile.setOnClickListener {
            currentProfile?.let { showEditProfileDialog(it) }
        }
        binding.ivEditProfile.setOnClickListener {
            currentProfile?.let { showEditProfileDialog(it) }
        }
        binding.rowCurrency.root.setOnClickListener {
            currentProfile?.let { showEditProfileDialog(it) }
        }

        // About SpendWise
        binding.rowAbout.root.setOnClickListener {
            showAboutDialog()
        }

        // Help & Support
        binding.rowHelpSupport.root.setOnClickListener {
            showHelpDialog()
        }

        // Privacy Policy
        binding.rowPrivacyPolicy.root.setOnClickListener {
            showPrivacyDialog()
        }

        // App Version row is informational — no action needed
    }

    /**
     * Opens the Edit Profile dialog pre-filled with the current profile.
     * Saves changes back into the Room database on "Save"; every other screen
     * that observes the profile (Dashboard, Analytics) updates automatically.
     */
    private fun showEditProfileDialog(profile: UserProfile) {
        val dialogBinding = DialogEditProfileBinding.inflate(layoutInflater)

        // Pre-fill
        dialogBinding.etFullName.setText(profile.fullName)
        dialogBinding.etMonthlyIncome.setText(
            if (profile.monthlyIncome == profile.monthlyIncome.toLong().toDouble()) {
                profile.monthlyIncome.toLong().toString()
            } else {
                profile.monthlyIncome.toString()
            }
        )

        // Currency dropdown
        val currencies = resources.getStringArray(R.array.currencies)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            currencies
        )
        dialogBinding.actvCurrency.setAdapter(adapter)
        dialogBinding.actvCurrency.setText(profile.currency, false)

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnSave.setOnClickListener {
            val name = dialogBinding.etFullName.text?.toString()?.trim().orEmpty()
            val incomeText = dialogBinding.etMonthlyIncome.text?.toString()?.trim().orEmpty()
            val currency = dialogBinding.actvCurrency.text?.toString()?.trim().orEmpty()

            // Validate the same way as the first-time setup
            var valid = true
            if (name.isEmpty()) {
                dialogBinding.tilFullName.error = getString(R.string.setup_error_name)
                valid = false
            } else {
                dialogBinding.tilFullName.error = null
            }

            val income = incomeText.toDoubleOrNull()
            if (incomeText.isEmpty() || income == null || income <= 0) {
                dialogBinding.tilMonthlyIncome.error = getString(R.string.setup_error_income)
                valid = false
            } else {
                dialogBinding.tilMonthlyIncome.error = null
            }

            if (!valid) return@setOnClickListener

            profileViewModel.updateProfile(
                profile.copy(
                    fullName = name,
                    monthlyIncome = income!!,
                    currency = currency.ifEmpty { "LKR" }
                )
            ) {
                runOnUiThread {
                    Snackbar.make(binding.root, R.string.profile_updated, Snackbar.LENGTH_SHORT).show()
                }
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * Shows the Help & Support dialog (offline app — no external links).
     */
    private fun showHelpDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.profile_help_support)
            .setMessage(R.string.profile_help_support_message)
            .setPositiveButton(R.string.action_ok, null)
            .show()
    }

    /**
     * Shows the Privacy Policy dialog.
     */
    private fun showPrivacyDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.profile_privacy_policy)
            .setMessage(R.string.privacy_policy_message)
            .setPositiveButton(R.string.action_ok, null)
            .show()
    }

    /**
     * Shows the About SpendWise dialog.
     */
    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.profile_about_title)
            .setMessage(
                getString(
                    R.string.about_app_message,
                    getString(R.string.profile_version_value),
                    getString(R.string.profile_technology_value)
                )
            )
            .setPositiveButton(R.string.action_ok, null)
            .show()
    }
}
