package com.imagetovideoapp.ui.home

import android.graphics.Bitmap
import com.imagetovideoapp.domain.repository.VideoGenerationResult

data class VideoGenerationViewState (
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val item: VideoGenerationResult? = null,
    val selectedImage : Bitmap? = null
)