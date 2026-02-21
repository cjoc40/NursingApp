package com.nursingapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nursingapp.ui.screens.ActivityListScreen
import com.nursingapp.ui.screens.QuizListScreen

/** Sealed class describing each top-level navigation destination. */
sealed class AppDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    data object Quizzes : AppDestination(
        route = "quizzes",
        label = "Quizzes",
        selectedIcon = Icons.Filled.EmojiEvents,
        unselectedIcon = Icons.Outlined.EmojiEvents,
    )

    data object Activities : AppDestination(
        route = "activities",
        label = "Activities",
        selectedIcon = Icons.Filled.SelfImprovement,
        unselectedIcon = Icons.Outlined.SelfImprovement,
    )
}

private val topLevelDestinations = listOf(AppDestination.Quizzes, AppDestination.Activities)

/**
 * Root composable that owns the [NavHost] and bottom navigation bar.
 * Uses [androidx.navigation.compose] for type-safe, back-stack-aware navigation.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                topLevelDestinations.forEach { destination ->
                    val selected = currentDestination
                        ?.hierarchy
                        ?.any { it.route == destination.route } == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                // Pop up to the start destination to avoid large back stacks
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = destination.label,
                            )
                        },
                        label = { Text(destination.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Quizzes.route,
        ) {
            composable(AppDestination.Quizzes.route) {
                QuizListScreen(
                    modifier = Modifier.padding(innerPadding),
                )
            }
            composable(AppDestination.Activities.route) {
                ActivityListScreen(
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}
