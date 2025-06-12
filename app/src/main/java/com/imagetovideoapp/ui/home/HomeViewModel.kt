package com.imagetovideoapp.ui.home

import android.net.Uri
import android.util.Log
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

    fun generateVideo(imageUri: Uri, prompt: String?) {
        viewModelScope.launch {
            generateVideoUseCase(imageUri, prompt).collect { result ->
                when (result) {
                    is BaseResponse.Loading -> _viewState.update { viewState ->
                        viewState.copy(
                            isLoading = true, errorMessage = null
                        )
                    }

                    is BaseResponse.Success -> {
                        Log.e("ress","success : ${result.data}")

                        _viewState.update { viewState ->
                            viewState.copy(
                                isLoading = false,
                                errorMessage = null,
                                item = result.data
                            )
                        }
                    }

                    is BaseResponse.Error -> {
                        Log.e("ress","errorGeldi : ${result.exception.message}")
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


    fun setSelectedImage(imageUri: Uri) {
        _viewState.update {
            it.copy(selectedImage = imageUri)
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

