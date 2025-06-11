package com.imagetovideoapp.domain.repository

import com.imagetovideoapp.data.remote.VideoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVideoRepository(
        videoRepositoryImpl: VideoRepositoryImpl
    ): VideoRepository
}