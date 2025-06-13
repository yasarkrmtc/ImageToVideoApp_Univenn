package com.imagetovideoapp.domain.repository

import android.content.Context
import android.graphics.Bitmap
import com.imagetovideoapp.type.StatusEnum
import kotlinx.coroutines.flow.Flow
import com.imagetovideoapp.domain.state.BaseResponse

interface VideoRepository {
    suspend fun startVideoGeneration(
        bitmap: Bitmap,
        prompt: String?,
        context: Context
    ): Flow<BaseResponse<VideoGenerationResult>>

    suspend fun getPredictStatus(videoId: String): Flow<BaseResponse<StatusUiModel>>

    suspend fun getUserVideos(status: StatusEnum?): Flow<BaseResponse<List<UserVideo>>>
}
