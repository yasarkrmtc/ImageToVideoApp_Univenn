package com.imagetovideoapp.domain.repository

import com.apollographql.apollo.ApolloClient
import com.imagetovideoapp.BuildConfig
import com.imagetovideoapp.data.remote.DummyVideRepository
import com.imagetovideoapp.data.remote.VideoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object  RepositoryModule {

    @Provides
     fun provideRepository(apolloClient: ApolloClient): VideoRepository {
         return if (BuildConfig.USE_FAKE_REPO) DummyVideRepository(apolloClient) else VideoRepositoryImpl(apolloClient)
     }
}