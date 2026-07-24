package com.oduit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.oduit.app.data.local.entity.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("""
        SELECT * FROM transactions 
        ORDER BY date DESC, id DESC
    """)
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("""
        SELECT * FROM transactions 
        WHERE date BETWEEN :startDate AND :endDate 
        ORDER BY date DESC, id DESC
    """)
    fun getTransactionsBetween(startDate: Long, endDate: Long): Flow<List<Transaction>>

    @Query("""
        SELECT * FROM transactions 
        WHERE date BETWEEN :startDate AND :endDate 
        AND type = :type
        ORDER BY date DESC, id DESC
    """)
    fun getTransactionsByTypeBetween(
        startDate: Long,
        endDate: Long,
        type: String,
    ): Flow<List<Transaction>>

    @Query("""
        SELECT * FROM transactions 
        WHERE date BETWEEN :startDate AND :endDate 
        ORDER BY date DESC, id DESC
        LIMIT :limit
    """)
    fun getRecentTransactions(startDate: Long, endDate: Long, limit: Int = 5): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): Transaction?

    @Query("""
        SELECT COALESCE(SUM(amount), 0) FROM transactions 
        WHERE type = :type 
        AND date BETWEEN :startDate AND :endDate
    """)
    fun getTotalByTypeBetween(startDate: Long, endDate: Long, type: String): Flow<Double?>

    @Query("""
        SELECT COALESCE(SUM(amount), 0) FROM transactions 
        WHERE type = :type 
        AND accountId = :accountId
    """)
    fun getTotalByTypeAndAccount(type: String, accountId: Long): Flow<Double?>

    @Query("""
        SELECT COALESCE(SUM(amount), 0) FROM transactions 
        WHERE type = 'expense' 
        AND date BETWEEN :startDate AND :endDate
        AND categoryId = :categoryId
    """)
    fun getExpenseTotalByCategory(
        startDate: Long,
        endDate: Long,
        categoryId: Long,
    ): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)
}
