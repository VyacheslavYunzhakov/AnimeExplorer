package com.example.section

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.interactors.HomeInteractor
import com.example.domain.interactors.NetworkMonitor
import com.example.ui.MediaSectionState
import com.example.ui.MediaUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

const val DEFAULT_PER_PAGE = 15
@HiltViewModel
class SectionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val interactor: HomeInteractor,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {
    val isOnline = networkMonitor.isConnected.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val _uiState = MutableStateFlow(MediaSectionState(isLoading = true))
    val uiState: StateFlow<MediaSectionState> = _uiState.asStateFlow()

    private val sectionType: SectionType = checkNotNull(savedStateHandle["sectionType"])

    private var currentPage = AtomicInteger(1)

    private val _loadedImages = mutableStateMapOf<Int, Boolean>()
    val loadedImages: Map<Int, Boolean> get() = _loadedImages

    fun markImageLoaded(id: Int) {
        _loadedImages[id] = true
    }

    fun hasImageLoaded(id: Int): Boolean {
        return _loadedImages[id] == true
    }


    init {
        Log.d("myTag","SectionViewModel created")
        loadNextPage(sectionType)
        viewModelScope.launch {
            networkMonitor.isConnected
                .distinctUntilChanged()    // only when online/offline actually flips
                .filter { it }              // only when it becomes `true` (online)
                .collect {
                    reloadFailedOrEmpty()
                }
        }
    }

    private fun reloadFailedOrEmpty() {
        if (_uiState.value.errorMessage != null ||
            (_uiState.value.media.isEmpty() && !_uiState.value.isLoading)
        ) {
            loadNextPage(sectionType)
        }
    }

    fun loadNextPage(type: SectionType = sectionType) {
        val pageToLoad = currentPage.get()

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }
            try {
                val domainList = when (type) {
                    SectionType.UPCOMING -> interactor.getUpcomingPage(count = DEFAULT_PER_PAGE, page = pageToLoad)
                    SectionType.TRENDING -> interactor.getTrendingPage(count = DEFAULT_PER_PAGE, page = pageToLoad)
                    SectionType.CURRENTLY_AIRING -> interactor.getAiringPage(count = DEFAULT_PER_PAGE, page = pageToLoad)
                }

                val uiList = domainList.map { domain ->
                    MediaUiModel(
                        id = domain.id,
                        coverImage = domain.coverImage,
                        title = domain.title,
                        averageScore = domain.averageScore
                    )
                }

                _uiState.update {
                    val existingIds = it.media.map { media -> media.id }.toSet()
                    val newItems = uiList.filterNot { media -> media.id in existingIds }

                    it.copy(
                        media = it.media + newItems,
                        isLoading = false
                    )
                }
                currentPage.incrementAndGet()
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = t.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun reloadPage() {
        currentPage.set(0)
        loadNextPage(sectionType)
    }
}