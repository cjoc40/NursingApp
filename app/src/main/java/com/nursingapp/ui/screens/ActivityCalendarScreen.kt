package com.nursingapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nursingapp.data.ActivityRepository
import com.nursingapp.data.allActivityItems
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityCalendarScreen(
    activityId: Int,
    onScheduleConfirmed: () -> Unit
) {
    val context = LocalContext.current
    val datePickerState = rememberDatePickerState()

    // --- NEW: Time Picker State ---
    val timePickerState = rememberTimePickerState(initialHour = 12, initialMinute = 0)
    var showTimePicker by remember { mutableStateOf(false) }

    val activityToSchedule = remember(activityId) {
        (allActivityItems + ActivityRepository.customActivities).find { it.id == activityId }
    }
    val selectedMillis = datePickerState.selectedDateMillis

    // Time Picker Dialog
    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    if (selectedMillis != null && activityToSchedule != null) {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                            timeZone = TimeZone.getTimeZone("UTC")
                        }
                        val dateString = sdf.format(Date(selectedMillis))

                        // Format the specific activity time
                        val timeString = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)

                        // FIX: Pass all three parameters now
                        ActivityRepository.scheduleActivity(context, activityToSchedule, dateString, timeString)
                        onScheduleConfirmed()
                    }
                    showTimePicker = false
                }) { Text("Confirm All") }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule Activity") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (activityToSchedule == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Activity not found.") }
            } else {
                // Header
                Card(
                    modifier = Modifier.padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Scheduling: ${activityToSchedule.name}", fontWeight = FontWeight.Bold)
                    }
                }

                // Date Picker
                DatePicker(state = datePickerState, showModeToggle = false, modifier = Modifier.weight(1f))

                // Footer with "Next" or "Confirm"
                Surface(tonalElevation = 8.dp, shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        if (selectedMillis != null) {
                            Button(
                                onClick = { showTimePicker = true }, // Trigger Time Picker next
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Check, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Select Time & Confirm")
                            }
                        } else {
                            Text("Please tap a date above to continue.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = { TextButton(onClick = onDismissRequest) { Text("Cancel") } },
        text = { content() }
    )
}