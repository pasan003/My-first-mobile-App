package com.firstapp.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.firstapp.myapplication.CategoryItem
import com.firstapp.myapplication.database.AppDatabase
import com.firstapp.myapplication.database.entity.Category
import com.firstapp.myapplication.repository.CategoryRepository
import com.firstapp.myapplication.repository.ExpenseRepository
import com.firstapp.myapplication.utils.Mapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Exposes category data from the repository as [LiveData], pairing each
 * category with its live expense count so the Category Manager can display
 * accurate counts.
 */
class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application.applicationContext)
    private val categoryRepository = CategoryRepository(database.categoryDao())
    private val expenseRepository = ExpenseRepository(database.expenseDao())

    /** All categories with live expense counts, sorted by name. */
    val categories: LiveData<List<CategoryItem>> =
        combine(
            categoryRepository.getAllCategories(),
            expenseRepository.getAllExpenses()
        ) { categoryList, expenseList ->
            categoryList.map { category ->
                val count = expenseList.count { it.expense.categoryId == category.id }
                Mapper.toCategoryItem(category, count)
            }
        }.asLiveData()

    /** Raw category names for dropdowns (e.g. the Add Expense screen). */
    val categoryNames: LiveData<List<String>> = categoryRepository.getAllCategories()
        .map { list -> list.map { it.name } }
        .asLiveData()

    /** Total number of categories — used by the summary badge. */
    val categoryCount: LiveData<Int> = categoryRepository.getCategoryCount().asLiveData()

    /** Whether the database is currently empty (no categories at all). */
    val isEmpty: LiveData<Boolean> = categoryRepository.getCategoryCount()
        .map { it == 0 }
        .asLiveData()

    // ---------- WRITE OPERATIONS ----------

    fun insert(category: Category, onComplete: ((Long) -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = categoryRepository.insert(category)
            onComplete?.invoke(id)
        }
    }

    fun update(category: Category, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.update(category)
            onComplete?.invoke()
        }
    }

    fun delete(category: Category, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.delete(category)
            onComplete?.invoke()
        }
    }
}
