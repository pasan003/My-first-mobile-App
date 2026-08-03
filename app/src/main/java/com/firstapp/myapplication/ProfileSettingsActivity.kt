package com.firstapp.myapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.firstapp.myapplication.databinding.ActivityProfileSettingsBinding
import com.firstapp.myapplication.databinding.DialogEditProfileBinding
import com.firstapp.myapplication.database.entity.UserProfile
import com.firstapp.myapplication.utils.CurrencyUtils
import com.firstapp.myapplication.utils.DateUtils
import com.firstapp.myapplication.viewmodel.UserProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Profile & Settings screen backed by the Room-stored user profile.
 *
 * - Displays the real profile (avatar, full name, monthly income, currency,
 *   member-since date) loaded from the database.
 * - "Edit Profile" opens a dialog to update name, monthly income and currency.
 * - Includes About App, Privacy Policy and Help & Support sections.
 * - Logout is disabled because this is an offline application.
 *
 * Any change made here is reflected automatically on the Dashboard and
 * Analytics because those screens observe the same Room profile.
 */
class ProfileSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSettingsBinding
    private val viewModel: UserProfileViewModel by viewModels()

    /** The profile currently displayed; used as the base for edits. */
    private var currentProfile: UserProfile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupSettingRows()
        setupClickListeners()
        observeProfile()
    }

    /**
     * Sets up the toolbar with back navigation.
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_profile_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_more_options -> {
                showPlaceholderToast(getString(R.string.cd_more_options))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Shows a short toast as a placeholder for future functionality.
     */
    private fun showPlaceholderToast(action: String) {
        Toast.makeText(
            this,
            getString(R.string.sample_toast_placeholder, action),
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Configures the Application Settings rows with icons, titles, and descriptions.
     */
    private fun setupSettingRows() {
        // Notifications
        binding.rowNotifications.ivRowIcon.setImageResource(R.drawable.ic_notifications)
        binding.rowNotifications.tvRowTitle.setText(R.string.profile_notifications)
        binding.rowNotifications.tvRowDescription.setText(R.string.profile_notifications_desc)
        binding.rowNotifications.tvRowDescription.visibility = View.VISIBLE

        // Language
        binding.rowLanguage.ivRowIcon.setImageResource(R.drawable.ic_language)
        binding.rowLanguage.tvRowTitle.setText(R.string.profile_language)
        binding.rowLanguage.tvRowDescription.setText(R.string.profile_language_desc)
        binding.rowLanguage.tvRowDescription.visibility = View.VISIBLE

        // Date Format
        binding.rowDateFormat.ivRowIcon.setImageResource(R.drawable.ic_calendar)
        binding.rowDateFormat.tvRowTitle.setText(R.string.profile_date_format)
        binding.rowDateFormat.tvRowDescription.setText(R.string.profile_date_format_desc)
        binding.rowDateFormat.tvRowDescription.visibility = View.VISIBLE

        // Currency Format
        binding.rowCurrencyFormat.ivRowIcon.setImageResource(R.drawable.ic_payment)
        binding.rowCurrencyFormat.tvRowTitle.setText(R.string.profile_currency_format)
        binding.rowCurrencyFormat.tvRowDescription.setText(R.string.profile_currency_format_desc)
        binding.rowCurrencyFormat.tvRowDescription.visibility = View.VISIBLE

        // Help Center
        binding.rowHelpCenter.ivRowIcon.setImageResource(R.drawable.ic_category_outline)
        binding.rowHelpCenter.tvRowTitle.setText(R.string.profile_help_center)

        // Contact Support
        binding.rowContactSupport.ivRowIcon.setImageResource(R.drawable.ic_profile)
        binding.rowContactSupport.ivRowIcon.setColorFilter(
            ContextCompat.getColor(this, R.color.finance_primary)
        )
        binding.rowContactSupport.tvRowTitle.setText(R.string.profile_contact_support)

        // Privacy Policy
        binding.rowPrivacyPolicy.ivRowIcon.setImageResource(R.drawable.ic_save)
        binding.rowPrivacyPolicy.tvRowTitle.setText(R.string.profile_privacy_policy)

        // Terms & Conditions
        binding.rowTermsConditions.ivRowIcon.setImageResource(R.drawable.ic_notes)
        binding.rowTermsConditions.tvRowTitle.setText(R.string.profile_terms_conditions)
    }

    /**
     * Observes the profile from Room and updates the screen whenever it changes.
     */
    private fun observeProfile() {
        viewModel.profile.observe(this) { profile ->
            if (profile == null) return@observe

            currentProfile = profile
            val symbol = CurrencyUtils.symbolFor(profile.currency)

            binding.tvUserName.text = profile.fullName
            binding.tvIncomeValue.text = getString(
                R.string.profile_income_value,
                CurrencyUtils.format(profile.monthlyIncome, symbol)
            )
            binding.tvMemberSince.text = getString(
                R.string.profile_member_since_format,
                DateUtils.formatDate(profile.createdAt)
            )
            binding.tvCurrencyValue.text = CurrencyUtils.currencyDisplay(profile.currency)
            binding.tvBudgetValue.text = CurrencyUtils.format(profile.monthlyIncome, symbol)
        }
    }

    /**
     * Sets up click listeners: edit profile, application setting rows and
     * the About / Privacy / Help & Support rows.
     */
    private fun setupClickListeners() {
        // Edit Profile — via the button or the pencil icon in the header
        binding.btnEditProfile.setOnClickListener {
            currentProfile?.let { showEditProfileDialog(it) }
        }
        binding.ivEditProfile.setOnClickListener {
            currentProfile?.let { showEditProfileDialog(it) }
        }

        // Application settings — UI placeholders (rows are <include> layouts,
        // so the click listener goes on the included root view)
        binding.rowNotifications.root.setOnClickListener {
            showPlaceholderToast(getString(R.string.profile_notifications))
        }
        binding.rowLanguage.root.setOnClickListener {
            showPlaceholderToast(getString(R.string.profile_language))
        }
        binding.rowDateFormat.root.setOnClickListener {
            showPlaceholderToast(getString(R.string.profile_date_format))
        }
        binding.rowCurrencyFormat.root.setOnClickListener {
            showPlaceholderToast(getString(R.string.profile_currency_format))
        }

        // Help & Support
        binding.rowHelpCenter.root.setOnClickListener {
            showPlaceholderToast(getString(R.string.profile_help_center))
        }
        binding.rowContactSupport.root.setOnClickListener {
            showPlaceholderToast(getString(R.string.profile_contact_support))
        }
        binding.rowPrivacyPolicy.root.setOnClickListener {
            showPrivacyDialog()
        }
        binding.rowTermsConditions.root.setOnClickListener {
            showAboutDialog()
        }

        // Logout — disabled in this offline app
        binding.btnLogout.setOnClickListener {
            Toast.makeText(this, R.string.feature_coming_soon, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Opens the Edit Profile dialog pre-filled with the current profile.
     * Saves changes back into the Room database on "Save".
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

            viewModel.updateProfile(
                profile.copy(
                    fullName = name,
                    monthlyIncome = income!!,
                    currency = currency.ifEmpty { "LKR" }
                )
            ) {
                runOnUiThread {
                    Toast.makeText(this, R.string.profile_updated, Toast.LENGTH_SHORT).show()
                }
            }
            dialog.dismiss()
        }

        dialog.show()
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
