package com.example.smartdailyexpensetracker.viewmodel

import Expense
import ExpenseRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class UiState(
    val todayList: List<Expense> = emptyList(),
    val totalToday: Double = 0.0
)

class ExpenseViewModel(private val repo: ExpenseRepository) : ViewModel() {
    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    init {
        observeToday()
    }

    val allExpenses = repo.getAll()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val dailyTotals: StateFlow<List<Pair<String, Double>>> =
        allExpenses.map { list ->
            val format = SimpleDateFormat("dd MMM", Locale.getDefault())
            list.groupBy { format.format(Date(it.timestamp)) }
                .map { (date, expenses) -> date to expenses.sumOf { it.amount } }
                .sortedBy { it.first }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val categoryTotals: StateFlow<List<Pair<String, Double>>> =
        allExpenses.map { list ->
            list.groupBy { it.category }
                .map { (cat, expenses) -> cat to expenses.sumOf { it.amount } }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    private fun observeToday() {
        val todayStart = getStartOfDay(System.currentTimeMillis())
        repo.getForDay(todayStart).onEach { list ->
            _ui.value = _ui.value.copy(
                todayList = list,
                totalToday = list.sumOf { it.amount }
            )
        }.launchIn(viewModelScope)
    }

    fun addExpense(title: String, amount: Double, category: String, notes: String?) {
        if (title.isBlank() || amount <= 0) return
        viewModelScope.launch {
            val since = System.currentTimeMillis() - 86400000L
            if (repo.countSimilar(title, amount, since) > 0) {
                // Duplicate warning (optional)
            }
            repo.addExpense(
                Expense(
                    title = title,
                    amount = amount,
                    category = category,
                    notes = notes
                )
            )
        }
    }

    private fun getStartOfDay(ts: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = ts
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    // ExpenseViewModel.kt
    fun loadExpensesForDate(dateMillis: Long) {
        viewModelScope.launch {
            repo.getForDay(dateMillis).collect { expenses ->
                _ui.value = _ui.value.copy(
                    todayList = expenses,
                    totalToday = expenses.sumOf { it.amount }
                )
            }
        }
    }

}

class ExpenseViewModelFactory(private val repo: ExpenseRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ExpenseViewModel(repo) as T
    }
}
