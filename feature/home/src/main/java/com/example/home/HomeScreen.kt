package com.example.home

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.ui.MediaItem
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
    isOnline: Boolean
) {
    val listState = rememberLazyListState()

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
                            MediaItem(media = item, isVisible = isVisible, isOnline, onMediaClick)
                        }
                    }
                }
            }
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
    fun reloadSection(type: MediaSectionType) {
        when (type) {
            MediaSectionType.UPCOMING -> viewModel.loadUpcoming()
            MediaSectionType.TRENDING -> viewModel.loadTrending()
            MediaSectionType.CURRENTLY_AIRING -> viewModel.loadAiring()
        }
    }
    HomeContent(upcomingState, trendingState, airingState, onMediaClick, ::reloadSection, onShowAllClick, isOnline)
}

@Composable
fun HomeContent (
    upcomingState: MediaSectionState,
    trendingState: MediaSectionState,
    airingState: MediaSectionState,
    onMediaClick: (Int) -> Unit,
    onErrorClick: (MediaSectionType) -> Unit,
    onShowAllClick: (MediaSectionType) -> Unit,
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
            onShowAllClick = onShowAllClick,
            isOnline
        )
        MediaSection(
            sectionType = MediaSectionType.TRENDING,
            media = trendingState.media,
            isLoading = trendingState.isLoading,
            errorMessage = trendingState.errorMessage,
            onMediaClick = onMediaClick,
            onErrorClick = onErrorClick,
            onShowAllClick = onShowAllClick,
            isOnline
        )
        MediaSection(
            sectionType = MediaSectionType.CURRENTLY_AIRING,
            media = airingState.media,
            isLoading = airingState.isLoading,
            errorMessage = airingState.errorMessage,
            onMediaClick = onMediaClick,
            onErrorClick = onErrorClick,
            onShowAllClick = onShowAllClick,
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
                onShowAllClick = {},
                true
            )
        }
    }

}