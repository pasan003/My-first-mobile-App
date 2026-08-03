package com.firstapp.myapplication.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.firstapp.myapplication.database.entity.Category
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the [Category] entity.
 */
@Dao
interface CategoryDao {

    // ---------- CREATE ----------

    @Insert
    suspend fun insert(category: Category): Long

    // ---------- READ ----------

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :id")
    fun getCategoryById(id: Long): Flow<Category?>

    @Query("SELECT COUNT(*) FROM categories")
    fun getCategoryCount(): Flow<Int>

    // ---------- UPDATE ----------

    @Update
    suspend fun update(category: Category)

    // ---------- DELETE ----------

    @Delete
    suspend fun delete(category: Category)

    /** Deletes all categories. Used by the seeded first-launch data if needed. */
    @Query("DELETE FROM categories")
    suspend fun deleteAll()
}
