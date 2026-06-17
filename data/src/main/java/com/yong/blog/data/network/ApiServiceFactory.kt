package com.yong.blog.data.network

import com.yong.blog.data.api.service.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiServiceFactory {
    private const val BLOG_API_URL = "https://dev-lr.com/"

    fun createApiService(
        interceptors: List<Interceptor> = emptyList()
    ): ApiService {
        val client = OkHttpClient.Builder()
            .apply {
                interceptors.forEach { addInterceptor(it) }
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BLOG_API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}