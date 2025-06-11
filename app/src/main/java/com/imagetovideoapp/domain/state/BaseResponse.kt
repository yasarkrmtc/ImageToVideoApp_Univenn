package com.imagetovideoapp.domain.state

sealed class BaseResponse<out T> {
    data class Success<out T>(val data: T) : BaseResponse<T>()
    data class Error(val exception: Throwable) : BaseResponse<Nothing>()
    object Loading : BaseResponse<Nothing>()
}


