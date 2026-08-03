package com.firstapp.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.firstapp.myapplication.Transaction
import com.firstapp.myapplication.database.AppDatabase
import com.firstapp.myapplication.database.entity.Expense
import com.firstapp.myapplication.database.relation.ExpenseWithCategory
import com.firstapp.myapplication.repository.ExpenseRepository
import com.firstapp.myapplication.utils.Mapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Exposes expense data from the repository as [LiveData] so Activities can
 * observe it instead of querying the database directly.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository =
        ExpenseRepository(AppDatabase.getInstance(application.applicationContext).expenseDao())

    /**
     * Currently selected category filter for Expense History.
     * `null` means "All" (no category filter).
     */
    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    /**
     * The text currently typed in the Expense History search bar.
     * Empty string means no search is active.
     */
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /** All expenses (newest first) with their category info — used by Expense History. */
    val allExpenses: LiveData<List<Transaction>> = repository.getAllExpenses()
        .map { list -> list.map { Mapper.toTransaction(it) } }
        .asLiveData()

    /**
     * Expenses for Expense History, filtered by the selected category
     * ([selectedCategoryId], `null` = All) AND the search query via Room.
     *
     * The matching (title, notes or category name, case-insensitive) happens
     * in SQLite with LIKE, so only matching rows ever leave the database.
     * Emits again whenever the category filter, the search text or the
     * underlying expenses table changes — giving real-time search results.
     * An empty query matches every row, so clearing the search text restores
     * the plain category-filtered list.
     */
    val filteredExpenses: LiveData<List<Transaction>> =
        combine(_selectedCategoryId, _searchQuery) { categoryId, query ->
            categoryId to query.escapeLike()
        }
            .flatMapLatest { (categoryId, query) ->
                repository.searchExpenses(query, categoryId)
                    .map { list -> list.map { Mapper.toTransaction(it) } }
            }
            .asLiveData()

    /** Total amount of all expenses — used by the Dashboard balance card. */
    val totalExpenses: LiveData<Double> = repository.getTotalExpenses().asLiveData()

    /** Number of expenses recorded — used by the Dashboard & History summaries. */
    val expenseCount: LiveData<Int> = repository.getExpenseCount().asLiveData()

    /**
     * Loads a single expense by its database id as a display-ready [Transaction].
     * Falls back to [Transaction] with an empty id if the expense no longer exists.
     */
    fun getExpenseById(id: Long): LiveData<Transaction> = repository.getExpenseById(id)
        .map { item -> item?.let { Mapper.toTransaction(it) } ?: emptyTransaction() }
        .asLiveData()

    /** Loads the raw [ExpenseWithCategory] for the detail screen (needs category + id). */
    fun getExpenseWithCategory(id: Long): LiveData<ExpenseWithCategory?> =
        repository.getExpenseById(id).asLiveData()

    /** Recent expenses for the Dashboard's "Recent Transactions" list. */
    fun getRecentExpenses(limit: Int): LiveData<List<Transaction>> =
        repository.getRecentExpenses(limit)
            .map { list -> list.map { Mapper.toTransaction(it) } }
            .asLiveData()

    /** Total expenses for the current month — used by History summary. */
    fun getMonthlyExpenses(startDate: Long, endDate: Long): LiveData<Double> =
        repository.getMonthlyExpenses(startDate, endDate).asLiveData()

    /**
     * Updates the category filter used by [filteredExpenses].
     * Pass `null` to show every transaction ("All").
     */
    fun selectCategoryFilter(categoryId: Long?) {
        if (_selectedCategoryId.value != categoryId) {
            _selectedCategoryId.value = categoryId
        }
    }

    /**
     * Updates the search text used by [filteredExpenses] in real time while
     * the user types. Pass an empty string to disable searching; the selected
     * category filter, if any, stays active. The query is trimmed so stray
     * leading/trailing whitespace never changes the results.
     */
    fun setSearchQuery(query: String) {
        val trimmed = query.trim()
        if (_searchQuery.value != trimmed) {
            _searchQuery.value = trimmed
        }
    }

    /**
     * Escapes SQL LIKE wildcards (`\`, `%` and `_`) so the text typed by the
     * user is matched literally instead of acting as a wildcard pattern.
     * Paired with the `ESCAPE '\'` clause in [com.firstapp.myapplication.database.dao.ExpenseDao.searchExpenses].
     */
    private fun String.escapeLike(): String =
        replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_")

    // ---------- WRITE OPERATIONS ----------

    fun insert(expense: Expense, onComplete: ((Long) -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.insert(expense)
            onComplete?.invoke(id)
        }
    }

    fun update(expense: Expense, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(expense)
            onComplete?.invoke()
        }
    }

    fun delete(expense: Expense, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(expense)
            onComplete?.invoke()
        }
    }

    /** Returns a blank Transaction used as a safe fallback when data is missing. */
    private fun emptyTransaction(): Transaction {
        return Transaction(
            id = 0L,
            title = "",
            category = "Other",
            amount = 0.0,
            date = "",
            iconResId = com.firstapp.myapplication.R.drawable.ic_category_outline
        )
    }
}
