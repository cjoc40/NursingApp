package com.nursingapp.ui.screens

import android.app.Activity
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nursingapp.data.ActivityCategory
import com.nursingapp.data.ActivityItem
import com.nursingapp.data.ActivityRepository
import com.nursingapp.data.allActivityItems
import com.nursingapp.ui.components.ActivityCard
import com.nursingapp.ui.theme.NursingAppTheme
import kotlinx.coroutines.launch
import java.util.Date

private enum class ActivityFilter(val label: String, val category: ActivityCategory?) {
    ALL("All", null),
    ART_CRAFTS("Art & Crafts", ActivityCategory.ART_CRAFTS),
    BAKING_COOKING("Baking & Cooking", ActivityCategory.BAKING_COOKING),
    MUSIC("Music", ActivityCategory.MUSIC),
    GAMES("Games", ActivityCategory.GAMES),
    EXERCISE("Exercise", ActivityCategory.EXERCISE),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityListScreen(
    modifier: Modifier = Modifier,
    onNavigateToAddActivity: () -> Unit
) {
    var selectedFilter by rememberSaveable { mutableStateOf(ActivityFilter.ALL) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val combinedActivities = allActivityItems + ActivityRepository.customActivities

    val filteredItems = if (selectedFilter.category == null) {
        combinedActivities
    } else {
        combinedActivities.filter { it.category == selectedFilter.category }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Activities",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                },
                actions = {
                    // NEW: Refresh Button to fetch from "API"
                    IconButton(onClick = {
                        scope.launch {
                            // You can implement fetchNewActivity in ActivityRepository
                            // ActivityRepository.fetchNewActivity(context)
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh Activities")
                    }

                    IconButton(onClick = { printActivityList(context as Activity, filteredItems) }) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = "Print")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
            )
        },
        floatingActionButton = {
            // NEW: Add Button
            FloatingActionButton(
                onClick = onNavigateToAddActivity,
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Activity")
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                ) {
                    items(ActivityFilter.entries) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter.label) },
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        text = "${filteredItems.size} activities",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            items(filteredItems, key = { it.id }) { activityItem ->
                ActivityCard(
                    item = activityItem,
                    onDeleteClick = { itemToDelete ->
                        ActivityRepository.deleteActivity(context, itemToDelete)
                    }
                )
            }
        }
    }
}

/** Generates an HTML representation of the activity list and sends it to the system print dialog. */
private fun printActivityList(activity: Activity, items: List<ActivityItem>) {
    val html = buildString {
        append(
            """
            <!DOCTYPE html><html><head>
            <meta charset="utf-8"/>
            <style>
              body { font-family: Arial, sans-serif; margin: 32px; color: #222; }
              h1 { color: #4A9E94; }
              .card { border: 1px solid #ccc; border-radius: 8px; padding: 12px 16px; margin-bottom: 16px; page-break-inside: avoid; }
              .category { font-size: 11px; font-weight: bold; color: #4A9E94; text-transform: uppercase; margin-bottom: 6px; }
              .name { font-size: 16px; font-weight: bold; margin-bottom: 6px; }
              .meta { font-size: 12px; color: #666; margin-bottom: 8px; }
              .description { font-size: 13px; margin-bottom: 8px; }
              .supplies { font-size: 13px; }
              .supplies ul { margin: 4px 0; padding-left: 20px; }
            </style>
            </head><body>
            <h1>Nursing App – Activity List</h1>
            <p style="color:#555;">Printed: ${Date()}</p>
            """.trimIndent(),
        )
        items.forEach { item ->
            append("""<div class="card">""")
            append("""<div class="category">${item.category.displayName}</div>""")
            append("""<div class="name">${item.name}</div>""")
            append("""<div class="meta">⏱ ${item.duration} &nbsp;|&nbsp; ${item.mobilityRequired.emoji} ${item.mobilityRequired.displayName}</div>""")
            append("""<div class="description">${item.description}</div>""")
            append("""<div class="supplies"><strong>Supplies:</strong><ul>""")
            item.supplies.forEach { supply -> append("<li>$supply</li>") }
            append("</ul></div>")
            append("</div>")
        }
        append("</body></html>")
    }

    val webView = WebView(activity)
    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            val printManager = activity.getSystemService(Activity.PRINT_SERVICE) as PrintManager
            val jobName = "NursingApp Activity List"
            val printAdapter = webView.createPrintDocumentAdapter(jobName)
            printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
        }
    }
    webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
}

@Preview(showBackground = true)
@Composable
private fun ActivityListScreenPreview() {
    NursingAppTheme {
        ActivityListScreen(
            modifier = Modifier.fillMaxSize(),
            onNavigateToAddActivity = { /* Do nothing in preview */ }
        )
    }
}
