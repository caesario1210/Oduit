package com.oduit.app.ui.screens.reports

import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.oduit.app.data.local.entity.Account
import com.oduit.app.data.local.entity.Transaction
import com.oduit.app.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class ReportPeriod {
    THIS_MONTH, THREE_MONTHS, SIX_MONTHS
}

data class CategoryBreakdown(
    val categoryId: Long,
    val categoryName: String,
    val color: Color,
    val amount: Double,
    val percentage: Float,
)

data class MonthlyData(
    val label: String,
    val income: Double,
    val expense: Double,
)

data class ReportUiState(
    val period: ReportPeriod = ReportPeriod.THIS_MONTH,
    val selectedAccountId: Long? = null,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val categoryBreakdown: List<CategoryBreakdown> = emptyList(),
    val monthlyTrend: List<MonthlyData> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val isLoading: Boolean = true,
)

class ReportViewModel(
    private val transactionRepository: TransactionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun setPeriod(period: ReportPeriod) {
        _uiState.value = _uiState.value.copy(period = period)
        loadData()
    }

    fun setAccountFilter(accountId: Long?) {
        _uiState.value = _uiState.value.copy(selectedAccountId = accountId)
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val (startDate, endDate) = getPeriodRange(_uiState.value.period)

            val allTransactions = transactionRepository
                .getTransactionsBetween(startDate, endDate)
                .firstOrNull() ?: emptyList()

            val filteredTxn = if (_uiState.value.selectedAccountId != null) {
                allTransactions.filter { it.accountId == _uiState.value.selectedAccountId }
            } else allTransactions

            val allCategories = transactionRepository
                .getAllCategories()
                .firstOrNull() ?: emptyList()

            val allAccounts = transactionRepository
                .getAllAccounts()
                .firstOrNull() ?: emptyList()

            val categoryMap = allCategories.associateBy { it.id }

            val cashflowTxn = filteredTxn.filter { it.transactionType != "transfer" }

            val totalIncome = cashflowTxn
                .filter { it.type == "income" }
                .sumOf { it.amount }

            val totalExpense = cashflowTxn
                .filter { it.type == "expense" }
                .sumOf { it.amount }

            val expenseByCategory = cashflowTxn
                .filter { it.type == "expense" && it.categoryId != null }
                .groupBy { it.categoryId!! }
                .mapValues { (_, txns) -> txns.sumOf { it.amount } }

            val totalExpenseCategorized = expenseByCategory.values.sum()
            val breakdown = expenseByCategory.map { (catId, amount) ->
                val cat = categoryMap[catId]
                CategoryBreakdown(
                    categoryId = catId,
                    categoryName = cat?.name ?: "Lainnya",
                    color = parseColor(cat?.color ?: "#607D8B"),
                    amount = amount,
                    percentage = if (totalExpenseCategorized > 0)
                        (amount / totalExpenseCategorized).toFloat() else 0f,
                )
            }.sortedByDescending { it.amount }

            val monthlyData = buildMonthlyTrend(
                _uiState.value.period,
                cashflowTxn,
            )

            _uiState.value = _uiState.value.copy(
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                balance = totalIncome - totalExpense,
                categoryBreakdown = breakdown,
                monthlyTrend = monthlyData,
                accounts = allAccounts,
                isLoading = false,
            )
        }
    }

    private fun buildMonthlyTrend(period: ReportPeriod, transactions: List<Transaction>): List<MonthlyData> {
        val cal = Calendar.getInstance()
        val monthCount = when (period) {
            ReportPeriod.THIS_MONTH -> 1
            ReportPeriod.THREE_MONTHS -> 3
            ReportPeriod.SIX_MONTHS -> 6
        }
        val effectiveCount = monthCount.coerceAtLeast(1)
        val result = mutableListOf<MonthlyData>()
        val dateFormat = SimpleDateFormat("MMM", Locale("id", "ID"))

        for (i in (effectiveCount - 1) downTo 0) {
            cal.timeInMillis = System.currentTimeMillis()
            cal.add(Calendar.MONTH, -i)
            val month = cal.get(Calendar.MONTH)
            val year = cal.get(Calendar.YEAR)
            val label = "${dateFormat.format(cal.time)} ${year.toString().takeLast(2)}"

            val monthStart = Calendar.getInstance().apply {
                set(year, month, 1, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val monthEnd = Calendar.getInstance().apply {
                set(year, month, 1, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.MONTH, 1)
                add(Calendar.MILLISECOND, -1)
            }.timeInMillis

            val monthTxns = transactions.filter { it.date in monthStart..monthEnd }
            val income = monthTxns.filter { it.type == "income" }.sumOf { it.amount }
            val expense = monthTxns.filter { it.type == "expense" }.sumOf { it.amount }
            result.add(MonthlyData(label, income, expense))
        }
        return result
    }

    private fun getPeriodRange(period: ReportPeriod): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        val end = cal.timeInMillis
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        val start = when (period) {
            ReportPeriod.THIS_MONTH -> cal.timeInMillis
            ReportPeriod.THREE_MONTHS -> { cal.add(Calendar.MONTH, -2); cal.timeInMillis }
            ReportPeriod.SIX_MONTHS -> { cal.add(Calendar.MONTH, -5); cal.timeInMillis }
        }
        return Pair(start, end)
    }

    private fun parseColor(hex: String): Color {
        return try { Color(AndroidColor.parseColor(hex)) }
        catch (_: Exception) { Color(0xFF009688) }
    }

    class Factory(
        private val transactionRepository: TransactionRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReportViewModel(transactionRepository) as T
        }
    }
}
