package com.firstapp.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.firstapp.myapplication.database.dao.CategoryDao
import com.firstapp.myapplication.database.dao.ExpenseDao
import com.firstapp.myapplication.database.entity.Category
import com.firstapp.myapplication.database.entity.Expense
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * The application's Room database.
 *
 * Uses the singleton pattern so every Repository / ViewModel / Activity
 * shares a single database connection.
 */
@Database(
    entities = [Expense::class, Category::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        private const val DATABASE_NAME = "spendwise_database"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns the singleton database instance, creating it on first access.
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(SeedDatabaseCallback(context.applicationContext))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Inserts the default categories only on the very first launch,
         * when the database file is created. Later launches never re-seed,
         * so no duplicate categories can be inserted.
         */
        private class SeedDatabaseCallback(private val context: Context) :
            RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val database = getInstance(context)
                    database.categoryDao().insert(
                        Category(name = "Food", icon = "ic_food", color = "primary_container")
                    )
                    database.categoryDao().insert(
                        Category(name = "Transport", icon = "ic_transport", color = "secondary_container")
                    )
                    database.categoryDao().insert(
                        Category(name = "Shopping", icon = "ic_shopping", color = "tertiary")
                    )
                    database.categoryDao().insert(
                        Category(name = "Bills", icon = "ic_bills", color = "error_container")
                    )
                    database.categoryDao().insert(
                        Category(name = "Entertainment", icon = "ic_entertainment", color = "secondary")
                    )
                }
            }
        }
    }
}
