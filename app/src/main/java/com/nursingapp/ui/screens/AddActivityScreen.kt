package com.nursingapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nursingapp.data.ActivityCategory
import com.nursingapp.data.ActivityRepository
import com.nursingapp.data.MobilityLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityScreen(onActivityAdded: () -> Unit) {
    val context = LocalContext.current

    // State for each field
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("30–45 min") }
    var suppliesText by remember { mutableStateOf("") } // We will split this by commas

    // Dropdown States
    var selectedCategory by remember { mutableStateOf(ActivityCategory.ART_CRAFTS) }
    var selectedMobility by remember { mutableStateOf(MobilityLevel.SEATED) }

    var categoryExpanded by remember { mutableStateOf(false) }
    var mobilityExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState) // Added scrolling for small screens
    ) {
        Text("Add New Activity", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // Basic Info
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Activity Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category Dropdown
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = categoryExpanded.let { selectedCategory.displayName },
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                ActivityCategory.entries.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.displayName) },
                        onClick = {
                            selectedCategory = category
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mobility Dropdown
        ExposedDropdownMenuBox(
            expanded = mobilityExpanded,
            onExpandedChange = { mobilityExpanded = !mobilityExpanded }
        ) {
            OutlinedTextField(
                value = mobilityExpanded.let { "${selectedMobility.emoji} ${selectedMobility.displayName}" },
                onValueChange = {},
                readOnly = true,
                label = { Text("Mobility Level") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mobilityExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = mobilityExpanded,
                onDismissRequest = { mobilityExpanded = false }
            ) {
                MobilityLevel.entries.forEach { level ->
                    DropdownMenuItem(
                        text = { Text("${level.emoji} ${level.displayName}") },
                        onClick = {
                            selectedMobility = level
                            mobilityExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = duration,
            onValueChange = { duration = it },
            label = { Text("Duration (e.g. 30–45 min)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = suppliesText,
            onValueChange = { suppliesText = it },
            label = { Text("Supplies (separate with commas)") },
            placeholder = { Text("Paper, Scissors, Glue...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    // Convert the comma-separated string into a List<String>
                    val suppliesList = suppliesText.split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }

                    // Pass ALL parameters to your repository
                    ActivityRepository.addActivity(
                        context = context,
                        name = name,
                        description = description,
                        duration = duration,
                        mobility = selectedMobility,
                        category = selectedCategory,
                        supplies = suppliesList
                    )
                    onActivityAdded()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Activity")
        }
    }
}