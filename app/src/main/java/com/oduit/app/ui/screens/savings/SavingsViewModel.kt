package com.oduit.app.ui.screens.savings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.oduit.app.data.local.entity.SavingsGoal
import com.oduit.app.data.repository.SavingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class SavingsGoalWithProgress(
    val goal: SavingsGoal,
) {
    val progress: Float
        get() = if (goal.targetAmount > 0)
            (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f

    val percent: Int
        get() = (progress * 100).toInt()

    val remainingDays: Long?
        get() {
            val targetDate = goal.targetDate ?: return null
            val now = Calendar.getInstance().timeInMillis
            if (targetDate <= now) return 0
            return (targetDate - now) / (1000 * 60 * 60 * 24)
        }

    val isCompleted: Boolean
        get() = goal.currentAmount >= goal.targetAmount
}

data class SavingsUiState(
    val goals: List<SavingsGoalWithProgress> = emptyList(),
    val isLoading: Boolean = true,
)

class SavingsViewModel(
    private val savingsRepository: SavingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavingsUiState())
    val uiState: StateFlow<SavingsUiState> = _uiState.asStateFlow()

    private val _showForm = MutableStateFlow(false)
    val showForm: StateFlow<Boolean> = _showForm.asStateFlow()

    private val _editingGoal = MutableStateFlow<SavingsGoal?>(null)
    val editingGoal: StateFlow<SavingsGoal?> = _editingGoal.asStateFlow()

    private val _showContribution = MutableStateFlow<SavingsGoal?>(null)
    val showContribution: StateFlow<SavingsGoal?> = _showContribution.asStateFlow()

    init {
        loadGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            savingsRepository.getAllGoals().collect { goals ->
                _uiState.value = SavingsUiState(
                    goals = goals.map { SavingsGoalWithProgress(it) },
                    isLoading = false,
                )
            }
        }
    }

    fun showAddForm() {
        _editingGoal.value = null
        _showForm.value = true
    }

    fun showEditForm(goal: SavingsGoal) {
        _editingGoal.value = goal
        _showForm.value = true
    }

    fun hideForm() {
        _showForm.value = false
        _editingGoal.value = null
    }

    fun showContributionFor(goal: SavingsGoal) {
        _showContribution.value = goal
    }

    fun hideContribution() {
        _showContribution.value = null
    }

    fun saveGoal(id: Long?, name: String, targetAmount: Double, targetDate: Long?) {
        viewModelScope.launch {
            if (id != null && id > 0) {
                val existing = savingsRepository.getGoalById(id) ?: return@launch
                savingsRepository.updateGoal(
                    existing.copy(name = name, targetAmount = targetAmount, targetDate = targetDate),
                )
            } else {
                savingsRepository.insertGoal(
                    SavingsGoal(
                        name = name,
                        targetAmount = targetAmount,
                        targetDate = targetDate,
                    ),
                )
            }
            hideForm()
        }
    }

    fun deleteGoal(goal: SavingsGoal) {
        viewModelScope.launch {
            savingsRepository.deleteGoal(goal)
            hideForm()
        }
    }

    fun addContribution(goalId: Long, amount: Double) {
        viewModelScope.launch {
            savingsRepository.addContribution(goalId, amount)
            hideContribution()
        }
    }

    class Factory(
        private val savingsRepository: SavingsRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SavingsViewModel(savingsRepository) as T
        }
    }
}
