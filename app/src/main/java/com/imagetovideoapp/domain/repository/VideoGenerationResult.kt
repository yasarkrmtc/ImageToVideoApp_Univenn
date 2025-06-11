package com.imagetovideoapp.domain.repository


data class VideoGenerationResult(
    val id: String,
    val status: String
)
data class StatusUiModel(
    val progress: Int,
    val status: String,
    val videoUrl: String,
    val description: String?
)
data class UserVideo(
    val id: String,
    val url: String,
    val status: String,
    val prompt: String?,
    val thumbnailUrl: String?,
    val duration: Int
)
