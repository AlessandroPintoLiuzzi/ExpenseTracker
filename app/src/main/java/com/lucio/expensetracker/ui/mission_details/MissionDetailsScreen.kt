package com.lucio.expensetracker.ui.mission_details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lucio.expensetracker.domain.model.Expense
import com.lucio.expensetracker.ui.dashboard.BudgetSummaryCard
import com.lucio.expensetracker.ui.dashboard.DashboardUiState
import com.lucio.expensetracker.ui.dashboard.ExpenseDetailsDialog
import com.lucio.expensetracker.ui.dashboard.ExpenseItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionDetailsScreen(
    viewModel: MissionDetailsViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var expenseToShowDetails by remember { mutableStateOf<Expense?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.mission?.name ?: "Mission Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    val mission = uiState.mission
                    if (mission != null) {
                        BudgetSummaryCard(
                            uiState = DashboardUiState(
                                totalTransport = uiState.expenses.filter { it.category == "Transport" }.sumOf { it.amount },
                                totalFood = uiState.expenses.filter { it.category == "Food" }.sumOf { it.amount },
                                totalHotel = uiState.expenses.filter { it.category == "Hotel" }.sumOf { it.amount },
                                totalOthers = uiState.expenses.filter { it.category !in listOf("Transport", "Food", "Hotel") }.sumOf { it.amount }
                            )
                        )
                    }
                }
                item {
                    Text(
                        text = "Expenses for this Mission",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(uiState.expenses) { expense ->
                    ExpenseItem(
                        expense = expense,
                        onDelete = { viewModel.deleteExpense(expense) },
                        onViewDetails = { expenseToShowDetails = expense }
                    )
                }
            }
        }

        expenseToShowDetails?.let { expense ->
            ExpenseDetailsDialog(
                expense = expense,
                onDismiss = { expenseToShowDetails = null }
            )
        }
    }
}
