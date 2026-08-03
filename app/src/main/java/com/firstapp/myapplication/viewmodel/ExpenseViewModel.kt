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

    /** All expenses (newest first) with their category info — used by Expense History. */
    val allExpenses: LiveData<List<Transaction>> = repository.getAllExpenses()
        .map { list -> list.map { Mapper.toTransaction(it) } }
        .asLiveData()

    /**
     * Expenses for Expense History, filtered by [selectedCategoryId] via Room.
     * When the selected id is null, every expense is returned; otherwise only
     * expenses that match that category id are returned. Emits again whenever
     * the filter changes or the underlying expenses table changes.
     */
    val filteredExpenses: LiveData<List<Transaction>> = _selectedCategoryId
        .flatMapLatest { categoryId ->
            val source = if (categoryId == null) {
                repository.getAllExpenses()
            } else {
                repository.getExpensesByCategory(categoryId)
            }
            source.map { list -> list.map { Mapper.toTransaction(it) } }
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
