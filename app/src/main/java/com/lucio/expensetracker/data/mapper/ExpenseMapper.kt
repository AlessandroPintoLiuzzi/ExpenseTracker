package com.lucio.expensetracker.data.mapper

import com.lucio.expensetracker.data.local.ExpenseEntity
import com.lucio.expensetracker.domain.model.Expense

fun ExpenseEntity.toExpense(): Expense {
    return Expense(
        id = id,
        amount = amount,
        category = category,
        date = date,
        receiptPath = receiptPath,
        missionId = missionId
    )
}

fun Expense.toExpenseEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        amount = amount,
        category = category,
        date = date,
        receiptPath = receiptPath,
        missionId = missionId
    )
}
