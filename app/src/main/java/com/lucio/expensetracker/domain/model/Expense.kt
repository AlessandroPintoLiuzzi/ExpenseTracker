package com.lucio.expensetracker.domain.model

data class Expense(
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val date: Long,
    val receiptPath: String?,
    val missionId: Long
)
