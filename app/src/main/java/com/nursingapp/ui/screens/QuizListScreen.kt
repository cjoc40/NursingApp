package com.nursingapp.ui.screens

import android.app.Activity
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nursingapp.data.QuizCategory
import com.nursingapp.data.QuizItem
import com.nursingapp.data.allQuizItems
import com.nursingapp.data.SongRepository
import com.nursingapp.ui.components.QuizCard
import com.nursingapp.ui.theme.NursingAppTheme
import kotlinx.coroutines.launch
import java.util.Date

// Removed ALL from the enum
private enum class QuizFilter(val label: String) {
    TRIVIA("Trivia"),
    GUESS_THE_SONG("Guess the Song"),
}

@Composable
fun SongQuizScreen(
    onAddSongClick: () -> Unit
) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = onAddSongClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("➕ Add Song")
        }

        LazyColumn {
            items(SongRepository.songs, key = { it.id }) { song ->
                QuizCard(
                    item = song,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    onDeleteClick = { itemToDelete ->
                        SongRepository.deleteSong(context, itemToDelete)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizListScreen(modifier: Modifier = Modifier, onNavigateToAddSong: () -> Unit) {
    // Defaulting to Trivia since "All" is removed
    var selectedFilter by rememberSaveable { mutableStateOf(QuizFilter.TRIVIA) }
    val context = LocalContext.current

    val combinedItems = allQuizItems + SongRepository.songs

    val filteredItems = when (selectedFilter) {
        QuizFilter.TRIVIA -> combinedItems.filter { it.category == QuizCategory.TRIVIA }
        QuizFilter.GUESS_THE_SONG -> combinedItems.filter { it.category == QuizCategory.GUESS_THE_SONG }
    }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Quizzes",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                },
                actions = {
                    IconButton(onClick = { printQuizList(context as Activity, filteredItems) }) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = "Print")
                    }

                    IconButton(onClick = {
                        scope.launch { SongRepository.fetchNewTrivia(context) }
                    }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Get New Trivia")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddSong,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Song")
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp // Adds Navbar height + extra gap
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
        ) {
            // Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(QuizFilter.entries) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter.label) },
                        )
                    }
                }
            }

            // Count summary
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        text = "${filteredItems.size} items",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Filtered results
            items(filteredItems, key = { it.id }) { quizItem ->
                QuizCard(
                    item = quizItem,
                    onDeleteClick = { itemToDelete ->
                        SongRepository.deleteSong(context, itemToDelete)
                    }
                )
            }
        }
    }
}

/** Generates an HTML representation of the quiz list and sends it to the system print dialog. */
private fun printQuizList(activity: Activity, items: List<QuizItem>) {
    val html = buildString {
        append(
            """
            <!DOCTYPE html><html><head>
            <meta charset="utf-8"/>
            <style>
              body { font-family: Arial, sans-serif; margin: 32px; color: #222; }
              h1 { color: #6650A4; }
              .card { border: 1px solid #ccc; border-radius: 8px; padding: 12px 16px; margin-bottom: 16px; }
              .category { font-size: 11px; font-weight: bold; color: #6650A4; text-transform: uppercase; margin-bottom: 6px; }
              .question { font-size: 15px; font-weight: 600; margin-bottom: 4px; }
              .answer { font-size: 14px; color: #555; border-top: 1px dashed #ccc; padding-top: 8px; margin-top: 8px; }
              .hint { font-size: 12px; font-style: italic; color: #888; }
            </style>
            </head><body>
            <h1>Nursing App – Quiz List</h1>
            <p style="color:#555;">Printed: ${Date()}</p>
            """.trimIndent(),
        )
        items.forEach { item ->
            append("""<div class="card">""")
            append("""<div class="category">${item.category.displayName}</div>""")
            append("""<div class="question">${item.question}</div>""")
            if (item.hint.isNotBlank()) {
                append("""<div class="hint">Hint: ${item.hint}</div>""")
            }
            append("""<div class="answer"><strong>Answer:</strong> ${item.answer}</div>""")
            append("</div>")
        }
        append("</body></html>")
    }

    val webView = WebView(activity)
    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            val printManager = activity.getSystemService(Activity.PRINT_SERVICE) as PrintManager
            val jobName = "NursingApp Quiz List"
            val printAdapter = webView.createPrintDocumentAdapter(jobName)
            printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
        }
    }
    webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
}

@Preview(showBackground = true)
@Composable
private fun QuizListScreenPreview() {
    NursingAppTheme {
        QuizListScreen(
            onNavigateToAddSong = { }
        )
    }
}