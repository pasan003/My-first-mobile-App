package com.firstapp.myapplication

/**
 * Data class representing a single financial transaction for the UI layer.
 *
 * This is the display model used by the RecyclerView adapters. It is created
 * from the Room [com.firstapp.myapplication.database.entity.Expense] entity by
 * [com.firstapp.myapplication.utils.Mapper] — the UI never talks to Room directly.
 *
 * @param id Database primary key of the underlying expense.
 * @param title Short description (e.g. "Lunch").
 * @param category Category display name (e.g. "Food").
 * @param amount Monetary value.
 * @param date Formatted display date.
 * @param iconResId Drawable resource ID for the category icon.
 * @param isExpense Whether it's an expense (true) or income (false).
 * @param paymentMethod How it was paid (e.g. "Cash").
 * @param notes Optional user note.
 * @param transactionDate Raw epoch-millis date (for the detail screen).
 */
data class Transaction(
    val id: Long,
    val title: String,
    val category: String,
    val amount: Double,
    val date: String,
    val iconResId: Int,
    val isExpense: Boolean = true,
    val paymentMethod: String = "",
    val notes: String = "",
    val transactionDate: Long = 0L
)
