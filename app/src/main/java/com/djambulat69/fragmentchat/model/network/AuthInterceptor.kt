package com.djambulat69.fragmentchat.model.network

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder().addHeader(AUTH_HEADER, credential)
        val request = requestBuilder.build()
        return chain.proceed(request)
    }

    companion object {
        private const val AUTH_HEADER = "Authorization"
        private const val API_KEY = "4nJfSYDBV23HhUcQgBxgQ3KIroRkMjDS"

        private val credential: String = Credentials.basic("djambulat69@gmail.com", API_KEY)
    }
}
