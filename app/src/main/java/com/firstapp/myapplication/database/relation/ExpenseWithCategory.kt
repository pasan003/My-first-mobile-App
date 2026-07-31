package com.firstapp.myapplication.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.firstapp.myapplication.database.entity.Category
import com.firstapp.myapplication.database.entity.Expense

/**
 * POJO produced by Room when joining an [Expense] with its [Category].
 *
 * Using the @Relation annotation is the recommended Room way to model the
 * one-to-many relationship between Category and Expense while still getting
 * both objects in a single query.
 */
data class ExpenseWithCategory(
    @Embedded
    val expense: Expense,
    @Relation(parentColumn = "categoryId", entityColumn = "id")
    val category: Category?
)
