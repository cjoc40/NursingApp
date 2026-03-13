package com.nursingapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nursingapp.data.ActivityCategory
import com.nursingapp.data.ActivityRepository
import com.nursingapp.data.MobilityLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityScreen(
    initialDate: String? = null,
    onBack: () -> Unit,
    onActivityAdded: () -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("30–45 min") }
    var suppliesText by remember { mutableStateOf("") }
    var instructionsText by remember { mutableStateOf("") }
    var benefitsText by remember { mutableStateOf("") }

    var selectedCategory by remember { mutableStateOf(ActivityCategory.ART_CRAFTS) }
    var selectedMobility by remember { mutableStateOf(MobilityLevel.SEATED) }

    var categoryExpanded by remember { mutableStateOf(false) }
    var mobilityExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Activity") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            if (initialDate != null) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Scheduling for: $initialDate",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Activity Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory.displayName,
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

            ExposedDropdownMenuBox(
                expanded = mobilityExpanded,
                onExpandedChange = { mobilityExpanded = !mobilityExpanded }
            ) {
                OutlinedTextField(
                    value = "${selectedMobility.emoji} ${selectedMobility.displayName}",
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
                value = instructionsText,
                onValueChange = { instructionsText = it },
                label = { Text("Instructions (one step per line)") },
                placeholder = { Text("Step 1\nStep 2\nStep 3...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = benefitsText,
                onValueChange = { benefitsText = it },
                label = { Text("Benefits (separate with commas)") },
                placeholder = { Text("Memory recall, Social pride...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Brief Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val suppliesList = suppliesText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        val benefitsList = benefitsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        val instructionsList = instructionsText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }

                        ActivityRepository.addActivity(
                            context = context,
                            name = name,
                            description = description,
                            duration = duration,
                            mobility = selectedMobility,
                            category = selectedCategory,
                            supplies = suppliesList,
                            instructions = instructionsList,
                            benefits = benefitsList,
                            date = initialDate
                        )
                        onActivityAdded()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Activity")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}