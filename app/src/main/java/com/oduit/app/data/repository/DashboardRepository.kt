package com.oduit.app.data.repository

import com.oduit.app.data.local.dao.AccountDao
import com.oduit.app.data.local.dao.CategoryDao
import com.oduit.app.data.local.dao.TransactionDao
import com.oduit.app.data.local.entity.Account
import com.oduit.app.data.local.entity.Category
import com.oduit.app.data.local.entity.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class DashboardRepository(
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
) {
    fun getAllAccounts(): Flow<List<Account>> {
        return accountDao.getAllAccounts()
    }

    fun getTotalBalance(): Flow<Double?> {
        return accountDao.getTotalInitialBalance()
    }

    fun getMonthlyIncome(month: Int, year: Int): Flow<Double?> {
        val range = getMonthRange(month, year)
        return transactionDao.getTotalByTypeBetween(range.first, range.second, "income")
    }

    fun getMonthlyExpense(month: Int, year: Int): Flow<Double?> {
        val range = getMonthRange(month, year)
        return transactionDao.getTotalByTypeBetween(range.first, range.second, "expense")
    }

    fun getRecentTransactions(month: Int, year: Int): Flow<List<com.oduit.app.data.local.entity.Transaction>> {
        val range = getMonthRange(month, year)
        return transactionDao.getRecentTransactions(range.first, range.second, 5)
    }

    fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions()
    }

    fun getAllTimeIncome(): Flow<Double?> {
        return transactionDao.getTotalByTypeBetween(0, Long.MAX_VALUE, "income")
    }

    fun getAllTimeExpense(): Flow<Double?> {
        return transactionDao.getTotalByTypeBetween(0, Long.MAX_VALUE, "expense")
    }

    fun getAccountIncome(accountId: Long): Flow<Double?> {
        return transactionDao.getTotalByTypeAndAccount("income", accountId)
    }

    fun getAccountExpense(accountId: Long): Flow<Double?> {
        return transactionDao.getTotalByTypeAndAccount("expense", accountId)
    }

    fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
    }

    fun getRecentTransactionsAll(): Flow<List<com.oduit.app.data.local.entity.Transaction>> {
        val cal = Calendar.getInstance()
        val range = getMonthRange(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR))
        return transactionDao.getRecentTransactions(range.first, range.second, 5)
    }

    private fun getMonthRange(month: Int, year: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(year, month, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        cal.add(Calendar.MONTH, 1)
        cal.add(Calendar.MILLISECOND, -1)
        val end = cal.timeInMillis

        return Pair(start, end)
    }
}
