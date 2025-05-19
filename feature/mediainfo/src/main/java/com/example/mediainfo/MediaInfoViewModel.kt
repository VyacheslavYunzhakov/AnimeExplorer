package com.example.mediainfo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.MediaInfo
import com.example.domain.interactors.MediaInfoInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MediaState (
    val isLoading: Boolean = true,
    val error: String? = null,
    val mediaInfo: MediaInfo? = null
)

@HiltViewModel
class MediaInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val interactor: MediaInfoInteractor
) : ViewModel() {

    private val _mediaState = MutableStateFlow(MediaState())
    val mediaState: StateFlow<MediaState> = _mediaState.asStateFlow()

    private val mediaId: Int = checkNotNull(savedStateHandle["mediaId"])

    fun getMediaInfo() {
        viewModelScope.launch {
            try {
                val result = interactor.getMediaInfo(mediaId)
                _mediaState.update { MediaState(isLoading = false, error = null, mediaInfo = result) }
            } catch (e: Exception) {
                _mediaState.update { MediaState(isLoading = false, error = e.message, mediaInfo = null) }
            }
        }
    }
}