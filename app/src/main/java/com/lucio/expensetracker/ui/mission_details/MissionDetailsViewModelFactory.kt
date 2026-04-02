package com.lucio.expensetracker.ui.mission_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lucio.expensetracker.domain.repository.ExpenseRepository
import com.lucio.expensetracker.domain.repository.MissionRepository

class MissionDetailsViewModelFactory(
    private val missionId: Long,
    private val missionRepository: MissionRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MissionDetailsViewModel::class.java)) {
            return MissionDetailsViewModel(missionId, missionRepository, expenseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
