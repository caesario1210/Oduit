package com.oduit.app.data.repository

import com.oduit.app.data.local.dao.BudgetDao
import com.oduit.app.data.local.dao.CategoryDao
import com.oduit.app.data.local.dao.TransactionDao
import com.oduit.app.data.local.entity.Budget
import com.oduit.app.data.local.entity.Category
import com.oduit.app.data.local.entity.Transaction
import kotlinx.coroutines.flow.Flow

class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
) {
    fun getBudgetsForMonth(month: Int, year: Int): Flow<List<Budget>> {
        return budgetDao.getBudgetsForMonth(month, year)
    }

    suspend fun getBudgetById(id: Long): Budget? {
        return budgetDao.getBudgetById(id)
    }

    suspend fun getBudgetByCategory(categoryId: Long, month: Int, year: Int): Budget? {
        return budgetDao.getBudgetByCategory(categoryId, month, year)
    }

    suspend fun insert(budget: Budget): Long {
        return budgetDao.insert(budget)
    }

    suspend fun update(budget: Budget) {
        budgetDao.update(budget)
    }

    suspend fun delete(budget: Budget) {
        budgetDao.delete(budget)
    }

    fun getExpenseCategories(): Flow<List<Category>> {
        return categoryDao.getCategoriesByType("expense")
    }

    fun getTransactionsBetween(startDate: Long, endDate: Long): Flow<List<Transaction>> {
        return transactionDao.getTransactionsBetween(startDate, endDate)
    }

    fun getSpentForCategory(categoryId: Long, month: Int, year: Int): Flow<Double?> {
        val cal = java.util.Calendar.getInstance()
        cal.set(year, month, 1, 0, 0, 0)
        val start = cal.timeInMillis
        cal.add(java.util.Calendar.MONTH, 1)
        cal.add(java.util.Calendar.MILLISECOND, -1)
        val end = cal.timeInMillis

        return transactionDao.getExpenseTotalByCategory(start, end, categoryId)
    }
}
