package com.imagetovideoapp.ui.generating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imagetovideoapp.domain.state.BaseResponse
import com.imagetovideoapp.domain.usecase.GetPredictStatusUseCase
import com.imagetovideoapp.type.StatusEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

    private val _status = MutableStateFlow("")
    val status: SharedFlow<String> = _status

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
                                _status.value = result.data.status
                                if (result.data.status == StatusEnum.SUCCEEDED.name){
                                    isSuccess = true
                                }
                            }
                            is BaseResponse.Error -> {
                                _errorMessage.value = result.exception.message
                                isSuccess = true
                            }

                            else -> {

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