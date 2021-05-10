package com.djambulat69.fragmentchat.di

import com.djambulat69.fragmentchat.model.UriReader
import com.djambulat69.fragmentchat.model.UriReaderImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class UriReaderModule {

    @Singleton
    @Provides
    fun provideUriReader(): UriReader {
        return UriReaderImpl()
    }

}
