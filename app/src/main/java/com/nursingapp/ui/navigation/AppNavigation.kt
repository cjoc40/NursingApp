package com.nursingapp.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.outlined.CalendarMonth
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
import com.nursingapp.ui.screens.ActivityCalendarScreen
import com.nursingapp.ui.screens.ActivityListScreen
import com.nursingapp.ui.screens.AddActivityScreen
import com.nursingapp.ui.screens.AddSongScreen
import com.nursingapp.ui.screens.CalendarScreen
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
    data object AddSong : AppDestination(
        route = "add_song",
        label = "Add Song",
        selectedIcon = Icons.Filled.SelfImprovement,
        unselectedIcon = Icons.Outlined.SelfImprovement
    )
    data object AddActivity : AppDestination(
        route = "add_activity",
        label = "Add Activity",
        selectedIcon = Icons.Filled.SelfImprovement,
        unselectedIcon = Icons.Outlined.SelfImprovement
    )
    data object Calendar : AppDestination(
        route = "calendar",
        label = "Calendar",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth,
    )
}

private val topLevelDestinations = listOf(
    AppDestination.Quizzes,
    AppDestination.Activities,
    AppDestination.Calendar
)

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
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Main Quiz List Screen
            composable(AppDestination.Quizzes.route) {
                QuizListScreen(
                    onNavigateToAddSong = {
                        navController.navigate(AppDestination.AddSong.route)
                    }
                )
            }
            // 2. Activities Screen
            composable(AppDestination.Activities.route) {
                ActivityListScreen(
                    modifier = Modifier.fillMaxSize(),
                    onNavigateToAddActivity = {
                        navController.navigate(AppDestination.AddActivity.route)
                    }
                )
            }
            // 3. Add Song Screen
            composable(AppDestination.AddSong.route) {
                AddSongScreen(
                    onSongAdded = {
                        navController.popBackStack()
                    }
                )
            }
            composable(AppDestination.AddActivity.route) {
                AddActivityScreen(
                    onActivityAdded = {
                        navController.popBackStack()
                    }
                )
            }
            composable(AppDestination.Calendar.route) {
                CalendarScreen(modifier = Modifier.fillMaxSize())
            }
            composable("schedule_picker/{activityId}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("activityId")?.toIntOrNull() ?: 0
                ActivityCalendarScreen(
                    activityId = id,
                    onScheduleConfirmed = { navController.popBackStack() }
                )
            }
        }
    }
}
