package com.lucio.expensetracker.ui.add_expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lucio.expensetracker.domain.repository.ExpenseRepository
import com.lucio.expensetracker.domain.repository.MissionRepository

class AddExpenseViewModelFactory(
    private val expenseRepository: ExpenseRepository,
    private val missionRepository: MissionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddExpenseViewModel::class.java)) {
            return AddExpenseViewModel(expenseRepository, missionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
