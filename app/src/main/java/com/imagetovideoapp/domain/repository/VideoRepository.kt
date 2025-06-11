package com.imagetovideoapp.domain.repository

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.DefaultUpload
import com.apollographql.apollo.api.Optional
import com.imagetovideoapp.GetPredictStatusQuery
import com.imagetovideoapp.GetUserVideosQuery
import com.imagetovideoapp.Img2VideoMutation
import com.imagetovideoapp.type.StatusEnum
import okio.source
import java.io.File
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val apolloClient: ApolloClient
) {
    suspend fun startVideoGeneration(imageFile: File, prompt: String?): VideoGenerationResult? {
        Log.d("VideoRepository", "Sending image: ${imageFile.absolutePath}")

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
        val video = response.data?.img2Video?.video
        val status = response.data?.img2Video?.status

        return if (video != null && status != null) {
            VideoGenerationResult(
                id = video.id,
                status = video.status.name
            )
        } else {
            null
        }
    }
    suspend fun getPredictStatus(videoId: String): StatusUiModel? {
        val response = apolloClient.query(GetPredictStatusQuery(videoId)).execute()
        val statusData = response.data?.getPredictStatus

        return statusData?.let {
            StatusUiModel(
                progress = it.progress,
                status = it.status.name,
                videoUrl = "https://videoai-api.univenn.com/output/$videoId.mp4",
                description = "Video oluşturuluyor..."
            )
        }
    }

    suspend fun getUserVideos(status: StatusEnum?): List<UserVideo> {
        val query = GetUserVideosQuery(status = Optional.presentIfNotNull(status))
        val response = apolloClient.query(query).execute()
        val videos = response.data?.getUserVideos

        return videos?.map {
            UserVideo(
                id = it.id,
                url = it.url ?: "",
                status = it.status.name,
                prompt = it.prompt,
                thumbnailUrl = it.thumbnailUrl,
                duration = it.duration ?: 0
            )
        } ?: emptyList()
    }
}
