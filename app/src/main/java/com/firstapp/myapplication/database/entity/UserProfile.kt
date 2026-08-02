package com.firstapp.myapplication.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing the app's single user profile.
 *
 * Only one profile is ever stored (the setup screen creates it and the
 * Profile & Settings screen edits it).
 *
 * @param id Auto-generated primary key.
 * @param fullName The user's display name (e.g. "Pasan").
 * @param monthlyIncome Monthly income used by the dashboard balance card and analytics.
 * @param currency ISO-style currency code (e.g. "LKR") used to format amounts.
 * @param createdAt Epoch millis when the profile was created (first-time setup).
 */
@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val monthlyIncome: Double,
    val currency: String = "LKR",
    val createdAt: Long = System.currentTimeMillis()
)
