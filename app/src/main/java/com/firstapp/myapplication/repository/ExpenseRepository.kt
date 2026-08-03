package com.firstapp.myapplication.repository

import com.firstapp.myapplication.database.dao.ExpenseDao
import com.firstapp.myapplication.database.entity.Expense
import com.firstapp.myapplication.database.relation.CategoryStats
import com.firstapp.myapplication.database.relation.ExpenseWithCategory
import kotlinx.coroutines.flow.Flow

/**
 * Repository that hides the [ExpenseDao] implementation details from the
 * ViewModels and Activities. All database access goes through repositories.
 */
class ExpenseRepository(private val expenseDao: ExpenseDao) {

    // ---------- READ ----------

    fun getAllExpenses(): Flow<List<ExpenseWithCategory>> = expenseDao.getAllExpenses()

    fun getExpenseById(id: Long): Flow<ExpenseWithCategory?> = expenseDao.getExpenseById(id)

    fun getRecentExpenses(limit: Int): Flow<List<ExpenseWithCategory>> =
        expenseDao.getRecentExpenses(limit)

    fun getTotalExpenses(): Flow<Double> = expenseDao.getTotalExpenses()

    /** Aggregated transaction count + total amount per category. */
    fun getCategoryStats(): Flow<List<CategoryStats>> = expenseDao.getCategoryStats()

    fun getMonthlyExpenses(startDate: Long, endDate: Long): Flow<Double> =
        expenseDao.getMonthlyExpenses(startDate, endDate)

    /**
     * Expenses matching [query] in title, notes or category name
     * (case-insensitive), optionally restricted to [categoryId] (`null` = all).
     *
     * [query] must have SQL LIKE wildcards (`\`, `%`, `_`) pre-escaped — the
     * only caller (ExpenseViewModel.filteredExpenses) escapes them via its
     * private `escapeLike` helper, paired with the DAO's `ESCAPE '\'` clause.
     */
    fun searchExpenses(query: String, categoryId: Long?): Flow<List<ExpenseWithCategory>> =
        expenseDao.searchExpenses(query, categoryId)

    fun getExpenseCount(): Flow<Int> = expenseDao.getExpenseCount()

    fun getExpensesBetweenDate(startDate: Long, endDate: Long): Flow<List<ExpenseWithCategory>> =
        expenseDao.getExpensesBetweenDate(startDate, endDate)

    // ---------- CREATE ----------

    suspend fun insert(expense: Expense): Long = expenseDao.insert(expense)

    // ---------- UPDATE ----------

    suspend fun update(expense: Expense) = expenseDao.update(expense)

    // ---------- DELETE ----------

    suspend fun delete(expense: Expense) = expenseDao.delete(expense)

    suspend fun deleteAll() = expenseDao.deleteAll()
}
