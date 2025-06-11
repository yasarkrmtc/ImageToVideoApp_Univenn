package com.imagetovideoapp.domain.usecase

import com.imagetovideoapp.domain.repository.VideoRepository
import com.imagetovideoapp.domain.repository.UserVideo
import com.imagetovideoapp.type.StatusEnum
import javax.inject.Inject
import com.imagetovideoapp.domain.state.BaseResponse
import kotlinx.coroutines.flow.Flow

class GetUserVideosUseCase @Inject constructor(
    private val repository: VideoRepository
) {
    suspend operator fun invoke(status: StatusEnum? = null): Flow<BaseResponse<List<UserVideo>>> {
        return repository.getUserVideos(status)
    }
}
