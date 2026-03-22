package com.lucio.expensetracker.domain.repository

import com.lucio.expensetracker.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    suspend fun insertExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
    fun getAllExpenses(): Flow<List<Expense>>
    fun getExpensesByMissionId(missionId: Long): Flow<List<Expense>>
    suspend fun getExpenseById(id: Long): Expense?
}
