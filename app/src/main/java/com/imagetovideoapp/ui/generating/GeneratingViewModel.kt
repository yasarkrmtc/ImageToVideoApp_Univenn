package com.imagetovideoapp.ui.generating

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imagetovideoapp.domain.repository.StatusUiModel
import com.imagetovideoapp.domain.state.BaseResponse
import com.imagetovideoapp.domain.usecase.GetPredictStatusUseCase
import com.imagetovideoapp.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeneratingViewModel @Inject constructor(
    private val getPredictStatusUseCase: GetPredictStatusUseCase
) : ViewModel() {

    private val _progress = MutableStateFlow(0)
    val progress: SharedFlow<Int> = _progress


    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: SharedFlow<String?> = _errorMessage

    fun startPolling(videoId: String) {
        viewModelScope.launch {
            try {
                var isSuccess = false
                while (!isSuccess) {
                    getPredictStatusUseCase(videoId).collect { result ->
                        when (result) {
                            is BaseResponse.Success -> {
                                _progress.value = result.data.progress
                                if (result.data.progress == 100){
                                    isSuccess = true
                                }
                            }
                            is BaseResponse.Error -> {
                                _errorMessage.value = result.exception.message
                                isSuccess = true
                            }
                            else -> {
                                val errorMessage = Constants.VIDEO_GENERATION_FAILED
                                _errorMessage.value = errorMessage
                                isSuccess = false
                            }
                        }
                    }
                    delay(1000)
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
}
