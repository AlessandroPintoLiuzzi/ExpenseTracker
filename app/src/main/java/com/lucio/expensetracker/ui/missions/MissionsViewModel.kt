package com.lucio.expensetracker.ui.missions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucio.expensetracker.domain.model.Mission
import com.lucio.expensetracker.domain.repository.MissionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MissionsViewModel(
    private val repository: MissionRepository
) : ViewModel() {

    val missions: StateFlow<List<Mission>> = repository.getAllMissions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addMission(name: String, startDate: Long, endDate: Long) {
        viewModelScope.launch {
            repository.insertMission(
                Mission(name = name, startDate = startDate, endDate = endDate)
            )
        }
    }

    fun deleteMission(mission: Mission) {
        viewModelScope.launch {
            repository.deleteMission(mission)
        }
    }
}
