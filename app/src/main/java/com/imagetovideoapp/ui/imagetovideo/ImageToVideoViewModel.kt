package com.imagetovideoapp.ui.imagetovideo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imagetovideoapp.domain.repository.UserVideo
import com.imagetovideoapp.domain.state.BaseResponse
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

    private val _userVideos = MutableSharedFlow<BaseResponse<List<UserVideo>>>()
    val userVideos: SharedFlow<BaseResponse<List<UserVideo>>> = _userVideos

    fun fetchUserVideos(status: StatusEnum? = null) {
        viewModelScope.launch {
            _userVideos.emit(BaseResponse.Loading)

            try {
                getUserVideosUseCase(status).collect { result ->
                    when (result) {
                        is BaseResponse.Success -> {
                            _userVideos.emit(BaseResponse.Success(result.data))
                        }

                        is BaseResponse.Error -> {
                            _userVideos.emit(BaseResponse.Error(result.exception))
                        }

                        else -> {
                        }
                    }
                }
            } catch (e: Exception) {
                _userVideos.emit(BaseResponse.Error(e))
            }
        }
    }
}
