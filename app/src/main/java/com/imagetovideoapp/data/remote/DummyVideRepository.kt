package com.imagetovideoapp.data.remote

import android.content.Context
import android.graphics.Bitmap
import com.apollographql.apollo.ApolloClient
import com.imagetovideoapp.domain.repository.VideoRepository
import com.imagetovideoapp.domain.repository.StatusUiModel
import com.imagetovideoapp.domain.repository.UserVideo
import com.imagetovideoapp.domain.repository.VideoGenerationResult
import com.imagetovideoapp.domain.state.BaseResponse
import com.imagetovideoapp.type.StatusEnum
import com.imagetovideoapp.utils.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import java.io.File
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.nextInt

class DummyVideRepository @Inject constructor(
    private val apolloClient: ApolloClient
) : VideoRepository {

    override suspend fun startVideoGeneration(
        bitmap: Bitmap,
        prompt: String?,
        context: Context
    ): Flow<BaseResponse<VideoGenerationResult>> = flow {
        emit(BaseResponse.Loading)
        delay(1000)
        emit(BaseResponse.Success(
            VideoGenerationResult(
                id = userVideos[Random.nextInt(userVideos.indices)].id,
                status = StatusEnum.SUCCEEDED.name
            )
        ))
    }

    private var progress = 0

    override suspend fun getPredictStatus(videoId: String): Flow<BaseResponse<StatusUiModel>> =
        flow {
            emit(BaseResponse.Loading)
            progress += 5
            delay(10)

            val status = if (progress >= 100) StatusEnum.SUCCEEDED.name else Constants.STATUS_PROCESSING

            val statusUiModel = StatusUiModel(
                progress = progress,
                status = StatusEnum.SUCCEEDED.name,
                videoUrl = "${Constants.VIDEO_URL_BASE}$videoId.mp4",
                description = Constants.VIDEO_CREATING_DESCRIPTION
            )

            emit(BaseResponse.Success(statusUiModel))

            if (progress >= 100) {
                progress = 0
            }
        }
            .onStart {
                emit(BaseResponse.Loading)
            }
            .catch { e ->
                emit(BaseResponse.Error(e))
            }

    override suspend fun getUserVideos(status: StatusEnum?): Flow<BaseResponse<List<UserVideo>>> =
        flow {
            emit(BaseResponse.Loading)
            delay(1000)
            emit(BaseResponse.Success(userVideos))
        }
            .catch { e ->
                emit(BaseResponse.Error(e))
            }
}
