package com.example.home

import androidx.annotation.StringRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest

enum class MediaSectionType(@StringRes val titleResId: Int) {
    UPCOMING(R.string.upcoming_anime),
    TRENDING(R.string.trending_anime),
    CURRENTLY_AIRING(R.string.currently_airing)
}

@Composable
fun MediaSection(
    sectionType: MediaSectionType,
    media: List<MediaUiModel>,
    isLoading: Boolean,
    errorMessage: String?,
    onMediaClick: (MediaUiModel) -> Unit,
    onErrorClick: (MediaSectionType) -> Unit,
    isOnline: Boolean
) {
    val listState = rememberLazyListState()

    val visibleItemsRange = remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.map { it.index }.toSet()
        }
    }
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = LocalContext.current.getString(sectionType.titleResId),
            style = MaterialTheme.typography.titleMedium
        )

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)) {
            when {
                isLoading && media.isEmpty() -> {
                    LazyRow {
                        items(10) { ShimmerMediaItem() }
                    }
                }

                errorMessage != null -> {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clickable {
                                onErrorClick(sectionType)
                            },
                    )
                }

                else -> {
                    LazyRow(state = listState) {
                        itemsIndexed(media) { index, item ->
                            val isVisible = visibleItemsRange.value.contains(index)
                            MediaItem(media = item, isVisible = isVisible, isOnline)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun HomeScreen(
    onMediaClick: (MediaUiModel) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val upcomingState by viewModel.uiUpcomingState.collectAsStateWithLifecycle()
    val trendingState by viewModel.uiTrendingState.collectAsStateWithLifecycle()
    val airingState by viewModel.uiAiringState.collectAsStateWithLifecycle()
    fun reloadSection(type: MediaSectionType) {
        when (type) {
            MediaSectionType.UPCOMING -> viewModel.loadUpcoming()
            MediaSectionType.TRENDING -> viewModel.loadTrending()
            MediaSectionType.CURRENTLY_AIRING -> viewModel.loadAiring()
        }
    }
    HomeContent(upcomingState, trendingState, airingState, onMediaClick, ::reloadSection, isOnline)
}

@Composable
fun HomeContent (
    upcomingState: MediaSectionState,
    trendingState: MediaSectionState,
    airingState: MediaSectionState,
    onMediaClick: (MediaUiModel) -> Unit,
    onErrorClick: (MediaSectionType) -> Unit,
    isOnline: Boolean
) {

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        MediaSection(
            sectionType = MediaSectionType.UPCOMING,
            media = upcomingState.media,
            isLoading = upcomingState.isLoading,
            errorMessage = upcomingState.errorMessage,
            onMediaClick = onMediaClick,
            onErrorClick = onErrorClick,
            isOnline
        )
        MediaSection(
            sectionType = MediaSectionType.TRENDING,
            media = trendingState.media,
            isLoading = trendingState.isLoading,
            errorMessage = trendingState.errorMessage,
            onMediaClick = onMediaClick,
            onErrorClick = onErrorClick,
            isOnline
        )
        MediaSection(
            sectionType = MediaSectionType.CURRENTLY_AIRING,
            media = airingState.media,
            isLoading = airingState.isLoading,
            errorMessage = airingState.errorMessage,
            onMediaClick = onMediaClick,
            onErrorClick = onErrorClick,
            isOnline
        )
    }
}

// Now your preview only needs to feed HomeContent:
@Preview(showBackground = true)
@Composable
fun HomeContent_Preview() {
    MaterialTheme {
        Surface {
            val sampleState = MediaSectionState(
                media = emptyList(),
                isLoading = true,
                errorMessage = null
            )
            HomeContent(
                upcomingState = sampleState,
                trendingState = sampleState,
                airingState = sampleState,
                onMediaClick = { /* no-op */ },
                onErrorClick = {},
                true
            )
        }
    }

}

@Composable
fun MediaItem(
    media: MediaUiModel,
    isVisible: Boolean,
    isOnline: Boolean
) {
    val brush = shimmerBrush()
    val context = LocalContext.current

    // 1) Build a request that WILL cache to memory/disk.
    val request = ImageRequest.Builder(context)
        .data(media.coverImage)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    // 2) Create and remember the painter
    val painter = rememberAsyncImagePainter(request)
    val painterState = painter.state

    // 3) Decide whether to show the image:
    //    • painterState is Success → we’ve already loaded it before
    //    • OR (isVisible && isOnline) → first time load
    val shouldDisplayImage = painterState is AsyncImagePainter.State.Success ||
            (isVisible && isOnline)

    Column(
        modifier = Modifier
            .width(150.dp)
            .padding(16.dp)
    ) {
        if (shouldDisplayImage && media.coverImage != null) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .background(brush)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = media.title.orEmpty(),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2, minLines = 2
        )
        Text(
            text = "Score: ${media.averageScore ?: "N/A"}",
            style = MaterialTheme.typography.bodySmall
        )
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