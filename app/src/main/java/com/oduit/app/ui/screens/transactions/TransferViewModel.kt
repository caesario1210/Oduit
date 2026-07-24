package com.oduit.app.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.oduit.app.OduitApp
import com.oduit.app.data.local.entity.Category
import com.oduit.app.data.local.entity.Transaction
import com.oduit.app.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import com.oduit.app.util.getWittyMessage
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class TransferViewModel(
    private val transactionRepository: TransactionRepository,
) : ViewModel() {

    private val _transferSuccessEvent = MutableSharedFlow<String>()
    val transferSuccessEvent: SharedFlow<String> = _transferSuccessEvent.asSharedFlow()

    fun saveTransfer(fromId: Long, toId: Long, amount: Double, date: Long, note: String) {
        viewModelScope.launch {
            try {
                val catDao = OduitApp.instance.database.categoryDao()
                var allCats = catDao.getAllCategories().firstOrNull() ?: emptyList()

                var expCat = allCats.find { it.name == "Transfer" && it.type == "expense" }
                if (expCat == null) {
                    val id = catDao.insert(Category(
                        name = "Transfer", type = "expense", icon = "swap_horiz", color = "#009688", isDefault = true,
                    ))
                    allCats = catDao.getAllCategories().firstOrNull() ?: emptyList()
                    expCat = allCats.find { it.id == id }
                }

                var incCat = allCats.find { it.name == "Transfer" && it.type == "income" }
                if (incCat == null) {
                    val id = catDao.insert(Category(
                        name = "Transfer", type = "income", icon = "swap_horiz", color = "#009688", isDefault = true,
                    ))
                    allCats = catDao.getAllCategories().firstOrNull() ?: emptyList()
                    incCat = allCats.find { it.id == id }
                }

                val txnNote = if (note.isNotBlank()) "Transfer: $note" else "Transfer"

                expCat?.let {
                    transactionRepository.insert(Transaction(
                        accountId = fromId, amount = amount, type = "expense",
                        categoryId = it.id, date = date, note = txnNote,
                        transactionType = "transfer",
                    ))
                }

                incCat?.let {
                    transactionRepository.insert(Transaction(
                        accountId = toId, amount = amount, type = "income",
                        categoryId = it.id, date = date, note = txnNote,
                        transactionType = "transfer",
                    ))
                }

                _transferSuccessEvent.emit(getWittyMessage("", "transfer"))
            } catch (e: Exception) {
                _transferSuccessEvent.emit("Gagal: ${e.message}")
            }
        }
    }

    class Factory(
        private val transactionRepository: TransactionRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TransferViewModel(transactionRepository) as T
        }
    }
}
