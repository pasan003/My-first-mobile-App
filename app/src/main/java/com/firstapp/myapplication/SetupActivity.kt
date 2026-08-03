package com.firstapp.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.firstapp.myapplication.databinding.ActivitySetupBinding
import com.firstapp.myapplication.utils.UiAnimations
import com.firstapp.myapplication.viewmodel.UserProfileViewModel

/**
 * First-time setup screen shown only when no user profile exists yet.
 *
 * Collects the user's full name and monthly income, saves the profile into
 * the Room database and navigates to the Dashboard. Because the profile is
 * stored persistently, this screen is never shown again unless the app data
 * is cleared (or the app is reinstalled).
 */
class SetupActivity : BaseActivity() {

    private lateinit var binding: ActivitySetupBinding
    private val viewModel: UserProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGetStarted.setOnClickListener {
            handleGetStarted()
        }
        UiAnimations.pressFeedback(binding.btnGetStarted)
    }

    /**
     * Validates the form and saves the profile. On success the app navigates
     * to the Dashboard and clears the back stack so the setup screen cannot
     * be returned to.
     */
    private fun handleGetStarted() {
        val name = binding.etFullName.text?.toString()?.trim().orEmpty()
        val incomeText = binding.etMonthlyIncome.text?.toString()?.trim().orEmpty()

        // Name cannot be empty
        var valid = true
        if (name.isEmpty()) {
            binding.tvNameError.visibility = View.VISIBLE
            valid = false
        } else {
            binding.tvNameError.visibility = View.GONE
        }

        // Monthly income must be greater than zero
        val income = incomeText.toDoubleOrNull()
        if (incomeText.isEmpty() || income == null || income <= 0) {
            binding.tvIncomeError.visibility = View.VISIBLE
            valid = false
        } else {
            binding.tvIncomeError.visibility = View.GONE
        }

        if (!valid) {
            Toast.makeText(
                this,
                R.string.error_fill_required_fields,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Prevent double taps while saving
        binding.btnGetStarted.isEnabled = false

        viewModel.createProfile(
            fullName = name,
            monthlyIncome = income!!,
            currency = "LKR"
        ) {
            runOnUiThread {
                val intent = Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                startActivity(intent)
                finishWithoutAnimation()
            }
        }
    }
}
