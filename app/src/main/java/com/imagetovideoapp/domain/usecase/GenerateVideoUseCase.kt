package com.imagetovideoapp.domain.usecase

import android.content.Context
import android.net.Uri
import com.imagetovideoapp.domain.repository.VideoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GenerateVideoUseCase @Inject constructor(
    private val repository: VideoRepository,
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(imageUri: Uri, prompt: String?): String? {
        val file = com.imagetovideoapp.utils.FileUtils.getFileFromUri(context, imageUri)
        return repository.startVideoGeneration(file, prompt)
    }
}
