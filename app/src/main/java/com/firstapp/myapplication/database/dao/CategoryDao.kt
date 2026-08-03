package com.firstapp.myapplication.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
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

    /** Number of categories that already use [name] (case-insensitive), excluding [excludeId]. */
    @Query("SELECT COUNT(*) FROM categories WHERE LOWER(name) = LOWER(:name) AND id != :excludeId")
    suspend fun countByName(name: String, excludeId: Long): Int

    /** Finds a category by its exact name, e.g. the "Other" fallback category. */
    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): Category?

    // ---------- UPDATE ----------

    @Update
    suspend fun update(category: Category)

    // ---------- DELETE ----------

    @Delete
    suspend fun delete(category: Category)

    /** Deletes all categories. Used by the seeded first-launch data if needed. */
    @Query("DELETE FROM categories")
    suspend fun deleteAll()

    /** Reassigns every expense from [sourceCategoryId] to [targetCategoryId]. */
    @Query("UPDATE expenses SET categoryId = :targetCategoryId WHERE categoryId = :sourceCategoryId")
    suspend fun moveExpenses(sourceCategoryId: Long, targetCategoryId: Long)

    @Query("DELETE FROM categories WHERE id = :categoryId")
    suspend fun deleteById(categoryId: Long)

    /**
     * Deletes a category and guarantees its expenses stay valid: every expense
     * is first moved to [targetCategoryId] and only then is the category
     * deleted — all inside one atomic Room transaction.
     */
    @Transaction
    suspend fun deleteCategoryAndMoveExpenses(categoryId: Long, targetCategoryId: Long) {
        moveExpenses(categoryId, targetCategoryId)
        deleteById(categoryId)
    }
}
