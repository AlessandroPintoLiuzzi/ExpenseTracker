package com.lucio.expensetracker.ui.dashboard

import com.lucio.expensetracker.domain.model.Expense
import com.lucio.expensetracker.domain.model.Mission

data class DashboardUiState(
    val expenses: List<Expense> = emptyList(),
    val missions: List<Mission> = emptyList(),
    val selectedMissionId: Long? = null,
    val totalTransport: Double = 0.0,
    val totalFood: Double = 0.0,
    val totalHotel: Double = 0.0,
    val totalOthers: Double = 0.0,
    val isLoading: Boolean = false
) {
    val totalSpending: Double
        get() = totalTransport + totalFood + totalHotel + totalOthers
    
    val selectedMissionName: String
        get() = missions.find { it.id == selectedMissionId }?.name ?: "All Missions"
}
