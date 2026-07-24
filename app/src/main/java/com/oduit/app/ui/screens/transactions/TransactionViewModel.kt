package com.oduit.app.ui.screens.transactions

import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.oduit.app.data.local.entity.Category
import com.oduit.app.data.local.entity.Transaction
import com.oduit.app.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TransactionWithCategory(
    val transaction: Transaction,
    val categoryName: String = "",
    val categoryIcon: String = "category",
    val categoryColor: Color = Color(0xFF009688),
    val accountName: String = "",
)

data class TransactionUiState(
    val transactions: List<TransactionWithCategory> = emptyList(),
    val groupedTransactions: Map<String, List<TransactionWithCategory>> = emptyMap(),
    val categories: List<Category> = emptyList(),
    val selectedFilter: String = "all",
    val isLoading: Boolean = true,
)

class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    private val _showForm = MutableStateFlow(false)
    val showForm: StateFlow<Boolean> = _showForm.asStateFlow()

    private val _editingTransaction = MutableStateFlow<Transaction?>(null)
    val editingTransaction: StateFlow<Transaction?> = _editingTransaction.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            val accounts = transactionRepository.getAllAccounts().firstOrNull() ?: emptyList()
            val accountMap = accounts.associateBy { it.id }

            combine(
                transactionRepository.getAllTransactions(),
                transactionRepository.getAllCategories(),
            ) { transactions, categories ->
                val categoryMap = categories.associateBy { it.id }
                val enriched = transactions.map { txn ->
                    val cat = txn.categoryId?.let { categoryMap[it] }
                    val acc = accountMap[txn.accountId]
                    TransactionWithCategory(
                        transaction = txn,
                        categoryName = cat?.name ?: "",
                        categoryIcon = cat?.icon ?: "category",
                        categoryColor = parseColor(cat?.color ?: "#009688"),
                        accountName = acc?.name ?: "",
                    )
                }

                val filtered = when (_uiState.value.selectedFilter) {
                    "income" -> enriched.filter { it.transaction.type == "income" && it.categoryName != "Transfer" }
                    "expense" -> enriched.filter { it.transaction.type == "expense" && it.categoryName != "Transfer" }
                    "transfer" -> enriched.filter { it.categoryName == "Transfer" }
                    else -> enriched
                }

                _uiState.value = TransactionUiState(
                    transactions = enriched,
                    groupedTransactions = groupByDate(filtered),
                    categories = categories,
                    selectedFilter = _uiState.value.selectedFilter,
                    isLoading = false,
                )
            }.collect { }
        }
    }

    fun setFilter(filter: String) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        regroup()
    }

    private fun regroup() {
        val enriched = _uiState.value.transactions
        val filtered = when (_uiState.value.selectedFilter) {
            "income" -> enriched.filter { it.transaction.type == "income" && it.categoryName != "Transfer" }
            "expense" -> enriched.filter { it.transaction.type == "expense" && it.categoryName != "Transfer" }
            "transfer" -> enriched.filter { it.categoryName == "Transfer" }
            else -> enriched
        }
        _uiState.value = _uiState.value.copy(
            groupedTransactions = groupByDate(filtered),
        )
    }

    private fun groupByDate(transactions: List<TransactionWithCategory>): Map<String, List<TransactionWithCategory>> {
        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
        return transactions.groupBy {
            dateFormat.format(Date(it.transaction.date))
        }
    }

    fun showAddForm() {
        _editingTransaction.value = null
        _showForm.value = true
    }

    fun showEditForm(transaction: Transaction) {
        _editingTransaction.value = transaction
        _showForm.value = true
    }

    fun hideForm() {
        _showForm.value = false
        _editingTransaction.value = null
    }

    fun saveTransaction(
        amount: Double,
        type: String,
        accountId: Long,
        categoryId: Long?,
        date: Long,
        note: String,
    ) {
        viewModelScope.launch {
            val existing = _editingTransaction.value
            if (existing != null) {
                transactionRepository.update(
                    existing.copy(
                        amount = amount,
                        type = type,
                        accountId = accountId,
                        categoryId = categoryId,
                        date = date,
                        note = note,
                    ),
                )
            } else {
                transactionRepository.insert(
                    Transaction(
                        accountId = accountId,
                        amount = amount,
                        type = type,
                        categoryId = categoryId,
                        date = date,
                        note = note,
                    ),
                )
            }
            hideForm()
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.delete(transaction)
        }
    }

    private fun parseColor(hex: String): Color {
        return try {
            Color(AndroidColor.parseColor(hex))
        } catch (_: Exception) {
            Color(0xFF009688)
        }
    }

    class Factory(
        private val transactionRepository: TransactionRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TransactionViewModel(transactionRepository) as T
        }
    }
}
