package com.example.home

import android.R
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage


@Composable
fun HomeContent(
    state: HomeViewState,
    onMediaClick: (MediaUiModel) -> Unit,
    listState: LazyListState = rememberLazyListState()
) {
    Column {
        Text(modifier = Modifier.padding(start = 16.dp, top = 16.dp), text = stringResource(com.example.home.R.string.not_yet_released), style = MaterialTheme.typography.titleMedium)
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.isLoading && state.media.isEmpty()) {
                LazyRow {
                    items(10) { ShimmerMediaItem() }
                }
            } else {
                LazyRow(state = listState) {
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
    }
}

@Composable
fun HomeScreen(
    onMediaClick: (MediaUiModel) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(state = state, onMediaClick = onMediaClick)
}

// Now your preview only needs to feed HomeContent:
@Preview(showBackground = true)
@Composable
fun HomeContent_Preview() {
    MaterialTheme {
        Surface {
            val sampleState = HomeViewState(
                media = emptyList(),
                isLoading = true,
                errorMessage = null
            )
            HomeContent(
                state = sampleState,
                onMediaClick = { /* no-op */ }
            )
        }
    }

}

@Composable
fun MediaItem(media: MediaUiModel) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .padding(16.dp)
    ) {
        media.coverImage?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(maxLines = 2, text = media.title.orEmpty(), style = MaterialTheme.typography.titleMedium)
        Text(text = "Score: ${media.averageScore ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun shimmerBrush(): Brush {

    val transition = rememberInfiniteTransition()
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    ).value

    return Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.9f),
            Color.LightGray.copy(alpha = 0.3f),
            Color.LightGray.copy(alpha = 0.9f)
        ),
        start = Offset(x = translateAnim - 1000f, y = 0f),
        end = Offset(x = translateAnim, y = 0f)
    )
}

@Composable
fun ShimmerMediaItem() {
    val brush = shimmerBrush()

    Column(
        modifier = Modifier
            .width(150.dp)
            .padding(16.dp)
    ) {
        // image placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .background(brush)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // title placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(brush)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(brush)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // score placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(16.dp)
                .background(brush)
        )
    }
}