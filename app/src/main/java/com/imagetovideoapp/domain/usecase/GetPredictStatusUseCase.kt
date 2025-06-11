package com.imagetovideoapp.domain.usecase

import com.imagetovideoapp.domain.repository.StatusUiModel
import com.imagetovideoapp.domain.repository.VideoRepository
import javax.inject.Inject

class GetPredictStatusUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    suspend operator fun invoke(videoId: String): StatusUiModel? {
        return repository.getPredictStatus(videoId)
    }
}
