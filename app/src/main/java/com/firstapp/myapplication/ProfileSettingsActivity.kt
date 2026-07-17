package com.firstapp.myapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firstapp.myapplication.databinding.ActivityProfileSettingsBinding

/**
 * Activity that displays the Profile & Settings screen.
 *
 * Features (UI only — no functionality):
 * - Material Top App Bar with back arrow and more options menu
 * - Profile Header card with avatar, name, email, member since, edit icon
 * - Financial Preferences card (currency, monthly budget, budget reminder switch)
 * - Application Settings card (notifications, language, date format, currency format)
 * - Help & Support card (help center, contact support, privacy policy, terms)
 * - About SpendWise card (version, developer, technology)
 * - Logout section with outlined button
 *
 * All interactive elements are UI placeholders that show toast messages.
 * This screen is designed to later support:
 * - Profile editing (edit icon on profile header)
 * - Currency/Budget management (financial preferences rows)
 * - Notification preferences (application settings rows)
 * - Language/date format selection (application settings rows)
 * - Help links (help & support rows)
 * - Logout functionality
 *
 * @see AnalyticsActivity for the same UI-only pattern used throughout the app
 */
class ProfileSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupSettingRows()
        setupClickListeners()
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
        binding.rowNotifications.tvRowDescription.visibility = android.view.View.VISIBLE

        // Language
        binding.rowLanguage.ivRowIcon.setImageResource(R.drawable.ic_language)
        binding.rowLanguage.tvRowTitle.setText(R.string.profile_language)
        binding.rowLanguage.tvRowDescription.setText(R.string.profile_language_desc)
        binding.rowLanguage.tvRowDescription.visibility = android.view.View.VISIBLE

        // Date Format
        binding.rowDateFormat.ivRowIcon.setImageResource(R.drawable.ic_calendar)
        binding.rowDateFormat.tvRowTitle.setText(R.string.profile_date_format)
        binding.rowDateFormat.tvRowDescription.setText(R.string.profile_date_format_desc)
        binding.rowDateFormat.tvRowDescription.visibility = android.view.View.VISIBLE

        // Currency Format
        binding.rowCurrencyFormat.ivRowIcon.setImageResource(R.drawable.ic_payment)
        binding.rowCurrencyFormat.tvRowTitle.setText(R.string.profile_currency_format)
        binding.rowCurrencyFormat.tvRowDescription.setText(R.string.profile_currency_format_desc)
        binding.rowCurrencyFormat.tvRowDescription.visibility = android.view.View.VISIBLE

        // Help Center
        binding.rowHelpCenter.ivRowIcon.setImageResource(R.drawable.ic_category_outline)
        binding.rowHelpCenter.tvRowTitle.setText(R.string.profile_help_center)

        // Contact Support
        binding.rowContactSupport.ivRowIcon.setImageResource(R.drawable.ic_edit)
        binding.rowContactSupport.tvRowTitle.setText(R.string.profile_contact_support)

        // Privacy Policy
        binding.rowPrivacyPolicy.ivRowIcon.setImageResource(R.drawable.ic_save)
        binding.rowPrivacyPolicy.tvRowTitle.setText(R.string.profile_privacy_policy)

        // Terms & Conditions
        binding.rowTermsConditions.ivRowIcon.setImageResource(R.drawable.ic_notes)
        binding.rowTermsConditions.tvRowTitle.setText(R.string.profile_terms_conditions)
    }

    /**
     * Sets up click listeners for interactive elements.
     * Only the logout button shows a placeholder message.
     */
    private fun setupClickListeners() {
        // Logout — place holder for future implementation
        binding.btnLogout.setOnClickListener {
            Toast.makeText(
                this,
                getString(R.string.feature_coming_soon),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
