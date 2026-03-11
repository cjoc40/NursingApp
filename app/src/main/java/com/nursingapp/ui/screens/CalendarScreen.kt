package com.nursingapp.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nursingapp.data.ActivityRepository
import com.nursingapp.data.AlarmReceiver
import com.nursingapp.ui.components.ActivityCard
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> /* */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    var selectedDate by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }

    var calendarMonth by remember { mutableStateOf(Calendar.getInstance()) }

    val activitiesForDay by remember(selectedDate, ActivityRepository.customActivities.size) {
        derivedStateOf {
            ActivityRepository.customActivities
                .filter { it.scheduledDate == selectedDate }
                .distinctBy { it.id }
        }
    }

    var showTimePicker by remember { mutableStateOf(false) }
    var reminderTime by remember {
        mutableStateOf(ActivityRepository.getReminderTime(context))
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = reminderTime.split(":")[0].toInt(),
            initialMinute = reminderTime.split(":")[1].toInt()
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    reminderTime = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                    ActivityRepository.updateGlobalReminderTime(context, reminderTime)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            title = { Text("Default Reminder Time") },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    // Inside CalendarScreen, inside the AlertDialog 'text' Box
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TimePicker(state = timePickerState)

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                // Trigger an immediate notification for testing
                                val intent = android.content.Intent(context, AlarmReceiver::class.java).apply {
                                    putExtra("activity_name", "Test Activity ✅")
                                }
                                context.sendBroadcast(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Send Test Notification")
                        }
                    }
                }
            }

        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activity Calendar", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showTimePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Reminder Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_activity?date=$selectedDate") },
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
            CompactCalendar(
                currentMonth = calendarMonth,
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it },
                onMonthChange = {
                    val newMonth = calendarMonth.clone() as Calendar
                    newMonth.add(Calendar.MONTH, it)
                    calendarMonth = newMonth
                }
            )
            Text(
                text = "Reminders set for $reminderTime",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Activities for $selectedDate",
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
                            ActivityCard(
                                item = item,
                                onDeleteClick = { itemToDelete ->
                                    ActivityRepository.unscheduleActivity(context, itemToDelete)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun CompactCalendar(
    currentMonth: Calendar,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    onMonthChange: (Int) -> Unit
) {
    val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentMonth.time)

    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = (currentMonth.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, 1)
    }.get(Calendar.DAY_OF_WEEK) - 1

    Column(modifier = Modifier.padding(16.dp)) {
        // Month Header with Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChange(-1) }) {
                Icon(Icons.Default.ChevronLeft, "Prev")
            }
            Text(
                text = monthName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { onMonthChange(1) }) {
                Icon(Icons.Default.ChevronRight, "Next")
            }
        }

        // Day Letters
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Grid of Days
        var dayCounter = 1
        for (row in 0..5) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0..6) {
                    val cellIndex = row * 7 + col
                    if (cellIndex < firstDayOfWeek || dayCounter > daysInMonth) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val currentDay = dayCounter
                        val dateStr = String.format(
                            "%04d-%02d-%02d",
                            currentMonth.get(Calendar.YEAR),
                            currentMonth.get(Calendar.MONTH) + 1,
                            currentDay
                        )
                        CalendarDayCell(
                            day = currentDay,
                            dateStr = dateStr, // Pass it here
                            isSelected = dateStr == selectedDate,
                            hasEvent = ActivityRepository.hasActivitiesOnDate(dateStr),
                            modifier = Modifier.weight(1f),
                            onClick = { onDateSelected(dateStr) }
                        )
                        dayCounter++
                    }
                }
            }
            if (dayCounter > daysInMonth) break
        }
    }
}

@Composable
fun CalendarDayCell(
    day: Int,
    isSelected: Boolean,
    dateStr: String,
    hasEvent: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val eventCount = remember(ActivityRepository.customActivities.toList(), dateStr) {
        ActivityRepository.customActivities.count { it.scheduledDate == dateStr }
    }
    val hasEvent = eventCount > 0
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = day.toString(),
                    fontSize = 14.sp,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
        if (hasEvent) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(14.dp)
                    .background(MaterialTheme.colorScheme.error, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = eventCount.toString(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = LocalTextStyle.current.copy(
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        lineHeight = 9.sp
                    ),
                    modifier = Modifier.offset(y = (-0.5).dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.size(16.dp))
        }
    }
}
