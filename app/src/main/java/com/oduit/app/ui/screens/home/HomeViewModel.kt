package com.oduit.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.oduit.app.data.local.entity.Account
import com.oduit.app.data.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeUiState(
    val totalBalance: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val accounts: List<Account> = emptyList(),
    val accountBalances: Map<Long, Double> = emptyMap(),
    val recentTransactions: List<com.oduit.app.data.local.entity.Transaction> = emptyList(),
    val isLoading: Boolean = true,
)

class HomeViewModel(
    private val dashboardRepository: DashboardRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val cal = Calendar.getInstance()
    private val currentMonth = cal.get(Calendar.MONTH)
    private val currentYear = cal.get(Calendar.YEAR)

    private val accounts: List<Account> get() = _uiState.value.accounts

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            val accs = dashboardRepository.getAllAccounts().firstOrNull() ?: emptyList()
            val totalInitBalance = accs.sumOf { it.initialBalance }

            combine(
                dashboardRepository.getRecentTransactions(currentMonth, currentYear),
                dashboardRepository.getAllTransactions(),
            ) { recent, allTxns ->

                val cashflowTxns = allTxns.filter { it.transactionType != "transfer" }

                val allIncome = cashflowTxns.filter { it.type == "income" }.sumOf { it.amount }
                val allExpense = cashflowTxns.filter { it.type == "expense" }.sumOf { it.amount }

                val monthlyIncomeValue = cashflowTxns.filter {
                    it.type == "income" && isInCurrentMonth(it.date)
                }.sumOf { it.amount }

                val monthlyExpenseValue = cashflowTxns.filter {
                    it.type == "expense" && isInCurrentMonth(it.date)
                }.sumOf { it.amount }

                val recentFiltered = recent.filter { it.transactionType != "transfer" }

                val balances = mutableMapOf<Long, Double>()
                accs.forEach { acc ->
                    val accIncome = allTxns.filter { it.type == "income" && it.accountId == acc.id }.sumOf { it.amount }
                    val accExpense = allTxns.filter { it.type == "expense" && it.accountId == acc.id }.sumOf { it.amount }
                    balances[acc.id] = acc.initialBalance + accIncome - accExpense
                }

                HomeUiState(
                    totalBalance = totalInitBalance + allIncome - allExpense,
                    monthlyIncome = monthlyIncomeValue,
                    monthlyExpense = monthlyExpenseValue,
                    accounts = accs,
                    accountBalances = balances,
                    recentTransactions = recentFiltered,
                    isLoading = false,
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun isInCurrentMonth(dateMillis: Long): Boolean {
        val c = Calendar.getInstance()
        c.timeInMillis = dateMillis
        return c.get(Calendar.MONTH) == currentMonth && c.get(Calendar.YEAR) == currentYear
    }

    class Factory(
        private val dashboardRepository: DashboardRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(dashboardRepository) as T
        }
    }
}
