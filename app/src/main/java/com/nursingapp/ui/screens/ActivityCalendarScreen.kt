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
import com.nursingapp.data.HolidayRepository
import com.nursingapp.data.allActivityItems
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityCalendarScreen(
    activityId: Int, // Received from navigation
    onScheduleConfirmed: () -> Unit
) {
    val context = LocalContext.current
    val datePickerState = rememberDatePickerState()

    // Find the specific activity the user is trying to schedule
    val activityToSchedule = remember(activityId) {
        (allActivityItems + ActivityRepository.customActivities).find { it.id == activityId }
    }
    val selectedMillis = datePickerState.selectedDateMillis

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule Activity") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (activityToSchedule == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Activity not found.")
                }
            } else {
                // 1. Header showing what we are scheduling
                Card(
                    modifier = Modifier.padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Scheduling: ${activityToSchedule.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = activityToSchedule.category.displayName,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // 2. The Calendar View
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false,
                    modifier = Modifier.weight(1f) // Takes up available space
                )

                // 3. Selection & Confirmation
                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        if (selectedMillis != null) {
                            // FIX: Force UTC to prevent the date from shifting to the previous day
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                                timeZone = TimeZone.getTimeZone("UTC")
                            }
                            val dateString = sdf.format(Date(selectedMillis))

                            // Check for holiday
                            val monthDaySdf = SimpleDateFormat("MM-dd", Locale.getDefault()).apply {
                                timeZone = TimeZone.getTimeZone("UTC")
                            }
                            val holiday = HolidayRepository.getHolidayForDate(monthDaySdf.format(Date(selectedMillis)))

                            if (holiday != null) {
                                Text(
                                    "🌟 Note: This is ${holiday.name}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            Text(
                                "Target Date: $dateString",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    // SAVE to Repository
                                    ActivityRepository.scheduleActivity(context, activityToSchedule, dateString)
                                    onScheduleConfirmed() // Go back to list
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Confirm Selection")
                            }
                        } else {
                            Text(
                                "Please tap a date above to schedule this activity.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}