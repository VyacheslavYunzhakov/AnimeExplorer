package com.example.home

import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.ui.MediaItem
import com.example.ui.MediaSectionState
import com.example.ui.MediaUiModel
import com.example.ui.ShimmerMediaItem

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
    onMediaClick: (Int) -> Unit,
    onErrorClick: (MediaSectionType) -> Unit,
    onShowAllClick: (MediaSectionType) -> Unit,
    isOnline: Boolean,
    listState: LazyListState,
    blurredMedia: MutableList<Bitmap?>,
    viewModel: HomeViewModel = hiltViewModel()

) {
    val visibleItemsRange = remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.map { it.index }.toSet()
        }
    }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = LocalContext.current.getString(sectionType.titleResId),
                style = MaterialTheme.typography.titleMedium
            )
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "See more",
                modifier = Modifier
                    .size(48.dp)
                    .padding(horizontal = 8.dp)
                    .clickable {
                        onShowAllClick(sectionType)
                    }
            )
        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)) {
            when {
                isLoading && media.isEmpty() -> {
                    LazyRow {
                        items(DEFAULT_PER_PAGE) { ShimmerMediaItem() }
                    }
                }

                errorMessage != null -> {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clickable { onErrorClick(sectionType) },
                    )
                }

                else -> {
                    LazyRow(state = listState) {
                        itemsIndexed(media) { index, item ->
                            val isVisible = visibleItemsRange.value.contains(index)
                            val hasLoadedBefore = viewModel.hasImageLoaded(item.id)
                            MediaItem(
                                media = item,
                                isVisible = isVisible,
                                isOnline = isOnline,
                                onMediaClick = onMediaClick,
                                hasLoadedBefore = hasLoadedBefore,
                                onImageLoaded = { bitmap ->
                                    viewModel.markImageLoaded(item.id)
                                    blurredMedia[index] = bitmap
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeContent (
    upcomingState: MediaSectionState,
    trendingState: MediaSectionState,
    airingState: MediaSectionState,
    onMediaClick: (Int) -> Unit,
    onErrorClick: (MediaSectionType) -> Unit,
    onShowAllClick: (MediaSectionType) -> Unit,
    isOnline: Boolean,
    scrollEnabled: Boolean = true,
    topInset: Dp = 0.dp
) {
    val scrollState = rememberScrollState()

    val listStates = remember {
        mutableStateMapOf<MediaSectionType, LazyListState>().apply {
            MediaSectionType.entries.forEach { type ->
                this[type] = LazyListState()
            }
        }
    }
    val blurredBitmaps = remember {
        mutableStateMapOf<MediaSectionType, MutableList<Bitmap?>>().apply {
            MediaSectionType.entries.forEach { type ->
                this[type] = MutableList<Bitmap?>(DEFAULT_PER_PAGE) { null }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState, enabled = scrollEnabled)
    ) {
        Spacer(modifier = Modifier.height(topInset))
            MediaSection(
                sectionType = MediaSectionType.UPCOMING,
                media = upcomingState.media,
                isLoading = upcomingState.isLoading,
                errorMessage = upcomingState.errorMessage,
                onMediaClick = onMediaClick,
                onErrorClick = onErrorClick,
                onShowAllClick = onShowAllClick,
                isOnline,
                listStates.getValue(MediaSectionType.UPCOMING),
                blurredBitmaps.getValue(MediaSectionType.UPCOMING)
            )
            MediaSection(
                sectionType = MediaSectionType.TRENDING,
                media = trendingState.media,
                isLoading = trendingState.isLoading,
                errorMessage = trendingState.errorMessage,
                onMediaClick = onMediaClick,
                onErrorClick = onErrorClick,
                onShowAllClick = onShowAllClick,
                isOnline,
                listStates.getValue(MediaSectionType.TRENDING),
                blurredBitmaps.getValue(MediaSectionType.TRENDING)
            )
            MediaSection(
                sectionType = MediaSectionType.CURRENTLY_AIRING,
                media = airingState.media,
                isLoading = airingState.isLoading,
                errorMessage = airingState.errorMessage,
                onMediaClick = onMediaClick,
                onErrorClick = onErrorClick,
                onShowAllClick = onShowAllClick,
                isOnline,
                listStates.getValue(MediaSectionType.CURRENTLY_AIRING),
                blurredBitmaps.getValue(MediaSectionType.CURRENTLY_AIRING)
            )
        Spacer(modifier = Modifier.height(300.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun HomeContent_Preview() {
    MaterialTheme {
        Surface {
            HomeScreen(
                onMediaClick = { },
                onShowAllClick = {}
            )
        }
    }
}

@Composable
fun HomeScreen(
    onMediaClick: (Int) -> Unit,
    onShowAllClick: (MediaSectionType) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val upcomingState by viewModel.uiUpcomingState.collectAsStateWithLifecycle()
    val trendingState by viewModel.uiTrendingState.collectAsStateWithLifecycle()
    val airingState by viewModel.uiAiringState.collectAsStateWithLifecycle()

    val headerHeight = 96.dp

    Box(modifier = Modifier.fillMaxSize()) {
        HomeContent(
            upcomingState = upcomingState,
            trendingState = trendingState,
            airingState = airingState,
            onMediaClick = onMediaClick,
            onErrorClick = { type ->
                when (type) {
                    MediaSectionType.UPCOMING -> viewModel.loadUpcoming()
                    MediaSectionType.TRENDING -> viewModel.loadTrending()
                    MediaSectionType.CURRENTLY_AIRING -> viewModel.loadAiring()
                }
            },
            onShowAllClick = onShowAllClick,
            isOnline = isOnline,
            scrollEnabled = true,
            topInset = headerHeight
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .align(Alignment.TopCenter),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("Главная", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}