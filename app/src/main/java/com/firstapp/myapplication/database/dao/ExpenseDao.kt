package com.firstapp.myapplication.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.firstapp.myapplication.database.entity.Expense
import com.firstapp.myapplication.database.relation.CategoryStats
import com.firstapp.myapplication.database.relation.ExpenseWithCategory
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the [Expense] entity.
 *
 * Exposes reactive [Flow] queries so the UI can observe changes automatically
 * and suspend functions for all CRUD write operations.
 */
@Dao
interface ExpenseDao {

    // ---------- CREATE ----------

    @Insert
    suspend fun insert(expense: Expense): Long

    // ---------- READ ----------

    @Transaction
    @Query("SELECT * FROM expenses ORDER BY transactionDate DESC, id DESC")
    fun getAllExpenses(): Flow<List<ExpenseWithCategory>>

    @Transaction
    @Query("SELECT * FROM expenses WHERE id = :id")
    fun getExpenseById(id: Long): Flow<ExpenseWithCategory?>

    @Transaction
    @Query("SELECT * FROM expenses ORDER BY transactionDate DESC, id DESC LIMIT :limit")
    fun getRecentExpenses(limit: Int): Flow<List<ExpenseWithCategory>>

    /** Total amount of all expenses. */
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses")
    fun getTotalExpenses(): Flow<Double>

    /**
     * Aggregated transaction count and total amount per category.
     * Categories with no expenses simply have no row (treated as 0 / 0.0).
     */
    @Query(
        "SELECT categoryId AS categoryId, " +
            "COUNT(*) AS transactionCount, " +
            "COALESCE(SUM(amount), 0.0) AS totalAmount " +
            "FROM expenses GROUP BY categoryId"
    )
    fun getCategoryStats(): Flow<List<CategoryStats>>

    /** Total amount of expenses recorded between two timestamps (inclusive start, exclusive end). */
    @Query(
        "SELECT COALESCE(SUM(amount), 0.0) FROM expenses " +
            "WHERE transactionDate >= :startDate AND transactionDate < :endDate"
    )
    fun getMonthlyExpenses(startDate: Long, endDate: Long): Flow<Double>

    /**
     * Expenses whose title, notes or category name contains [query]
     * (case-insensitive, matched by SQLite itself so the whole table is
     * never loaded into memory), optionally narrowed to a single category
     * via [categoryId] (`null` = all categories).
     *
     * An empty [query] matches every row, so clearing the search text
     * naturally restores the plain category-filtered list.
     *
     * The caller is expected to pre-escape SQL LIKE wildcards (`\`, `%` and
     * `_`) in [query] so that typed wildcard characters match literally.
     */
    @Transaction
    @Query(
        "SELECT expenses.* FROM expenses " +
            "INNER JOIN categories ON categories.id = expenses.categoryId " +
            "WHERE (:categoryId IS NULL OR expenses.categoryId = :categoryId) " +
            "AND (LOWER(expenses.title) LIKE '%' || LOWER(:query) || '%' ESCAPE '\\' " +
            "OR LOWER(COALESCE(expenses.notes, '')) LIKE '%' || LOWER(:query) || '%' ESCAPE '\\' " +
            "OR LOWER(categories.name) LIKE '%' || LOWER(:query) || '%' ESCAPE '\\') " +
            "ORDER BY expenses.transactionDate DESC, expenses.id DESC"
    )
    fun searchExpenses(query: String, categoryId: Long?): Flow<List<ExpenseWithCategory>>

    @Query("SELECT COUNT(*) FROM expenses")
    fun getExpenseCount(): Flow<Int>

    /** All expenses within [startDate, endDate) — used by Analytics. */
    @Transaction
    @Query(
        "SELECT * FROM expenses " +
            "WHERE transactionDate >= :startDate AND transactionDate < :endDate " +
            "ORDER BY transactionDate DESC, id DESC"
    )
    fun getExpensesBetweenDate(startDate: Long, endDate: Long): Flow<List<ExpenseWithCategory>>

    // ---------- UPDATE ----------

    @Update
    suspend fun update(expense: Expense)

    // ---------- DELETE ----------

    @Delete
    suspend fun delete(expense: Expense)

    @Query("DELETE FROM expenses")
    suspend fun deleteAll()
}
