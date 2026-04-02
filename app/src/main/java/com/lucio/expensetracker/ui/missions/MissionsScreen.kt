package com.lucio.expensetracker.ui.missions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lucio.expensetracker.domain.model.Mission
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionsScreen(
    viewModel: MissionsViewModel,
    onBackClick: () -> Unit,
    onMissionClick: (Long) -> Unit
) {
    val missions by viewModel.missions.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingMission by remember { mutableStateOf<Mission?>(null) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Missions") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Mission")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(missions) { mission ->
                MissionItem(
                    mission = mission,
                    onEdit = { editingMission = mission },
                    onDelete = { viewModel.deleteMission(mission) },
                    onGeneratePdf = { viewModel.generatePdfForMission(context, mission) },
                    onViewDetails = { onMissionClick(mission.id) }
                )
            }
        }

        if (showAddDialog) {
            MissionDialog(
                title = "Add New Mission",
                onDismiss = { showAddDialog = false },
                onConfirm = { name, start, end ->
                    viewModel.addMission(name, start, end)
                    showAddDialog = false
                }
            )
        }

        editingMission?.let { mission ->
            MissionDialog(
                title = "Update Mission",
                initialName = mission.name,
                initialStartDate = mission.startDate,
                initialEndDate = mission.endDate,
                confirmLabel = "Update",
                onDismiss = { editingMission = null },
                onConfirm = { name, start, end ->
                    viewModel.updateMission(mission.copy(name = name, startDate = start, endDate = end))
                    editingMission = null
                }
            )
        }
    }
}

@Composable
fun MissionItem(
    mission: Mission,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onGeneratePdf: () -> Unit,
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mission.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${formatDate(mission.startDate)} - ${formatDate(mission.endDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row {
                IconButton(onClick = onGeneratePdf) {
                    Icon(
                        Icons.Default.PictureAsPdf,
                        contentDescription = "Generate PDF",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(onClick = onViewDetails) {
                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = "View Details",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionDialog(
    title: String,
    initialName: String = "",
    initialStartDate: Long = System.currentTimeMillis(),
    initialEndDate: Long = System.currentTimeMillis() + 86400000 * 7,
    confirmLabel: String = "Add",
    onDismiss: () -> Unit,
    onConfirm: (String, Long, Long) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    // In a real app, use a DatePicker.
    val startDate = initialStartDate
    val endDate = initialEndDate

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Mission Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Date: ${formatDate(startDate)} - ${formatDate(endDate)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name, startDate, endDate) },
                enabled = name.isNotBlank()
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
