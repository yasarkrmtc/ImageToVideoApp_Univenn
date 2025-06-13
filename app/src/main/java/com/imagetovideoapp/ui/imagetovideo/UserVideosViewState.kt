package com.imagetovideoapp.ui.imagetovideo

import com.imagetovideoapp.domain.repository.UserVideo

data class UserVideosViewState (
    val isLoading: Boolean? = null,
    val errorMessage: String? = null,
    val itemList: List<UserVideo>? = null,
)