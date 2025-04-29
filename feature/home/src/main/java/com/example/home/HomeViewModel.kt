package com.example.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.interactors.HomeInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeViewState(
    val media: List<MediaUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class MediaUiModel(
    val id: Int,
    val bannerImage: String?,
    val title: String?,
    val averageScore: Int?
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val interactor: HomeInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeViewState(isLoading = true))
    val uiState: StateFlow<HomeViewState> = _uiState.asStateFlow()

    init {
        loadMediaPage()
    }

    private fun loadMediaPage() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val domainList = interactor.getMediaPage()
                val uiList = domainList.map { domain ->
                    MediaUiModel(
                        id = domain.id,
                        bannerImage = domain.bannerImage,
                        title = domain.title,
                        averageScore = domain.averageScore
                    )
                }
                _uiState.update {
                    it.copy(
                        media = uiList,
                        isLoading = false
                    )
                }
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

    /**
     * Retry loading launches on error.
     */
    fun retry() {
        loadMediaPage()
    }
}