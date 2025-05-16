package com.example.animeexploreranilist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.animeexploreranilist.navigation.ExplorerNavHost
import com.example.animeexploreranilist.ui.rememberExplorerAppState
import com.example.animeexploreranilist.ui.theme.AnimeExplorerAnimeListTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimeExplorerAnimeListTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    ExplorerNavHost(rememberExplorerAppState(navController))
                }
            }
        }
    }
}