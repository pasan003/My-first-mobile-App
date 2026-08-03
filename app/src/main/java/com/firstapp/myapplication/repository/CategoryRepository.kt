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

    // ---------- CREATE ----------

    suspend fun insert(category: Category): Long = categoryDao.insert(category)

    // ---------- UPDATE ----------

    suspend fun update(category: Category) = categoryDao.update(category)

    // ---------- DELETE ----------

    suspend fun delete(category: Category) = categoryDao.delete(category)

    suspend fun deleteAll() = categoryDao.deleteAll()
}
