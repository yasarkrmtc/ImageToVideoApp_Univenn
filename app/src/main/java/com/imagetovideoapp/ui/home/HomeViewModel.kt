package com.imagetovideoapp.ui.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imagetovideoapp.domain.repository.VideoGenerationResult
import com.imagetovideoapp.domain.state.BaseResponse
import com.imagetovideoapp.domain.usecase.GenerateVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val generateVideoUseCase: GenerateVideoUseCase
) : ViewModel() {

    private val _viewState = MutableSharedFlow<BaseResponse<VideoGenerationResult>>()
    val viewState: SharedFlow<BaseResponse<VideoGenerationResult>> = _viewState

    fun generateVideo(imageUri: Uri, prompt: String?) {
        viewModelScope.launch {
            _viewState.emit(BaseResponse.Loading)

            try {
                generateVideoUseCase(imageUri, prompt).collect { result ->
                    when (result) {
                        is BaseResponse.Success -> {
                            _viewState.emit(BaseResponse.Success(result.data))
                        }
                        is BaseResponse.Error -> {
                            _viewState.emit(BaseResponse.Error(result.exception))
                        }
                        else -> {
                        }
                    }
                }
            } catch (e: Exception) {
                _viewState.emit(BaseResponse.Error(e))
            }
        }
    }
}
