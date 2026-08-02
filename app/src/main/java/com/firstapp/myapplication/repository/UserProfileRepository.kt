package com.firstapp.myapplication.repository

import com.firstapp.myapplication.database.dao.UserProfileDao
import com.firstapp.myapplication.database.entity.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Repository that hides the [UserProfileDao] implementation details from the
 * ViewModels and Activities. All profile access goes through this class.
 */
class UserProfileRepository(private val userProfileDao: UserProfileDao) {

    // ---------- READ ----------

    fun getProfile(): Flow<UserProfile?> = userProfileDao.getProfile()

    // ---------- CREATE ----------

    suspend fun insert(profile: UserProfile): Long = userProfileDao.insert(profile)

    // ---------- UPDATE ----------

    suspend fun update(profile: UserProfile) = userProfileDao.update(profile)

    // ---------- DELETE ----------

    suspend fun deleteAll() = userProfileDao.deleteAll()
}
