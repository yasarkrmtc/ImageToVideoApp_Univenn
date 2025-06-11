package com.imagetovideoapp.ui.generating

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imagetovideoapp.domain.repository.StatusUiModel
import com.imagetovideoapp.domain.usecase.GetPredictStatusUseCase
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

    private val _progress = MutableSharedFlow<StatusUiModel>()
    val progress: SharedFlow<StatusUiModel> = _progress

    fun startPolling(videoId: String) {
        Log.e("qqqqqqqq55555" ,videoId)
        viewModelScope.launch {
            while (true) {
                val result = getPredictStatusUseCase(videoId)
                result?.let { _progress.emit(it) }
                if (result?.status == "SUCCEEDED" || result?.status == "FAILED") break
                delay(2000) // Wait before polling again
            }
        }
    }
}
