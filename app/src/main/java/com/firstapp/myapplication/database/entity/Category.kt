package com.firstapp.myapplication.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing an expense category.
 *
 * @param id Auto-generated primary key.
 * @param name Display name of the category (e.g. "Food").
 * @param icon Drawable resource *name* of the category icon (e.g. "ic_food").
 *        Storing names instead of resource IDs keeps the database stable across builds.
 * @param color Color resource *name* used for the category indicator (e.g. "primary_container").
 * @param createdAt Epoch millis when the category was created.
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: String,
    val createdAt: Long = System.currentTimeMillis()
)
