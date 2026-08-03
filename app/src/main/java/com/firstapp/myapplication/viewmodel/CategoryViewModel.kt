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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/** The ways the Category Manager list can be sorted. */
enum class CategorySortOption {
    NAME_A_Z,
    MOST_USED,
    LEAST_USED,
    HIGHEST_SPENDING,
    LOWEST_SPENDING
}

/** Result of an insert/update attempt. */
enum class CategorySaveResult {
    SUCCESS,
    DUPLICATE_NAME
}

/**
 * Exposes category data from the repository as [LiveData].
 *
 * - Pairing each category with its live transaction count and total amount
 *   (both aggregated by SQL queries in the DAO, never manually in the UI).
 * - Filtering the list in real time by the search query.
 * - Sorting the list by the selected [CategorySortOption] (remembered until
 *   the screen closes, but survives rotation because it lives in the ViewModel).
 * - Validating that category names are unique before insert/update.
 * - Deleting a category and, when requested, moving its expenses to the
 *   "Other" category first so no expense is ever left without a valid category.
 */
class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application.applicationContext)
    private val categoryRepository = CategoryRepository(database.categoryDao())
    private val expenseRepository = ExpenseRepository(database.expenseDao())

    /** The text currently typed in the Category Manager search bar. */
    private val _searchQuery = MutableStateFlow("")

    /** The currently selected sort order. */
    private val _sortOption = MutableStateFlow(CategorySortOption.NAME_A_Z)
    val sortOption: StateFlow<CategorySortOption> = _sortOption.asStateFlow()

    /**
     * All categories with live stats, filtered by the search query and sorted.
     * Emits again whenever categories, expenses, the query or the sort change.
     */
    val categories: LiveData<List<CategoryItem>> =
        combine(
            categoryRepository.getAllCategories(),
            expenseRepository.getCategoryStats(),
            _searchQuery,
            _sortOption
        ) { categoryList, stats, query, sort ->
            val statsByCategory = stats.associateBy { it.categoryId }
            categoryList
                .map { category ->
                    val stat = statsByCategory[category.id]
                    Mapper.toCategoryItem(
                        category = category,
                        expenseCount = stat?.transactionCount ?: 0,
                        totalAmount = stat?.totalAmount ?: 0.0
                    )
                }
                .filter { it.name.contains(query, ignoreCase = true) }
                .sortedWith(comparatorFor(sort))
        }.asLiveData()

    /** Raw category names for dropdowns (e.g. the Add Expense screen). */
    val categoryNames: LiveData<List<String>> = categoryRepository.getAllCategories()
        .map { list -> list.map { it.name } }
        .asLiveData()

    /** Total number of categories in the database — used by the summary badge. */
    val categoryCount: LiveData<Int> = categoryRepository.getCategoryCount().asLiveData()

    /** Whether the database is currently empty (no categories at all). */
    val isEmpty: LiveData<Boolean> = categoryRepository.getCategoryCount()
        .map { it == 0 }
        .asLiveData()

    // ---------- SEARCH & SORT ----------

    /** Updates the search filter in real time while the user types. */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /** Changes the sort order; kept until the screen is closed. */
    fun setSortOption(option: CategorySortOption) {
        _sortOption.value = option
    }

    // ---------- WRITE OPERATIONS ----------

    /**
     * Inserts a new category after checking that its name is not a duplicate.
     * The result is reported through [onResult].
     */
    fun insert(category: Category, onResult: ((CategorySaveResult) -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val duplicates = categoryRepository.countByName(category.name, excludeId = 0L)
            if (duplicates > 0) {
                onResult?.invoke(CategorySaveResult.DUPLICATE_NAME)
                return@launch
            }
            categoryRepository.insert(category)
            onResult?.invoke(CategorySaveResult.SUCCESS)
        }
    }

    /**
     * Updates an existing category after checking that its (new) name is not
     * already used by another category. The result is reported through [onResult].
     */
    fun update(category: Category, onResult: ((CategorySaveResult) -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val duplicates = categoryRepository.countByName(category.name, excludeId = category.id)
            if (duplicates > 0) {
                onResult?.invoke(CategorySaveResult.DUPLICATE_NAME)
                return@launch
            }
            categoryRepository.update(category)
            onResult?.invoke(CategorySaveResult.SUCCESS)
        }
    }

    /**
     * Deletes [category].
     *
     * When [moveExpensesToOther] is true the category's expenses are first
     * moved to the "Other" category inside one Room transaction, so no expense
     * is ever left without a valid category.
     *
     * @param onResult Called with true when the category was deleted, or false
     *        when it could not be deleted (e.g. "Other" is missing or is the
     *        category being deleted, so there is no valid fallback).
     */
    fun delete(
        category: Category,
        moveExpensesToOther: Boolean,
        onResult: ((Boolean) -> Unit)? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = if (moveExpensesToOther) {
                val other = categoryRepository.getByName("Other")
                if (other == null || other.id == category.id) {
                    false
                } else {
                    categoryRepository.deleteCategoryAndMoveExpenses(category.id, other.id)
                    true
                }
            } else {
                categoryRepository.delete(category)
                true
            }
            onResult?.invoke(success)
        }
    }

    // ---------- HELPERS ----------

    /** Returns the comparator for the given sort option (ties broken by name). */
    private fun comparatorFor(sort: CategorySortOption): Comparator<CategoryItem> =
        when (sort) {
            CategorySortOption.NAME_A_Z -> compareBy { it.name.lowercase() }
            CategorySortOption.MOST_USED ->
                compareByDescending<CategoryItem> { it.expenseCount }
                    .thenBy { it.name.lowercase() }
            CategorySortOption.LEAST_USED ->
                compareBy<CategoryItem> { it.expenseCount }
                    .thenBy { it.name.lowercase() }
            CategorySortOption.HIGHEST_SPENDING ->
                compareByDescending<CategoryItem> { it.totalAmount }
                    .thenBy { it.name.lowercase() }
            CategorySortOption.LOWEST_SPENDING ->
                compareBy<CategoryItem> { it.totalAmount }
                    .thenBy { it.name.lowercase() }
        }
}
