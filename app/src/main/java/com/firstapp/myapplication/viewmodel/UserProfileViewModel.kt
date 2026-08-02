package com.firstapp.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.firstapp.myapplication.database.AppDatabase
import com.firstapp.myapplication.database.entity.UserProfile
import com.firstapp.myapplication.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Exposes the user profile from the repository as [LiveData] so every screen
 * (Setup, Dashboard, Profile, Analytics) observes the same single profile.
 */
class UserProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserProfileRepository(
        AppDatabase.getInstance(application.applicationContext).userProfileDao()
    )

    /** The stored user profile, or null before the first-time setup is completed. */
    val profile: LiveData<UserProfile?> = repository.getProfile().asLiveData()

    // ---------- WRITE OPERATIONS ----------

    /** Creates the profile from the first-time setup screen. */
    fun createProfile(
        fullName: String,
        monthlyIncome: Double,
        currency: String,
        onComplete: (() -> Unit)? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(
                UserProfile(
                    fullName = fullName,
                    monthlyIncome = monthlyIncome,
                    currency = currency
                )
            )
            onComplete?.invoke()
        }
    }

    /** Updates the existing profile from the Profile & Settings screen. */
    fun updateProfile(profile: UserProfile, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(profile)
            onComplete?.invoke()
        }
    }
}
