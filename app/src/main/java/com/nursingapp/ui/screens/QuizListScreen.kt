package com.nursingapp.ui.screens

import android.app.Activity
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.unit.dp
import com.nursingapp.data.QuizCategory
import com.nursingapp.data.QuizItem
import com.nursingapp.data.allQuizItems
import com.nursingapp.data.SongRepository
import com.nursingapp.ui.components.QuizCard
import kotlinx.coroutines.launch
import java.util.Date
import androidx.compose.material.icons.filled.Settings
private enum class QuizFilter(val label: String) {
    TRIVIA("Trivia"),
    GUESS_THE_SONG("Guess the Song"),
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun QuizListScreen(modifier: Modifier = Modifier, onNavigateToAddSong: () -> Unit) {
    var selectedFilter by rememberSaveable { mutableStateOf(QuizFilter.TRIVIA) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 1. API Filter States
    var showApiSettings by remember { mutableStateOf(false) }
    var apiDifficulty by rememberSaveable { mutableStateOf("medium") }
    var apiCategoryId by rememberSaveable { mutableStateOf<Int?>(null) }

    val combinedItems = allQuizItems + SongRepository.songs
    val filteredItems = when (selectedFilter) {
        QuizFilter.TRIVIA -> combinedItems.filter { it.category == QuizCategory.TRIVIA }
        QuizFilter.GUESS_THE_SONG -> combinedItems.filter { it.category == QuizCategory.GUESS_THE_SONG }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quizzes", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { printQuizList(context as Activity, filteredItems) }) {
                        Icon(Icons.Default.Print, contentDescription = "Print")
                    }

                    // 2. Open API Settings
                    IconButton(onClick = { showApiSettings = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings, // Much cleaner!
                            contentDescription = "API Settings"
                        )
                    }

                    IconButton(onClick = {
                        scope.launch {
                            // 3. Pass filters to fetch
                            SongRepository.fetchNewTrivia(context, apiCategoryId, apiDifficulty)
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Get New Trivia")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
        floatingActionButton = {
            if (selectedFilter == QuizFilter.GUESS_THE_SONG) {
                FloatingActionButton(
                    onClick = onNavigateToAddSong,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Song")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding() + 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { Spacer(modifier = Modifier.height(70.dp)) }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Text("${filteredItems.size} items", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                items(filteredItems, key = { it.id }) { quizItem ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        QuizCard(item = quizItem, onDeleteClick = { SongRepository.deleteSong(context, it) })
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
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
        }

        // 4. API Settings Bottom Sheet
        if (showApiSettings) {
            ModalBottomSheet(
                onDismissRequest = { showApiSettings = false },
                sheetState = rememberModalBottomSheetState()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 40.dp)
                ) {
                    Text("API Retrieval Options", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))

                    Text("Difficulty", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("easy", "medium", "hard").forEach { level ->
                            FilterChip(
                                selected = apiDifficulty == level,
                                onClick = { apiDifficulty = level },
                                label = { Text(level.replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text("Category", style = MaterialTheme.typography.titleMedium)
                    // A quick way to handle categories
                    val categories = listOf("Any" to null, "General" to 9, "History" to 23, "Geography" to 22, "Music" to 12)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { (name, id) ->
                            FilterChip(
                                selected = apiCategoryId == id,
                                onClick = { apiCategoryId = id },
                                label = { Text(name) }
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = { showApiSettings = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}
private fun printQuizList(activity: Activity, items: List<QuizItem>) {
    val html = buildString {
        append(
            """
            <!DOCTYPE html><html><head>
            <meta charset="utf-8"/>
            <style>
              body { font-family: Arial, sans-serif; margin: 32px; color: #222; }
              h1 { color: #6650A4; }
              .card { border: 1px solid #ccc; border-radius: 8px; padding: 12px 16px; margin-bottom: 16px; page-break-inside: avoid; }
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