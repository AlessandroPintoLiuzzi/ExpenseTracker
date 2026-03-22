package com.lucio.expensetracker.domain.model

data class Mission(
    val id: Long = 0,
    val name: String,
    val startDate: Long,
    val endDate: Long
)
