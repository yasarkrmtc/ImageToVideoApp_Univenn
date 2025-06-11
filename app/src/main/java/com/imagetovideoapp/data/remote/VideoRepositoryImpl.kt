package com.imagetovideoapp.data.remote

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.DefaultUpload
import com.apollographql.apollo.api.Optional
import com.imagetovideoapp.GetPredictStatusQuery
import com.imagetovideoapp.GetUserVideosQuery
import com.imagetovideoapp.Img2VideoMutation
import com.imagetovideoapp.domain.repository.VideoRepository
import com.imagetovideoapp.domain.repository.StatusUiModel
import com.imagetovideoapp.domain.repository.UserVideo
import com.imagetovideoapp.domain.repository.VideoGenerationResult
import com.imagetovideoapp.domain.state.BaseResponse
import com.imagetovideoapp.type.StatusEnum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import okio.source
import java.io.File
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val apolloClient: ApolloClient
) : VideoRepository {

    override suspend fun startVideoGeneration(
        imageFile: File,
        prompt: String?
    ): Flow<BaseResponse<VideoGenerationResult>> = flow {
        emit(BaseResponse.Loading)
        val upload = DefaultUpload.Builder()
            .fileName(imageFile.name)
            .contentLength(imageFile.length())
            .content { sink ->
                imageFile.source().use { source ->
                    sink.buffer.writeAll(source)
                    sink.flush()
                }
            }
            .build()

        val mutation = Img2VideoMutation(
            image = upload,
            prompt = if (prompt.isNullOrBlank()) Optional.Absent else Optional.Present(prompt)
        )
        val response = apolloClient.mutation(mutation).execute()

        if (response.hasErrors()) {
            val errorMessage = response.errors?.get(0)?.message

            emit(BaseResponse.Error(Exception("Hata oluştu: $errorMessage")))
        } else {
            val video = response.data?.img2Video?.video
            val status = response.data?.img2Video?.status

            if (video != null && status != null) {
                emit(
                    BaseResponse.Success(
                        VideoGenerationResult(
                            id = video.id,
                            status = video.status.name
                        )
                    )
                )
            } else {
                emit(BaseResponse.Error(Exception("Video oluşturulamadı")))
            }
        }

    }.onStart {
        emit(BaseResponse.Loading)
    }.catch { e ->
        emit(BaseResponse.Error(e))
    }


    override suspend fun getPredictStatus(videoId: String): Flow<BaseResponse<StatusUiModel>> =
        flow {
            emit(BaseResponse.Loading)

            val response = apolloClient.query(GetPredictStatusQuery(videoId)).execute()
            if (response.hasErrors()) {
                val errorMessage = response.errors?.get(0)?.message
                emit(BaseResponse.Error(Exception("Hata oluştu: $errorMessage")))
            } else {
                val statusData = response.data?.getPredictStatus

                if (statusData != null) {
                    emit(
                        BaseResponse.Success(
                            StatusUiModel(
                                progress = statusData.progress,
                                status = statusData.status.name,
                                videoUrl = "https://videoai-api.univenn.com/output/$videoId.mp4",
                                description = "Video oluşturuluyor..."
                            )
                        )
                    )
                } else {
                    emit(BaseResponse.Error(Exception("Status bulunamadı")))  // Hata durumu
                }
            }
        }.onStart {
            emit(BaseResponse.Loading)
        }.catch { e ->
            emit(BaseResponse.Error(e))
        }


    override suspend fun getUserVideos(status: StatusEnum?): Flow<BaseResponse<List<UserVideo>>> =
        flow {
            emit(BaseResponse.Loading)

            val query = GetUserVideosQuery(status = Optional.presentIfNotNull(status))
            val response = apolloClient.query(query).execute()

            if (response.hasErrors()) {
                val errorMessage = response.errors?.get(0)?.message
                emit(BaseResponse.Error(Exception("Hata oluştu: $errorMessage")))
            } else {
                val videos = response.data?.getUserVideos

                if (videos != null) {
                    emit(BaseResponse.Success(
                        videos.map {
                            UserVideo(
                                id = it.id,
                                url = it.url ?: "",
                                status = it.status.name,
                                prompt = it.prompt,
                                thumbnailUrl = it.thumbnailUrl,
                                duration = it.duration ?: 0
                            )
                        }
                    ))
                } else {
                    emit(BaseResponse.Error(Exception("Videolar alınamadı")))
                }
            }
        }.onStart {
            emit(BaseResponse.Loading)
        }.catch { e ->
            emit(BaseResponse.Error(e))
        }

}
