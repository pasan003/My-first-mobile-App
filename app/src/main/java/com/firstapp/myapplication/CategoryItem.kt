package com.firstapp.myapplication

/**
 * Data class representing a single expense category.
 *
 * Designed for future Room Database integration:
 * - [id] will serve as the @PrimaryKey (auto-generated)
 * - [name] stores the category display name
 * - [iconResId] stores the drawable resource ID for the category icon
 * - [expenseCount] stores the number of expenses in this category (sample data for now)
 * - [colorIndicatorResId] stores the color resource ID for the category color indicator
 */
data class CategoryItem(
    val id: Int,
    val name: String,
    val iconResId: Int,
    val expenseCount: Int,
    val colorIndicatorResId: Int
)
