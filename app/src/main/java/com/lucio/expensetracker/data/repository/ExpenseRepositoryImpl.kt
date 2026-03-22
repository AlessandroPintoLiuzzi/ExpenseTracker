package com.lucio.expensetracker.data.repository

import com.lucio.expensetracker.data.local.ExpenseDao
import com.lucio.expensetracker.data.mapper.toExpense
import com.lucio.expensetracker.data.mapper.toExpenseEntity
import com.lucio.expensetracker.domain.model.Expense
import com.lucio.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseRepositoryImpl(
    private val dao: ExpenseDao
) : ExpenseRepository {

    override suspend fun insertExpense(expense: Expense) {
        dao.insertExpense(expense.toExpenseEntity())
    }

    override suspend fun deleteExpense(expense: Expense) {
        dao.deleteExpense(expense.toExpenseEntity())
    }

    override fun getAllExpenses(): Flow<List<Expense>> {
        return dao.getAllExpenses().map { entities ->
            entities.map { it.toExpense() }
        }
    }

    override fun getExpensesByMissionId(missionId: Long): Flow<List<Expense>> {
        return dao.getExpensesByMissionId(missionId).map { entities ->
            entities.map { it.toExpense() }
        }
    }

    override suspend fun getExpenseById(id: Long): Expense? {
        return dao.getExpenseById(id)?.toExpense()
    }
}
