package com.firstapp.myapplication

/**
 * Data class representing a single expense category shown on the
 * Category Manager screen.
 *
 * All values come from the Room database:
 * - [id] is the @PrimaryKey (auto-generated)
 * - [name] stores the category display name
 * - [iconResId] stores the drawable resource ID for the category icon
 * - [expenseCount] stores the number of expenses in this category
 * - [totalAmount] stores the total amount spent in this category
 * - [colorIndicatorResId] stores the color resource ID for the category color indicator
 */
data class CategoryItem(
    val id: Int,
    val name: String,
    val iconResId: Int,
    val expenseCount: Int,
    val totalAmount: Double,
    val colorIndicatorResId: Int
)
