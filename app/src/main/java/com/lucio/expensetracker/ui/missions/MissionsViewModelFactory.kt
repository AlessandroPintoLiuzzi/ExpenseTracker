package com.lucio.expensetracker.ui.missions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lucio.expensetracker.domain.repository.ExpenseRepository
import com.lucio.expensetracker.domain.repository.MissionRepository

class MissionsViewModelFactory(
    private val missionRepository: MissionRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MissionsViewModel::class.java)) {
            return MissionsViewModel(missionRepository, expenseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
