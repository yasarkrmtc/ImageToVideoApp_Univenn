package com.imagetovideoapp.ui.imagetovideo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imagetovideoapp.domain.repository.UserVideo
import com.imagetovideoapp.domain.usecase.GetUserVideosUseCase
import com.imagetovideoapp.type.StatusEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageToVideoViewModel @Inject constructor(
    application: Application,
    private val getUserVideosUseCase: GetUserVideosUseCase
) : AndroidViewModel(application) {

    private val _userVideos = MutableSharedFlow<List<UserVideo>>()
    val userVideos: SharedFlow<List<UserVideo>> = _userVideos

    fun fetchUserVideos(status: StatusEnum? = null) {
        viewModelScope.launch {
            try {
                val videos = getUserVideosUseCase(status)
                _userVideos.emit(videos)
            } catch (e: Exception) {
                // Handle error if needed
                _userVideos.emit(emptyList()) // Empty list on error
            }
        }
    }
}
