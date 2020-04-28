package com.getfood.data.api

import com.getfood.utilities.API_KEY
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * An Interceptor that adds an api key to requests if one is provided.
 */
class ClientAuthInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
            .addHeader("user-key", API_KEY)
        return chain.proceed(requestBuilder.build())
    }
}