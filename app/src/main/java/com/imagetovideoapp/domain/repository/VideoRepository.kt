package com.imagetovideoapp.domain.repository

import com.imagetovideoapp.domain.repository.StatusUiModel
import com.imagetovideoapp.domain.repository.UserVideo
import com.imagetovideoapp.domain.repository.VideoGenerationResult
import com.imagetovideoapp.type.StatusEnum
import java.io.File
import kotlinx.coroutines.flow.Flow
import com.imagetovideoapp.domain.state.BaseResponse

interface VideoRepository {

    suspend fun startVideoGeneration(imageFile: File, prompt: String?): Flow<BaseResponse<VideoGenerationResult>>

    suspend fun getPredictStatus(videoId: String): Flow<BaseResponse<StatusUiModel>>

    suspend fun getUserVideos(status: StatusEnum?): Flow<BaseResponse<List<UserVideo>>>
}
