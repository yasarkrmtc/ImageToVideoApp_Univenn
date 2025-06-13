package com.imagetovideoapp.domain.usecase

import com.imagetovideoapp.domain.repository.VideoRepository
import com.imagetovideoapp.domain.repository.StatusUiModel
import kotlinx.coroutines.flow.Flow
import com.imagetovideoapp.domain.state.BaseResponse
import javax.inject.Inject

class GetPredictStatusUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    suspend operator fun invoke(videoId: String): Flow<BaseResponse<StatusUiModel>> {
        return repository.getPredictStatus(videoId)
    }
}