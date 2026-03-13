package com.nursingapp.ui.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.nursingapp.data.QuizCategory
import com.nursingapp.data.QuizItem

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
                role = Role.Button
                contentDescription = if (isRevealed) "Answer revealed" else "Tap to reveal answer"
            }
            .clickable { isRevealed = !isRevealed },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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

                if (item.isCustom) {
                    IconButton(
                        onClick = { onDeleteClick(item) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val isSong = item.category == QuizCategory.GUESS_THE_SONG
            val hasLyrics = !item.lyrics.isNullOrBlank() || item.question.contains("♪")

            Column {
                Text(
                    text = if (isSong && hasLyrics) "🎵" else item.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                if (isSong && hasLyrics) {
                    val lyricsText = item.lyrics ?: item.question
                    if (lyricsText != "🎵") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = lyricsText,
                            style = MaterialTheme.typography.bodyLarge,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            if (!isSong && !item.hint.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hint: ${item.hint}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }


            if (isSong && item.youtubeId != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val appIntent = Intent(Intent.ACTION_VIEW, "vnd.youtube:${item.youtubeId}".toUri())
                        val webIntent = Intent(Intent.ACTION_VIEW, "https://www.youtube.com/watch?v=${item.youtubeId}".toUri())
                        try { context.startActivity(appIntent) } catch (e: Exception) { context.startActivity(webIntent) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000), contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Play Video", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                AnimatedVisibility(
                    visible = isRevealed,
                    enter = fadeIn(tween(300)),
                    exit = fadeOut(tween(200)),
                ) {
                    Column {
                        Text(
                            text = "Answer",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = item.answer,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                AnimatedVisibility(
                    visible = !isRevealed,
                    enter = fadeIn(tween(300)),
                    exit = fadeOut(tween(200)),
                ) {
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