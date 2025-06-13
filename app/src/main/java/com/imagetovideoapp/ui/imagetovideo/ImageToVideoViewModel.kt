package com.imagetovideoapp.ui.imagetovideo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imagetovideoapp.domain.state.BaseResponse
import com.imagetovideoapp.domain.usecase.GetUserVideosUseCase
import com.imagetovideoapp.type.StatusEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageToVideoViewModel @Inject constructor(
    private val getUserVideosUseCase: GetUserVideosUseCase
) : ViewModel() {

    private val _viewState = MutableStateFlow(UserVideosViewState())
    val viewState = _viewState.asStateFlow()

    fun fetchUserVideos(status: StatusEnum? = null) {
        viewModelScope.launch {
                getUserVideosUseCase(status).collect { result ->
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
                                    itemList = result.data
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
}