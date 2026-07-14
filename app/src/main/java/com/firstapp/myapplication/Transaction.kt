package com.firstapp.myapplication

/**
 * Data class representing a single financial transaction.
 */
data class Transaction(
    val title: String,
    val category: String,
    val amount: Double,
    val date: String,
    val iconResId: Int,       // Drawable resource ID for the category icon
    val isExpense: Boolean = true
)
