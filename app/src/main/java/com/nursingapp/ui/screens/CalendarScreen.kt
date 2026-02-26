package com.nursingapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nursingapp.data.ActivityRepository
import com.nursingapp.data.allActivityItems
import com.nursingapp.ui.components.ActivityCard
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    // Helper function to ensure EVERY date check in this file uses UTC
    fun getFormattedDate(millis: Long?): String {
        if (millis == null) return ""
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return sdf.format(Date(millis))
    }

    // 1. Reactive filter using the UTC helper
    val activitiesForDay by remember {
        derivedStateOf {
            val dateString = getFormattedDate(datePickerState.selectedDateMillis)
            if (dateString.isEmpty()) return@derivedStateOf emptyList()

            ActivityRepository.customActivities.filter { it.scheduledDate == dateString }
        }
    }

    // 2. Format the label for the UI title using the same UTC logic
    val selectedDateLabel = remember(datePickerState.selectedDateMillis) {
        if (datePickerState.selectedDateMillis == null) "Selected Day"
        else getFormattedDate(datePickerState.selectedDateMillis)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activity Calendar", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            // 1. Remove the weight from here.
            // Let the DatePicker define its own height so the Divider sits right under it.
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                title = null,
                headline = null,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

            // 2. Apply the weight here.
            // This Column will now take up every pixel left over from the bottom of the Divider to the Navbar.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Activities for $selectedDateLabel",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.primary
                )

                if (activitiesForDay.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "No activities scheduled.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        // This stays the same to allow scrolling behind the Navbar
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = innerPadding.calculateBottomPadding() + 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(activitiesForDay, key = { it.id }) { item ->
                            Box {
                                ActivityCard(
                                    item = item,
                                    onDeleteClick = { /* Handled by list screen */ }
                                )
                                TextButton(
                                    onClick = {
                                        ActivityRepository.unscheduleActivity(context, item)
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(end = 40.dp, top = 0.dp)
                                ) {
                                    Icon(
                                        Icons.Default.EventBusy,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text("Unschedule", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}