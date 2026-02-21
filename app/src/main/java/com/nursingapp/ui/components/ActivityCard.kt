package com.nursingapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Inventory2
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nursingapp.data.ActivityCategory
import com.nursingapp.data.ActivityItem
import com.nursingapp.data.MobilityLevel
import com.nursingapp.ui.theme.NursingAppTheme

/**
 * A card that displays activity details for nursing-home residents.
 *
 * The card always shows the activity name, category badge, duration, and mobility level.
 * Tapping the card expands it to reveal the full description and supplies list.
 */
@Composable
fun ActivityCard(
    item: ActivityItem,
    modifier: Modifier = Modifier,
    onDeleteClick: (ActivityItem) -> Unit // 1. Add this parameter
) {
    var isExpanded by rememberSaveable(item.id) { mutableStateOf(false) }

    val categoryColor = when (item.category) {
        ActivityCategory.ART_CRAFTS -> MaterialTheme.colorScheme.secondaryContainer
        ActivityCategory.BAKING_COOKING -> MaterialTheme.colorScheme.tertiaryContainer
        ActivityCategory.MUSIC -> MaterialTheme.colorScheme.primaryContainer
        ActivityCategory.GAMES -> MaterialTheme.colorScheme.surfaceVariant
        ActivityCategory.EXERCISE -> MaterialTheme.colorScheme.secondaryContainer
    }
    val onCategoryColor = when (item.category) {
        ActivityCategory.ART_CRAFTS -> MaterialTheme.colorScheme.onSecondaryContainer
        ActivityCategory.BAKING_COOKING -> MaterialTheme.colorScheme.onTertiaryContainer
        ActivityCategory.MUSIC -> MaterialTheme.colorScheme.onPrimaryContainer
        ActivityCategory.GAMES -> MaterialTheme.colorScheme.onSurfaceVariant
        ActivityCategory.EXERCISE -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top row: category badge + (Delete Icon if custom) + expand icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
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

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 2. Add Delete Button for custom items
                    if (item.isCustom) {
                        IconButton(
                            onClick = { onDeleteClick(item) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete activity",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Activity name
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Duration & Mobility meta row
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Duration
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Duration",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.duration,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // Mobility
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DirectionsRun,
                        contentDescription = "Mobility level",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${item.mobilityRequired.emoji} ${item.mobilityRequired.displayName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Expandable section: description + supplies
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(250)) + fadeIn(animationSpec = tween(250)),
                exit = shrinkVertically(animationSpec = tween(200)) + fadeOut(animationSpec = tween(200)),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Description
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Supplies
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = "Supplies needed",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.secondary,
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Supplies needed",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    item.supplies.forEach { supply ->
                        Text(
                            text = "• $supply",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 8.dp, bottom = 2.dp),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ActivityCardPreview() {
    NursingAppTheme {
        ActivityCard(
            item = ActivityItem(
                id = 1,
                name = "Watercolor Painting",
                description = "Details...",
                duration = "45–60 min",
                mobilityRequired = MobilityLevel.SEATED,
                supplies = listOf("Paints"),
                category = ActivityCategory.ART_CRAFTS,
                isCustom = true // Test with custom true to see delete icon
            ),
            modifier = Modifier.padding(16.dp),
            onDeleteClick = {} // Add empty lambda for preview
        )
    }
}
