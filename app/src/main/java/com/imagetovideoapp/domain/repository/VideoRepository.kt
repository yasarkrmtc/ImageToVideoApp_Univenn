package com.imagetovideoapp.domain.repository

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.DefaultUpload
import com.apollographql.apollo.api.Upload
import com.apollographql.apollo.api.Optional
import com.imagetovideoapp.Img2VideoMutation
import okio.BufferedSink
import okio.source
import java.io.File
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val apolloClient: ApolloClient
) {
    suspend fun startVideoGeneration(imageFile: File, prompt: String?): String? {
        Log.e("qqqqqq",imageFile.toString())

        val upload = DefaultUpload.Builder()
            .fileName("filename.txt")
            .contentLength(imageFile.length())
            .content { sink ->
                imageFile.source().use { sink.buffer.writeAll(it) }
                sink.flush()
            }
            .build()


        val response = apolloClient.mutation(
            Img2VideoMutation(
                image = upload,
                prompt = if (prompt.isNullOrBlank()) Optional.Absent else Optional.Present(prompt)
            )
        ).execute()

        return response.data?.img2Video?.status
    }
}



