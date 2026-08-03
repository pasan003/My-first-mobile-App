package com.firstapp.myapplication.repository

import com.firstapp.myapplication.database.dao.CategoryDao
import com.firstapp.myapplication.database.entity.Category
import kotlinx.coroutines.flow.Flow

/**
 * Repository that hides the [CategoryDao] implementation details from the
 * ViewModels and Activities.
 */
class CategoryRepository(private val categoryDao: CategoryDao) {

    // ---------- READ ----------

    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    fun getCategoryById(id: Long): Flow<Category?> = categoryDao.getCategoryById(id)

    fun getCategoryCount(): Flow<Int> = categoryDao.getCategoryCount()

    suspend fun countByName(name: String, excludeId: Long): Int =
        categoryDao.countByName(name, excludeId)

    suspend fun getByName(name: String): Category? = categoryDao.getByName(name)

    // ---------- CREATE ----------

    suspend fun insert(category: Category): Long = categoryDao.insert(category)

    // ---------- UPDATE ----------

    suspend fun update(category: Category) = categoryDao.update(category)

    // ---------- DELETE ----------

    suspend fun delete(category: Category) = categoryDao.delete(category)

    /** Moves a category's expenses to another category, then deletes it — atomically. */
    suspend fun deleteCategoryAndMoveExpenses(categoryId: Long, targetCategoryId: Long) =
        categoryDao.deleteCategoryAndMoveExpenses(categoryId, targetCategoryId)

    suspend fun deleteAll() = categoryDao.deleteAll()
}
