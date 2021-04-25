package com.djambulat69.fragmentchat.di

import com.djambulat69.fragmentchat.model.network.ZulipRetrofit
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideZulipService() = ZulipRetrofit.get()

}
