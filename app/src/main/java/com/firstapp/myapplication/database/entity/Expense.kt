package com.firstapp.myapplication.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a single expense.
 *
 * Each expense belongs to exactly one [Category] through [categoryId].
 * The foreign key uses RESTRICT so a category that still has expenses
 * cannot be deleted (the UI checks the count first and shows a message).
 *
 * @param id Auto-generated primary key.
 * @param title Short description of the expense (e.g. "Lunch").
 * @param amount Monetary value of the expense.
 * @param categoryId Foreign key -> [Category.id].
 * @param notes Optional note attached to the expense.
 * @param paymentMethod How the expense was paid (e.g. "Cash").
 * @param transactionDate Epoch millis of the expense date (selected by the user).
 * @param createdAt Epoch millis when the expense was recorded.
 */
@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("categoryId")]
)
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val categoryId: Long,
    val notes: String = "",
    val paymentMethod: String = "",
    val transactionDate: Long,
    val createdAt: Long = System.currentTimeMillis()
)
