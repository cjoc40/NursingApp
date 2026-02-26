package com.nursingapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nursingapp.data.ActivityRepository
import com.nursingapp.data.HolidayRepository
import com.nursingapp.data.allActivityItems
import com.nursingapp.ui.components.ActivityCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityCalendarScreen() {
    val datePickerState = rememberDatePickerState()
    val context = LocalContext.current

    // Get all activities that have a date assigned
    val scheduledActivities = (allActivityItems + ActivityRepository.customActivities)
        .filter { it.scheduledDate != null }

    Column(modifier = Modifier.fillMaxSize()) {
        DatePicker(
            state = datePickerState,
            showModeToggle = false,
            title = { Text("Activity Schedule", modifier = Modifier.padding(16.dp)) }
        )

        val selectedMillis = datePickerState.selectedDateMillis
        if (selectedMillis != null) {
            val dateFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                Date(
                    selectedMillis
                )
            )
            val monthDayFormatted = SimpleDateFormat("MM-dd", Locale.getDefault()).format(Date(selectedMillis))

            val holiday = HolidayRepository.getHolidayForDate(monthDayFormatted)
            val activitiesForDay = scheduledActivities.filter { it.scheduledDate == dateFormatted }

            LazyColumn(modifier = Modifier.padding(16.dp)) {
                // Show Special Day / Holiday First
                if (holiday != null) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            Text("🌟 Special Day: ${holiday.name}", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Show scheduled activities
                items(activitiesForDay) { activity ->
                    ActivityCard(item = activity, onDeleteClick = {})
                }

                if (holiday == null && activitiesForDay.isEmpty()) {
                    item { Text("No activities scheduled for this day.", style = MaterialTheme.typography.bodyMedium) }
                }
            }
        }
    }
}