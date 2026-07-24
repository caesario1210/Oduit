package com.oduit.app.ui.screens.budget

import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.oduit.app.data.local.entity.Budget
import com.oduit.app.data.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar

data class BudgetWithSpent(
    val budget: Budget?,
    val categoryId: Long,
    val categoryName: String,
    val categoryColor: Color,
    val spent: Double = 0.0,
    val limit: Double = 0.0,
) {
    val progress: Float
        get() = if (limit > 0) (spent / limit).toFloat().coerceIn(0f, 1.5f) else 0f

    val status: BudgetStatus
        get() = when {
            limit <= 0 -> BudgetStatus.NOT_SET
            progress >= 1f -> BudgetStatus.OVER
            progress >= 0.7f -> BudgetStatus.WARNING
            else -> BudgetStatus.SAFE
        }
}

enum class BudgetStatus { NOT_SET, SAFE, WARNING, OVER }

data class BudgetUiState(
    val budgets: List<BudgetWithSpent> = emptyList(),
    val isLoading: Boolean = true,
)

class BudgetViewModel(
    private val budgetRepository: BudgetRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    private val _showForm = MutableStateFlow<BudgetWithSpent?>(null)
    val showForm: StateFlow<BudgetWithSpent?> = _showForm.asStateFlow()

    private val cal = Calendar.getInstance()
    private val currentMonth = cal.get(Calendar.MONTH)
    private val currentYear = cal.get(Calendar.YEAR)

    init {
        loadBudgets()
    }

    private fun loadBudgets() {
        val monthStart = Calendar.getInstance().apply {
            set(currentYear, currentMonth, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val monthEnd = Calendar.getInstance().apply {
            set(currentYear, currentMonth, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MONTH, 1)
            add(Calendar.MILLISECOND, -1)
        }.timeInMillis

        viewModelScope.launch {
            combine(
                budgetRepository.getExpenseCategories(),
                budgetRepository.getBudgetsForMonth(currentMonth, currentYear),
                budgetRepository.getTransactionsBetween(monthStart, monthEnd),
            ) { categories, budgets, transactions ->
                val budgetMap = budgets.associateBy { it.categoryId }

                val spentByCategory = transactions
                    .filter { it.type == "expense" && it.categoryId != null }
                    .groupBy { it.categoryId!! }
                    .mapValues { (_, txns) -> txns.sumOf { it.amount } }

                categories.map { cat ->
                    val b = budgetMap[cat.id]
                    BudgetWithSpent(
                        budget = b,
                        categoryId = cat.id,
                        categoryName = cat.name,
                        categoryColor = parseColor(cat.color),
                        spent = spentByCategory[cat.id] ?: 0.0,
                        limit = b?.limitAmount ?: 0.0,
                    )
                }
            }.collect { result ->
                _uiState.value = BudgetUiState(budgets = result, isLoading = false)
            }
        }
    }

    fun showFormFor(budget: BudgetWithSpent) {
        _showForm.value = budget
    }

    fun hideForm() {
        _showForm.value = null
    }

    fun saveBudget(categoryId: Long, limitAmount: Double) {
        viewModelScope.launch {
            val existing = budgetRepository.getBudgetByCategory(categoryId, currentMonth, currentYear)
            if (existing != null) {
                budgetRepository.update(existing.copy(limitAmount = limitAmount))
            } else {
                budgetRepository.insert(
                    Budget(
                        categoryId = categoryId,
                        month = currentMonth,
                        year = currentYear,
                        limitAmount = limitAmount,
                    ),
                )
            }
            hideForm()
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            budgetRepository.delete(budget)
            hideForm()
        }
    }

    private fun parseColor(hex: String): Color {
        return try { Color(AndroidColor.parseColor(hex)) }
        catch (_: Exception) { Color(0xFF009688) }
    }

    class Factory(
        private val budgetRepository: BudgetRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BudgetViewModel(budgetRepository) as T
        }
    }
}
