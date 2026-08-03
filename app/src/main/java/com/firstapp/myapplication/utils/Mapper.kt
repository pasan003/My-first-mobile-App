package com.firstapp.myapplication.utils

import com.firstapp.myapplication.CategoryItem
import com.firstapp.myapplication.Transaction
import com.firstapp.myapplication.database.entity.Category
import com.firstapp.myapplication.database.relation.ExpenseWithCategory

/**
 * Converts Room entities into the plain UI models used by the RecyclerView
 * adapters and activities. Keeping this conversion in one place means the
 * UI layer never has to know about Room annotations or relations.
 */
object Mapper {

    /**
     * Converts an [ExpenseWithCategory] relation into the [Transaction] UI model.
     */
    fun toTransaction(item: ExpenseWithCategory): Transaction {
        val category = item.category
        return Transaction(
            id = item.expense.id,
            title = item.expense.title,
            category = category?.name ?: "Other",
            amount = item.expense.amount,
            date = DateUtils.formatDate(item.expense.transactionDate),
            iconResId = CategoryVisuals.iconResId(category?.icon ?: "ic_category_outline"),
            paymentMethod = item.expense.paymentMethod,
            notes = item.expense.notes,
            transactionDate = item.expense.transactionDate
        )
    }

    /**
     * Converts a [Category] entity plus its live stats (transaction count and
     * total amount, aggregated by the DAO) into the [CategoryItem] UI model.
     */
    fun toCategoryItem(
        category: Category,
        expenseCount: Int,
        totalAmount: Double
    ): CategoryItem {
        return CategoryItem(
            id = category.id.toInt(),
            name = category.name,
            iconResId = CategoryVisuals.iconResId(category.icon),
            expenseCount = expenseCount,
            totalAmount = totalAmount,
            colorIndicatorResId = CategoryVisuals.colorResId(category.color)
        )
    }
}
