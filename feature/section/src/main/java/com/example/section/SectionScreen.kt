package com.example.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MediaItem
import com.example.ui.ShimmerMediaItem

enum class SectionType {
    UPCOMING,
    TRENDING,
    CURRENTLY_AIRING
}

@Composable
fun SectionScreen(
    onBackClick: () -> Unit,
    onMediaClick: (Int) -> Unit,
    viewModel: SectionViewModel = hiltViewModel()
) {
    val gridState: LazyGridState = rememberLazyGridState()
    val visibleItemsRange by remember {
        derivedStateOf { gridState.layoutInfo.visibleItemsInfo.map { it.index }.toSet() }
    }

    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading = state.value.isLoading
    val media = state.value.media
    val errorMessage = state.value.errorMessage
    val isOnline = viewModel.isOnline.collectAsStateWithLifecycle()

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "See more",
            modifier = Modifier
                .size(32.dp)
                .clickable { onBackClick() }
        )

        Spacer(Modifier.height(8.dp))

        // — Grid container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize()
        ) {
            when {
                // 1) Loading placeholders
                isLoading && media.isEmpty() -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(6) {
                            ShimmerMediaItem()
                        }
                    }
                }

                // 2) Error & retry
                errorMessage != null -> {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clickable { viewModel.reloadPage() }
                    )
                }

                // 3) Actual grid
                else -> {
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(media, key = { index, item -> item.id }) { index, item ->
                            val isVisible = visibleItemsRange.contains(index)
                            MediaItem(
                                media     = item,
                                isVisible = isVisible,
                                isOnline  = isOnline.value,
                                onMediaClick = { onMediaClick(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
