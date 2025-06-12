package com.imagetovideoapp.ui.generating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imagetovideoapp.domain.repository.StatusUiModel
import com.imagetovideoapp.domain.state.BaseResponse
import com.imagetovideoapp.domain.usecase.GetPredictStatusUseCase
import com.imagetovideoapp.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeneratingViewModel @Inject constructor(
    private val getPredictStatusUseCase: GetPredictStatusUseCase
) : ViewModel() {

    private val _progress = MutableSharedFlow<BaseResponse<StatusUiModel>>()
    val progress: SharedFlow<BaseResponse<StatusUiModel>> = _progress

    fun startPolling(videoId: String) {
        viewModelScope.launch {
            _progress.emit(BaseResponse.Loading) // Emit loading state initially

            try {
                var isSuccess = false
                while (!isSuccess) {
                    getPredictStatusUseCase(videoId).collect { result ->
                        when (result) {
                            is BaseResponse.Success -> {
                                val status = result.data.status
                                val progress = result.data.progress
                                _progress.emit(BaseResponse.Success(result.data))
                                if (status == Constants.SUCCEEDED_STATUS) {
                                    isSuccess = true
                                }
                                if (progress in 0..100) {
                                    _progress.emit(BaseResponse.Success(result.data))
                                }
                            }
                            is BaseResponse.Error -> {
                                _progress.emit(BaseResponse.Error(result.exception))
                                isSuccess = true
                            }
                            else -> {
                                val errorMessage = Constants.VIDEO_GENERATION_FAILED
                                _progress.emit(BaseResponse.Error(Exception(errorMessage)))
                                isSuccess = true
                            }
                        }
                    }
                    delay(1000)
                }
            } catch (e: Exception) {
                _progress.emit(BaseResponse.Error(e))
            }
        }
    }
}
