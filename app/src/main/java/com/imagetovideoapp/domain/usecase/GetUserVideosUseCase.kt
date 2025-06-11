package com.imagetovideoapp.domain.usecase

import com.imagetovideoapp.domain.repository.UserVideo
import com.imagetovideoapp.domain.repository.VideoRepository
import com.imagetovideoapp.type.StatusEnum
import javax.inject.Inject

class GetUserVideosUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    suspend operator fun invoke(status: StatusEnum? = null): List<UserVideo> {
        return repository.getUserVideos(status)
    }
}
