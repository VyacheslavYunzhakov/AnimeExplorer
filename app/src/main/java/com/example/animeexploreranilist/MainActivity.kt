package com.example.animeexploreranilist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.home.navigation.HomeBaseRoute
import com.example.home.navigation.homeScreen
import com.example.animeexploreranilist.ui.theme.SpaceXExplorerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpaceXExplorerTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    // NavGraph sets up the overall navigation for the app.
                    NavHost(
                        navController = navController,
                        startDestination = HomeBaseRoute
                    ) {
                        homeScreen {}
                    }
                }
            }
        }
    }
}