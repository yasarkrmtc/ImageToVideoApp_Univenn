package com.imagetovideoapp.ui.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imagetovideoapp.domain.repository.VideoGenerationResult
import com.imagetovideoapp.domain.usecase.GenerateVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val generateVideoUseCase: GenerateVideoUseCase
) : ViewModel() {

    private val _videoId = MutableSharedFlow<VideoGenerationResult>()
    val videoId: SharedFlow<VideoGenerationResult> = _videoId

    fun generateVideo(imageUri: Uri, prompt: String?) {
        viewModelScope.launch {
            val result = generateVideoUseCase(imageUri, prompt)
            result?.let { _videoId.emit(it) }
        }
    }
}
