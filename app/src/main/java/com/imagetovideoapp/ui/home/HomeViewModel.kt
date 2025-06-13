package com.imagetovideoapp.ui.home

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imagetovideoapp.domain.state.BaseResponse
import com.imagetovideoapp.domain.usecase.GenerateVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val generateVideoUseCase: GenerateVideoUseCase
) : ViewModel() {


    private val _viewState = MutableStateFlow(VideoGenerationViewState())
    val viewState = _viewState.asStateFlow()

    fun generateVideo(bitmap: Bitmap, prompt: String?) {
        viewModelScope.launch {
            generateVideoUseCase(bitmap, prompt).collect { result ->
                when (result) {
                    is BaseResponse.Loading -> _viewState.update { viewState ->
                        viewState.copy(
                            isLoading = true, errorMessage = null
                        )
                    }

                    is BaseResponse.Success -> {
                        _viewState.update { viewState ->
                            viewState.copy(
                                isLoading = false,
                                errorMessage = null,
                                item = result.data
                            )
                        }
                    }

                    is BaseResponse.Error -> {
                        _viewState.update { viewState ->
                            viewState.copy(
                                isLoading = false, errorMessage = result.exception.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun setSelectedImage(bitmap: Bitmap) {
        _viewState.update {
            it.copy(selectedImage = bitmap)
        }
    }

    fun deleteImage() {
        _viewState.update {
            it.copy(selectedImage = null)
        }
    }
    fun resetState(){
        _viewState.update {
            it.copy(isLoading = false,errorMessage = null,item = null,selectedImage = null)
        }
    }
}