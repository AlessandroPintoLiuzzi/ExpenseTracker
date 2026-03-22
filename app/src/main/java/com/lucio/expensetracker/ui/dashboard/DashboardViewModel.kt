package com.lucio.expensetracker.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucio.expensetracker.domain.model.Expense
import com.lucio.expensetracker.domain.repository.ExpenseRepository
import com.lucio.expensetracker.domain.repository.MissionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val expenseRepository: ExpenseRepository,
    private val missionRepository: MissionRepository
) : ViewModel() {

    private val _selectedMissionId = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<DashboardUiState> = combine(
        expenseRepository.getAllExpenses(),
        missionRepository.getAllMissions(),
        _selectedMissionId
    ) { allExpenses, missions, selectedId ->
        val filteredExpenses = if (selectedId == null) {
            allExpenses
        } else {
            allExpenses.filter { it.missionId == selectedId }
        }

        DashboardUiState(
            expenses = filteredExpenses,
            missions = missions,
            selectedMissionId = selectedId,
            totalTransport = filteredExpenses.filter { it.category == "Transport" }.sumOf { it.amount },
            totalFood = filteredExpenses.filter { it.category == "Food" }.sumOf { it.amount },
            totalHotel = filteredExpenses.filter { it.category == "Hotel" }.sumOf { it.amount },
            totalOthers = filteredExpenses.filter { it.category !in listOf("Transport", "Food", "Hotel") }.sumOf { it.amount },
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )

    fun onMissionSelected(missionId: Long?) {
        _selectedMissionId.value = missionId
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expense)
        }
    }
}
