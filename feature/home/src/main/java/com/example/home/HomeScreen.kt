package com.example.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage


@Composable
fun HomeScreen(
    onMediaClick: (MediaUiModel) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading && state.media.isEmpty()) {
            // Initial loading with shimmer
            LazyColumn {
                items(10) {
                    ShimmerMediaItem()
                }
            }
        } else {
            LazyColumn(state = listState) {
                items(state.media) { media ->
                    MediaItem(media = media)
                }

                if (state.isLoading) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }

        state.errorMessage?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    // Trigger load more when we reach near end
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            lastVisible >= total - 3
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !state.isLoading) {
            viewModel.retry() // Replace with loadNextPage() if you add page tracking
        }
    }
}

@Composable
fun MediaItem(media: MediaUiModel) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp)
    ) {
        media.coverImage?.let {
            ImageThenTextLayout(it, media.title)
        }
        Text(
            text = "Score: ${media.averageScore ?: "N/A"}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun ImageThenTextLayout(imageUrl: String, text: String?) {
    Layout(
        content = {
            AsyncImage(model = imageUrl, contentDescription = null)
            Spacer(Modifier.height(8.dp))
            Text(text = text.toString(),maxLines = 2,overflow = TextOverflow.Ellipsis)
        }
    ) { measurables, constraints ->

        val imagePlaceable = measurables[0].measure(constraints)
        val imageWidth = imagePlaceable.width

        val spacerPlaceable = measurables[1].measure(constraints)

        val textPlaceable = measurables[2].measure(
            constraints.copy(minWidth = imageWidth, maxWidth = imageWidth)
        )
        val totalHeight = imagePlaceable.height + spacerPlaceable.height + textPlaceable.height

        layout(width = imageWidth, height = totalHeight) {
            imagePlaceable.place(0, 0)
            spacerPlaceable.place(0, imagePlaceable.height)
            textPlaceable.place(0, imagePlaceable.height + spacerPlaceable.height)
        }
    }
}

@Composable
fun ShimmerMediaItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(16.dp)
            .background(Color.LightGray)
    )
}
