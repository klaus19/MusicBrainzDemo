package com.example.android.di

import com.example.android.network.MBClient
import com.example.android.network.MB_API
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Singleton
    @Provides
    fun provideMBApi(
        mbClient: MBClient,
    ): MB_API {
        return mbClient.buildApi(MB_API::class.java)
    }


}