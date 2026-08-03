package com.firstapp.myapplication.database.relation

/**
 * Aggregated statistics for one category, returned by a grouped Room query
 * on the expenses table. Room maps each row to this POJO automatically.
 *
 * @param categoryId The [com.firstapp.myapplication.database.entity.Category] id the stats belong to.
 * @param transactionCount Number of expenses recorded in this category.
 * @param totalAmount Sum of all expense amounts in this category.
 */
data class CategoryStats(
    val categoryId: Long,
    val transactionCount: Int,
    val totalAmount: Double
)
