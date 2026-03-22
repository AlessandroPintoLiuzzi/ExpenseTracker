package com.lucio.expensetracker.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lucio.expensetracker.domain.repository.ExpenseRepository
import com.lucio.expensetracker.domain.repository.MissionRepository

class DashboardViewModelFactory(
    private val expenseRepository: ExpenseRepository,
    private val missionRepository: MissionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(expenseRepository, missionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
