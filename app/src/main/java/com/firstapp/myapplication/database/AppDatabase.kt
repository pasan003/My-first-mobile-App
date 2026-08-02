package com.firstapp.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.firstapp.myapplication.database.dao.CategoryDao
import com.firstapp.myapplication.database.dao.ExpenseDao
import com.firstapp.myapplication.database.dao.UserProfileDao
import com.firstapp.myapplication.database.entity.Category
import com.firstapp.myapplication.database.entity.Expense
import com.firstapp.myapplication.database.entity.UserProfile
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
    entities = [Expense::class, Category::class, UserProfile::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        private const val DATABASE_NAME = "spendwise_database"

        /** The nine categories seeded on the very first launch. */
        private val DEFAULT_CATEGORIES = listOf(
            Triple("Food", "ic_food", "primary_container"),
            Triple("Transport", "ic_transport", "secondary_container"),
            Triple("Shopping", "ic_shopping", "tertiary"),
            Triple("Bills", "ic_bills", "error_container"),
            Triple("Entertainment", "ic_entertainment", "secondary"),
            Triple("Health", "ic_health", "primary"),
            Triple("Education", "ic_education", "secondary_container"),
            Triple("Salary", "ic_payment", "text_income"),
            Triple("Other", "ic_category_outline", "surface_variant")
        )

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
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(SeedDatabaseCallback(context.applicationContext))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Upgrades an existing v1 database to v2 without losing user data:
         * 1. Creates the new `user_profiles` table.
         * 2. Adds the default categories that did not exist in v1
         *    (Health, Education, Salary, Other) — only if missing, so no
         *    duplicates are ever inserted.
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `user_profiles` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`fullName` TEXT NOT NULL, " +
                        "`monthlyIncome` REAL NOT NULL, " +
                        "`currency` TEXT NOT NULL, " +
                        "`createdAt` INTEGER NOT NULL)"
                )
                seedMissingCategories(db)
            }
        }

        /**
         * Inserts any default category that is not already present. Used by the
         * v1 -> v2 migration so existing installs also get the new defaults.
         */
        private fun seedMissingCategories(db: SupportSQLiteDatabase) {
            DEFAULT_CATEGORIES.forEach { (name, icon, color) ->
                db.execSQL(
                    "INSERT INTO `categories` (`name`, `icon`, `color`, `createdAt`) " +
                        "SELECT '$name', '$icon', '$color', ${System.currentTimeMillis()} " +
                        "WHERE NOT EXISTS (SELECT 1 FROM `categories` WHERE `name` = '$name')"
                )
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
                    val dao = database.categoryDao()
                    DEFAULT_CATEGORIES.forEach { (name, icon, color) ->
                        dao.insert(Category(name = name, icon = icon, color = color))
                    }
                }
            }
        }
    }
}
