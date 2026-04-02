package com.lucio.expensetracker.ui.mission_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucio.expensetracker.domain.model.Expense
import com.lucio.expensetracker.domain.model.Mission
import com.lucio.expensetracker.domain.repository.ExpenseRepository
import com.lucio.expensetracker.domain.repository.MissionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MissionDetailsUiState(
    val mission: Mission? = null,
    val expenses: List<Expense> = emptyList(),
    val isLoading: Boolean = true
)

class MissionDetailsViewModel(
    private val missionId: Long,
    private val missionRepository: MissionRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MissionDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMissionDetails()
    }

    private fun loadMissionDetails() {
        viewModelScope.launch {
            val mission = missionRepository.getMissionById(missionId)
            expenseRepository.getExpensesByMissionId(missionId).collect { expenses ->
                _uiState.update { 
                    it.copy(
                        mission = mission,
                        expenses = expenses,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expense)
        }
    }
}
