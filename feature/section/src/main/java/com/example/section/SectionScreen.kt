package com.example.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ui.MediaItem
import com.example.ui.ShimmerMediaItem
import kotlinx.coroutines.flow.filter

enum class SectionType {
    UPCOMING,
    TRENDING,
    CURRENTLY_AIRING
}

@Composable
fun SectionScreen(
    viewModel: SectionViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onMediaClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val gridState: LazyGridState = rememberLazyGridState()
    val visibleIndices = remember { mutableStateOf<Set<Int>>(emptySet()) }
    val isOnline by viewModel.isOnline.collectAsState()

    LaunchedEffect(gridState) {
        snapshotFlow {
            gridState.layoutInfo.visibleItemsInfo.map { it.index }.toSet()
        }.collect { visibleIndices.value = it }
    }

    LaunchedEffect(gridState) {
        snapshotFlow {
            val lastVisible = gridState.layoutInfo.visibleItemsInfo
                .lastOrNull()
                ?.index
                ?: -1
            lastVisible to gridState.isScrollInProgress
        }
            .filter { (lastVisible, isScrolling) ->
                isScrolling && lastVisible >= uiState.media.size - 1
            }
            .collect {
                viewModel.loadNextPage()
            }
    }
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "See more",
            modifier = Modifier
                .size(32.dp)
                .clickable { onBackClick() }
        )

        Spacer(Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.media.isEmpty() && uiState.isLoading) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(15) {
                        ShimmerMediaItem()
                    }
                }
            } else if (uiState.media.isEmpty() && uiState.errorMessage != null) {
                // initial error
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = uiState.errorMessage ?: "Unknown error")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.reloadPage() }) {
                        Text("Retry")
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    state = gridState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(uiState.media, key = { index, item -> item.id }) { index, item ->
                        val hasLoadedBefore = viewModel.hasImageLoaded(item.id)
                        MediaItem(
                            media = item,
                            isVisible = index in visibleIndices.value,
                            isOnline = isOnline,
                            onMediaClick = { onMediaClick(item.id) },
                            hasLoadedBefore = hasLoadedBefore,
                            onImageLoaded = {
                                viewModel.markImageLoaded(item.id)
                            }
                        )
                    }
                    if (uiState.isLoading) {
                        items(15) {
                            ShimmerMediaItem()
                        }
                    }
                }
            }
        }
    }
}

