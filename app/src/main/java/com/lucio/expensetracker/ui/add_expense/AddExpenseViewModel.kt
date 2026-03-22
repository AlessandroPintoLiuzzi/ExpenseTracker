package com.lucio.expensetracker.ui.add_expense

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucio.expensetracker.domain.model.Expense
import com.lucio.expensetracker.domain.model.Mission
import com.lucio.expensetracker.domain.repository.ExpenseRepository
import com.lucio.expensetracker.domain.repository.MissionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class AddExpenseUiState(
    val amount: String = "",
    val category: String = "Transport",
    val receiptUri: Uri? = null,
    val selectedMissionId: Long? = null,
    val missions: List<Mission> = emptyList(),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

class AddExpenseViewModel(
    private val expenseRepository: ExpenseRepository,
    private val missionRepository: MissionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState = _uiState.asStateFlow()

    init {
        missionRepository.getAllMissions()
            .onEach { missions ->
                _uiState.update { it.copy(missions = missions) }
            }.launchIn(viewModelScope)
    }

    fun onAmountChange(amount: String) {
        _uiState.update { it.copy(amount = amount) }
    }

    fun onCategoryChange(category: String) {
        _uiState.update { it.copy(category = category) }
    }

    fun onMissionChange(missionId: Long) {
        _uiState.update { it.copy(selectedMissionId = missionId) }
    }

    fun onReceiptCaptured(uri: Uri) {
        _uiState.update { it.copy(receiptUri = uri) }
    }

    fun saveExpense() {
        val amountDouble = uiState.value.amount.toDoubleOrNull()
        if (amountDouble == null) {
            _uiState.update { it.copy(error = "Invalid amount") }
            return
        }

        val missionId = uiState.value.selectedMissionId
        if (missionId == null) {
            _uiState.update { it.copy(error = "Please select a mission") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val expense = Expense(
                    amount = amountDouble,
                    category = uiState.value.category,
                    date = System.currentTimeMillis(),
                    receiptPath = uiState.value.receiptUri?.toString(),
                    missionId = missionId
                )
                expenseRepository.insertExpense(expense)
                _uiState.update { it.copy(isSaved = true, isSaving = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isSaving = false) }
            }
        }
    }

    fun createPhotoFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile("RECEIPT_${timeStamp}_", ".jpg", storageDir)
    }
}
