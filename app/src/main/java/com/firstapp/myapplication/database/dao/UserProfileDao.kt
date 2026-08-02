package com.firstapp.myapplication.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.firstapp.myapplication.database.entity.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the [UserProfile] entity.
 *
 * Because only one profile is ever stored, [getProfile] always returns the
 * most recently created row (which is the only row).
 */
@Dao
interface UserProfileDao {

    // ---------- READ ----------

    /** The single stored profile, or null before the first-time setup. */
    @Query("SELECT * FROM user_profiles ORDER BY id DESC LIMIT 1")
    fun getProfile(): Flow<UserProfile?>

    // ---------- CREATE ----------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: UserProfile): Long

    // ---------- UPDATE ----------

    @Update
    suspend fun update(profile: UserProfile)

    // ---------- DELETE ----------

    /** Deletes all profiles (used only when clearing app data). */
    @Query("DELETE FROM user_profiles")
    suspend fun deleteAll()
}
