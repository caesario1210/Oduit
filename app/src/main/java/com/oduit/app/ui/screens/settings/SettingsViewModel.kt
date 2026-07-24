package com.oduit.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.oduit.app.data.local.entity.Account
import com.oduit.app.data.local.entity.Category
import com.oduit.app.data.local.entity.Transaction
import com.oduit.app.data.local.entity.Budget
import com.oduit.app.data.local.entity.SavingsGoal
import com.oduit.app.data.local.entity.SavingsContribution
import com.oduit.app.data.local.dao.AccountDao
import com.oduit.app.data.local.dao.CategoryDao
import com.oduit.app.data.local.dao.TransactionDao
import com.oduit.app.data.local.dao.BudgetDao
import com.oduit.app.data.local.dao.SavingsGoalDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

data class SettingsUiState(
    val categories: List<Category> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val isLoading: Boolean = true,
)

class SettingsViewModel(
    private val categoryDao: CategoryDao,
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao,
    private val savingsGoalDao: SavingsGoalDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _showCategoryForm = MutableStateFlow(false)
    val showCategoryForm: StateFlow<Boolean> = _showCategoryForm.asStateFlow()

    private val _editingCategory = MutableStateFlow<Category?>(null)
    val editingCategory: StateFlow<Category?> = _editingCategory.asStateFlow()

    private val _showAccountForm = MutableStateFlow(false)
    val showAccountForm: StateFlow<Boolean> = _showAccountForm.asStateFlow()

    private val _editingAccount = MutableStateFlow<Account?>(null)
    val editingAccount: StateFlow<Account?> = _editingAccount.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val categories = categoryDao.getAllCategories().firstOrNull() ?: emptyList()
            val accounts = accountDao.getAllAccounts().firstOrNull() ?: emptyList()
            _uiState.value = SettingsUiState(categories = categories, accounts = accounts, isLoading = false)
        }
    }

    fun refresh() { loadData() }

    // Category CRUD
    fun showAddCategory() { _editingCategory.value = null; _showCategoryForm.value = true }
    fun showEditCategory(cat: Category) { _editingCategory.value = cat; _showCategoryForm.value = true }
    fun hideCategoryForm() { _showCategoryForm.value = false; _editingCategory.value = null }

    fun saveCategory(id: Long?, name: String, type: String, icon: String, color: String) {
        viewModelScope.launch {
            if (id != null && id > 0) {
                categoryDao.update(Category(id = id, name = name, type = type, icon = icon, color = color))
            } else {
                categoryDao.insert(Category(name = name, type = type, icon = icon, color = color))
            }
            hideCategoryForm()
            refresh()
        }
    }

    fun deleteCategory(cat: Category) {
        viewModelScope.launch { categoryDao.delete(cat); refresh() }
    }

    // Account CRUD
    fun showAddAccount() { _editingAccount.value = null; _showAccountForm.value = true }
    fun showEditAccount(acc: Account) { _editingAccount.value = acc; _showAccountForm.value = true }
    fun hideAccountForm() { _showAccountForm.value = false; _editingAccount.value = null }

    fun saveAccount(id: Long?, name: String, initialBalance: Double, icon: String) {
        viewModelScope.launch {
            if (id != null && id > 0) {
                accountDao.update(Account(id = id, name = name, initialBalance = initialBalance, icon = icon))
            } else {
                accountDao.insert(Account(name = name, initialBalance = initialBalance, icon = icon))
            }
            hideAccountForm()
            refresh()
        }
    }

    fun deleteAccount(acc: Account) {
        viewModelScope.launch { accountDao.delete(acc); refresh() }
    }

    // Export
    fun exportToUri(context: android.content.Context, uri: android.net.Uri) {
        viewModelScope.launch {
            try {
                val json = withContext(Dispatchers.IO) { buildExportJson() }
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    os.write(json.toString(2).toByteArray(Charsets.UTF_8))
                }
            } catch (_: Exception) { }
        }
    }

    private suspend fun buildExportJson(): JSONObject {
        val root = JSONObject()

        val accs = accountDao.getAllAccounts().firstOrNull() ?: emptyList()
        val cats = categoryDao.getAllCategories().firstOrNull() ?: emptyList()
        val txns = transactionDao.getAllTransactions().firstOrNull() ?: emptyList()

        val accountsArr = JSONArray()
        val categoriesArr = JSONArray()
        val transactionsArr = JSONArray()

        accs.forEach { accountsArr.put(it.toJson()) }
        cats.forEach { categoriesArr.put(it.toJson()) }
        txns.forEach { transactionsArr.put(it.toJson()) }

        root.put("version", 1)
        root.put("exported_at", System.currentTimeMillis())
        root.put("accounts", accountsArr)
        root.put("categories", categoriesArr)
        root.put("transactions", transactionsArr)

        return root
    }

    class Factory(
        private val categoryDao: CategoryDao,
        private val accountDao: AccountDao,
        private val transactionDao: TransactionDao,
        private val budgetDao: BudgetDao,
        private val savingsGoalDao: SavingsGoalDao,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(categoryDao, accountDao, transactionDao, budgetDao, savingsGoalDao) as T
        }
    }
}

private fun Account.toJson() = JSONObject().apply {
    put("id", id); put("name", name)
    put("initial_balance", initialBalance); put("icon", icon)
}

private fun Category.toJson() = JSONObject().apply {
    put("id", id); put("name", name); put("type", type)
    put("icon", icon); put("color", color); put("is_default", isDefault)
}

private fun Transaction.toJson() = JSONObject().apply {
    put("id", id); put("account_id", accountId); put("category_id", categoryId)
    put("amount", amount); put("type", type); put("date", date)
    put("note", note); put("created_at", createdAt)
}

private fun Budget.toJson() = JSONObject().apply {
    put("id", id); put("category_id", categoryId)
    put("month", month); put("year", year); put("limit_amount", limitAmount)
}

private fun SavingsGoal.toJson() = JSONObject().apply {
    put("id", id); put("name", name); put("target_amount", targetAmount)
    put("current_amount", currentAmount); put("target_date", targetDate)
    put("created_at", createdAt)
}
