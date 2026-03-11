package com.nursingapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nursingapp.data.ActivityRepository
import com.nursingapp.data.allActivityItems
import com.nursingapp.ui.components.ActivityCard
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    fun getFormattedDate(millis: Long?): String {
        if (millis == null) return ""
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return sdf.format(Date(millis))
    }

    val selectedDateString = getFormattedDate(datePickerState.selectedDateMillis)

    val activitiesForDay by remember {
        derivedStateOf {
            if (selectedDateString.isEmpty()) return@derivedStateOf emptyList()

            val scheduledCustomItems = ActivityRepository.customActivities
                .filter { it.scheduledDate == selectedDateString }

            val scheduledDefaultItems = allActivityItems
                .filter { it.scheduledDate == selectedDateString }

            (scheduledCustomItems + scheduledDefaultItems).distinctBy { it.id }
        }
    }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Activity Calendar", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("add_activity?date=$selectedDateString")
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Activity")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false,
                    title = null,
                    headline = null,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Activities for ${if (selectedDateString.isEmpty()) "Selected Day" else selectedDateString}",
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
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(activitiesForDay, key = { it.id }) { item ->
                                Box {
                                    ActivityCard(
                                        item = item,
                                        onDeleteClick = { ActivityRepository.deleteActivity(context, item) }
                                    )
                                    // The Unschedule Button
                                    TextButton(
                                        onClick = { ActivityRepository.unscheduleActivity(context, item) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(end = 40.dp, top = 8.dp)
                                    ) {
                                        Icon(Icons.Default.EventBusy, null, modifier = Modifier.size(18.dp))
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