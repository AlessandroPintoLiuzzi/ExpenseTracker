package com.lucio.expensetracker.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.lucio.expensetracker.domain.model.Expense
import com.lucio.expensetracker.ui.theme.ExpenseTrackerTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onAddExpenseClick: () -> Unit,
    onMissionsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var expenseToShowDetails by remember { mutableStateOf<Expense?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ExpenseTracker") },
                actions = {
                    IconButton(onClick = onMissionsClick) {
                        Icon(Icons.Default.Assignment, contentDescription = "Missions")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExpenseClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { paddingValues ->
        DashboardContent(
            uiState = uiState,
            onMissionSelected = { viewModel.onMissionSelected(it) },
            onDeleteExpense = { viewModel.deleteExpense(it) },
            onViewExpenseDetails = { expenseToShowDetails = it },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )

        expenseToShowDetails?.let { expense ->
            ExpenseDetailsDialog(
                expense = expense,
                onDismiss = { expenseToShowDetails = null }
            )
        }
    }
}

@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    onMissionSelected: (Long?) -> Unit,
    onDeleteExpense: (Expense) -> Unit,
    onViewExpenseDetails: (Expense) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            MissionFilter(
                uiState = uiState,
                onMissionSelected = onMissionSelected
            )
        }
        item {
            BudgetSummaryCard(uiState)
        }
        item {
            Text(
                text = "Expense History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        items(uiState.expenses) { expense ->
            ExpenseItem(
                expense = expense,
                onDelete = { onDeleteExpense(expense) },
                onViewDetails = { onViewExpenseDetails(expense) }
            )
        }
    }
}

@Composable
fun MissionFilter(
    uiState: DashboardUiState,
    onMissionSelected: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedCard(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Current Mission",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = uiState.selectedMissionName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            DropdownMenuItem(
                text = { Text("All Missions") },
                onClick = {
                    onMissionSelected(null)
                    expanded = false
                }
            )
            uiState.missions.forEach { mission ->
                DropdownMenuItem(
                    text = { Text(mission.name) },
                    onClick = {
                        onMissionSelected(mission.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun BudgetSummaryCard(uiState: DashboardUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Spending",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$${String.format("%.2f", uiState.totalSpending)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CategorySummaryItem("Transport", uiState.totalTransport, Icons.Default.Train)
                CategorySummaryItem("Food", uiState.totalFood, Icons.Default.Fastfood)
                CategorySummaryItem("Hotel", uiState.totalHotel, Icons.Default.Hotel)
            }
        }
    }
}

@Composable
fun CategorySummaryItem(label: String, amount: Double, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.secondary
        )
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Text(
            text = "$${String.format("%.0f", amount)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ExpenseItem(
    expense: Expense,
    onDelete: () -> Unit,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                val icon = when (expense.category) {
                    "Transport" -> Icons.Default.Train
                    "Food" -> Icons.Default.Fastfood
                    "Hotel" -> Icons.Default.Hotel
                    else -> Icons.Default.MoreHoriz
                }
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxSize(),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = expense.category,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = formatDate(expense.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "-$${String.format("%.2f", expense.amount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                IconButton(onClick = onViewDetails) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "View Details",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Expense",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun ExpenseDetailsDialog(
    expense: Expense,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "${expense.category} Expense") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Amount: $${String.format("%.2f", expense.amount)}", fontWeight = FontWeight.Bold)
                Text(text = "Date: ${formatDate(expense.date)}")
                
                if (expense.receiptPath != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Receipt:", style = MaterialTheme.typography.labelLarge)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = expense.receiptPath),
                            contentDescription = "Receipt Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Text(text = "No receipt photo available.", style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    ExpenseTrackerTheme {
        DashboardContent(
            uiState = DashboardUiState(
                expenses = listOf(
                    Expense(1, 25.0, "Transport", System.currentTimeMillis(), null, 1L),
                    Expense(2, 15.5, "Food", System.currentTimeMillis() - 86400000, null, 1L),
                    Expense(3, 120.0, "Hotel", System.currentTimeMillis() - 172800000, null, 1L)
                ),
                totalTransport = 25.0,
                totalFood = 15.5,
                totalHotel = 120.0
            ),
            onMissionSelected = {},
            onDeleteExpense = {},
            onViewExpenseDetails = {}
        )
    }
}
