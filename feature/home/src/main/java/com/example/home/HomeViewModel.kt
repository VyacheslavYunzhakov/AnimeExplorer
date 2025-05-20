package com.example.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.interactors.HomeInteractor
import com.example.domain.interactors.NetworkMonitor
import com.example.ui.MediaUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MediaSectionState (
    val media: List<MediaUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val interactor: HomeInteractor,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    val isOnline = networkMonitor.isConnected.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val _uiUpcomingState = MutableStateFlow(MediaSectionState(isLoading = true))
    val uiUpcomingState: StateFlow<MediaSectionState> = _uiUpcomingState.asStateFlow()

    private val _uiTrendingState = MutableStateFlow(MediaSectionState(isLoading = true))
    val uiTrendingState: StateFlow<MediaSectionState> = _uiTrendingState.asStateFlow()

    private val _uiAiringState = MutableStateFlow(MediaSectionState(isLoading = true))
    val uiAiringState: StateFlow<MediaSectionState> = _uiAiringState.asStateFlow()

    init {
        loadMediaPage()
        viewModelScope.launch {
            networkMonitor.isConnected
                .distinctUntilChanged()    // only when online/offline actually flips
                .filter { it }              // only when it becomes `true` (online)
                .collect {
                    reloadFailedOrEmpty()
                }
        }
    }

    private fun loadMediaPage() {
        loadUpcoming()
        loadAiring()
        loadTrending()
    }

    private fun reloadFailedOrEmpty() {
        if (_uiUpcomingState.value.errorMessage != null ||
            (_uiUpcomingState.value.media.isEmpty() && !_uiUpcomingState.value.isLoading)
        ) {
            loadUpcoming()
        }
        if (_uiTrendingState.value.errorMessage != null ||
            (_uiTrendingState.value.media.isEmpty() && !_uiTrendingState.value.isLoading)
        ) {
            loadTrending()
        }
        if (_uiAiringState.value.errorMessage != null ||
            (_uiAiringState.value.media.isEmpty() && !_uiAiringState.value.isLoading)
        ) {
            loadAiring()
        }
    }

    internal fun loadUpcoming() {
        viewModelScope.launch {
            _uiUpcomingState.update { it.copy(media = emptyList(), isLoading = true, errorMessage = null) }
            try {
                val domainList = interactor.getUpcomingPage()
                val uiList = domainList.map { domain ->
                    MediaUiModel(
                        id = domain.id,
                        coverImage = domain.coverImage,
                        title = domain.title,
                        averageScore = domain.averageScore
                    )
                }
                _uiUpcomingState.update {
                    it.copy(
                        media = uiList,
                        isLoading = false
                    )
                }
            } catch (t: Throwable) {
                _uiUpcomingState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = t.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    internal fun loadTrending() {
        viewModelScope.launch {
            _uiTrendingState.update { it.copy(media = emptyList(), isLoading = true, errorMessage = null) }
            try {
                val domainList = interactor.getTrendingPage()
                val uiList = domainList.map { domain ->
                    MediaUiModel(
                        id = domain.id,
                        coverImage = domain.coverImage,
                        title = domain.title,
                        averageScore = domain.averageScore
                    )
                }
                _uiTrendingState.update {
                    it.copy(
                        media = uiList,
                        isLoading = false
                    )
                }
            } catch (t: Throwable) {
                _uiTrendingState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = t.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    internal fun loadAiring() {
        viewModelScope.launch {
            _uiAiringState.update { it.copy(media = emptyList(), isLoading = true, errorMessage = null) }
            try {
                val domainList = interactor.getAiringPage()
                val uiList = domainList.map { domain ->
                    MediaUiModel(
                        id = domain.id,
                        coverImage = domain.coverImage,
                        title = domain.title,
                        averageScore = domain.averageScore
                    )
                }
                _uiAiringState.update {
                    it.copy(
                        media = uiList,
                        isLoading = false
                    )
                }
            } catch (t: Throwable) {
                _uiAiringState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = t.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    /**
     * Retry loading launches on error.
     */
    fun retry() {
        loadMediaPage()
    }
}