package com.nursingapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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

/**
 * A flashcard-style card that shows a quiz question and reveals the answer on tap.
 *
 * The card keeps its revealed/hidden state across recompositions using [rememberSaveable].
 * Tapping anywhere on the card toggles the answer visibility.
 */
@Composable
fun QuizCard(
    item: QuizItem,
    modifier: Modifier = Modifier,
) {
    var isRevealed by rememberSaveable(item.id) { mutableStateOf(false) }

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
            // Category badge
            Surface(
                shape = RoundedCornerShape(50),
                color = categoryColor,
                contentColor = onCategoryColor,
                modifier = Modifier.align(Alignment.Start),
            ) {
                Text(
                    text = item.category.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Question
            Text(
                text = item.question,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            // Optional hint
            if (item.hint.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hint: ${item.hint}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Tap-to-reveal section
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
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}
