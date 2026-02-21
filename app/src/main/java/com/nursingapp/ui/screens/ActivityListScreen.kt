package com.nursingapp.ui.screens

import android.app.Activity
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nursingapp.data.ActivityCategory
import com.nursingapp.data.ActivityItem
import com.nursingapp.data.allActivityItems
import com.nursingapp.ui.components.ActivityCard
import com.nursingapp.ui.theme.NursingAppTheme

private enum class ActivityFilter(val label: String, val category: ActivityCategory?) {
    ALL("All", null),
    ART_CRAFTS("Art & Crafts", ActivityCategory.ART_CRAFTS),
    BAKING_COOKING("Baking & Cooking", ActivityCategory.BAKING_COOKING),
    MUSIC("Music", ActivityCategory.MUSIC),
    GAMES("Games", ActivityCategory.GAMES),
    EXERCISE("Exercise", ActivityCategory.EXERCISE),
}

/**
 * Full-screen list of all activity suggestions, filterable by category.
 * A print action button lets coordinators print the activity list with supplies.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityListScreen(modifier: Modifier = Modifier) {
    var selectedFilter by rememberSaveable { mutableStateOf(ActivityFilter.ALL) }
    val context = LocalContext.current

    val filteredItems = if (selectedFilter.category == null) {
        allActivityItems
    } else {
        allActivityItems.filter { it.category == selectedFilter.category }
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
                    IconButton(onClick = { printActivityList(context as Activity, filteredItems) }) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = "Print activity list",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // Filter chips
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

            // Count row
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

            // Activity cards
            items(filteredItems, key = { it.id }) { activityItem ->
                ActivityCard(item = activityItem)
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
            <p style="color:#555;">Printed: ${java.util.Date()}</p>
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
        ActivityListScreen()
    }
}
