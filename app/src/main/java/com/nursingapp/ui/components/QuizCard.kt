package com.nursingapp.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nursingapp.data.QuizCategory
import com.nursingapp.data.QuizItem
import com.nursingapp.ui.theme.NursingAppTheme
import androidx.core.net.toUri

@Composable
fun QuizCard(
    item: QuizItem,
    modifier: Modifier = Modifier,
    onDeleteClick: (QuizItem) -> Unit
) {
    var isRevealed by rememberSaveable(item.id) { mutableStateOf(false) }
    val context = LocalContext.current

    val categoryColor = when (item.category) {
        QuizCategory.TRIVIA -> MaterialTheme.colorScheme.primaryContainer
        QuizCategory.GUESS_THE_SONG -> MaterialTheme.colorScheme.tertiaryContainer
    }
    val onCategoryColor = when (item.category) {
        QuizCategory.TRIVIA -> MaterialTheme.colorScheme.onPrimaryContainer
        QuizCategory.GUESS_THE_SONG -> MaterialTheme.colorScheme.onTertiaryContainer
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = if (isRevealed) {
                    "Question: ${item.question}. Answer revealed: ${item.answer}. Tap to hide."
                } else {
                    "Question: ${item.question}. Tap to reveal the answer."
                }
                role = Role.Button
            }
            .clickable { isRevealed = !isRevealed },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Category Badge + Delete Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = categoryColor,
                    contentColor = onCategoryColor,
                ) {
                    Text(
                        text = item.category.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }

                // Delete button only for user-added songs
                if (item.isCustom) {
                    IconButton(
                        onClick = { onDeleteClick(item) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete custom song",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Question Text
            Text(
                text = item.question,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            // Optional Hint
            if (item.hint.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hint: ${item.hint}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // YouTube Button
            if (item.category == QuizCategory.GUESS_THE_SONG && item.youtubeId != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        // "vnd.youtube:" is the specific URI prefix to open the YouTube App
                        // Fallback is the standard web URL
                        val appIntent = Intent(Intent.ACTION_VIEW,
                            "vnd.youtube:${item.youtubeId}".toUri())
                        val webIntent = Intent(Intent.ACTION_VIEW,
                            "https://www.youtube.com/watch?v=${item.youtubeId}".toUri())

                        try {
                            context.startActivity(appIntent)
                        } catch (e: Exception) {
                            // If the YouTube app isn't installed, open in the browser
                            context.startActivity(webIntent)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF0000), // YouTube Red
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Play on YouTube")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Answer Section (Revealed)
            AnimatedVisibility(
                visible = isRevealed,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(200)),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Answer",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.answer,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Hint Text (Hidden state)
            AnimatedVisibility(
                visible = !isRevealed,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(200)),
            ) {
                Box(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "Tap to reveal answer",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }
        }
    }
}

// --- Previews ---

@Preview(showBackground = true)
@Composable
private fun QuizCardTriviaPreview() {
    NursingAppTheme {
        QuizCard(
            item = QuizItem(
                id = 1,
                question = "What year did World War II end?",
                answer = "1945",
                category = QuizCategory.TRIVIA,
            ),
            modifier = Modifier.padding(16.dp),
            onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun QuizCardSongPreview() {
    NursingAppTheme {
        QuizCard(
            item = QuizItem(
                id = 21,
                question = "♪ \"You ain't nothin' but a hound dog, cryin' all the time…\"",
                answer = "Hound Dog – Elvis Presley (1956)",
                category = QuizCategory.GUESS_THE_SONG,
                youtubeId = "eHJ12Vhpyc"
            ),
            modifier = Modifier.padding(16.dp),
            onDeleteClick = {}
        )
    }
}