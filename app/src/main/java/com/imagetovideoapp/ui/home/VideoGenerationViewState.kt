package com.imagetovideoapp.ui.home

import com.imagetovideoapp.domain.repository.VideoGenerationResult

data class VideoGenerationViewState (
    val isLoading: Boolean? = null,
    val errorMessage: String? = null,
    val item: VideoGenerationResult? = null,
)