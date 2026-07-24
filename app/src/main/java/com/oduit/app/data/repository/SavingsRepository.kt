package com.oduit.app.data.repository

import com.oduit.app.data.local.dao.SavingsGoalDao
import com.oduit.app.data.local.entity.SavingsContribution
import com.oduit.app.data.local.entity.SavingsGoal
import kotlinx.coroutines.flow.Flow

class SavingsRepository(
    private val savingsGoalDao: SavingsGoalDao,
) {
    fun getAllGoals(): Flow<List<SavingsGoal>> {
        return savingsGoalDao.getAllGoals()
    }

    suspend fun getGoalById(id: Long): SavingsGoal? {
        return savingsGoalDao.getGoalById(id)
    }

    suspend fun insertGoal(goal: SavingsGoal): Long {
        return savingsGoalDao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: SavingsGoal) {
        savingsGoalDao.updateGoal(goal)
    }

    suspend fun deleteGoal(goal: SavingsGoal) {
        savingsGoalDao.deleteGoal(goal)
    }

    suspend fun addContribution(goalId: Long, amount: Double) {
        savingsGoalDao.addContribution(goalId, amount)
        savingsGoalDao.insertContribution(
            SavingsContribution(goalId = goalId, amount = amount),
        )
    }

    fun getContributions(goalId: Long): Flow<List<SavingsContribution>> {
        return savingsGoalDao.getContributions(goalId)
    }
}
