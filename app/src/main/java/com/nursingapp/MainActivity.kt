package com.nursingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nursingapp.ui.navigation.AppNavigation
import com.nursingapp.ui.theme.NursingAppTheme

/**
 * Single-Activity entry point for the Nursing App.
 *
 * All navigation is handled by [AppNavigation] using Jetpack Navigation Compose.
 * All quiz and activity data is embedded in the app — no network connection is needed,
 * making the app fully functional offline.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NursingAppTheme {
                AppNavigation()
            }
        }
    }
}
