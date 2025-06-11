package com.imagetovideoapp.domain.repository

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.DefaultUpload
import com.apollographql.apollo.api.Optional
import com.imagetovideoapp.Img2VideoMutation
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
}
