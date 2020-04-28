package com.getfood.utilities

import com.getfood.data.api.ClientAuthInterceptor
import com.getfood.data.api.ZomatoApiService
import com.getfood.data.restaurantlist.RestaurantListRemoteDataSource
import com.getfood.data.restaurantlist.RestaurantListRepository
import com.getfood.viewmodels.restaurantlist.RestaurantListViewModelFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object InjectorUtils {

    private val okHttpClient = OkHttpClient.Builder()

    private fun provideZomatoApiService(): ZomatoApiService {
        okHttpClient.addInterceptor(ClientAuthInterceptor())
        return Retrofit.Builder()
            .baseUrl(ZomatoApiService.ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())
            .build()
            .create(ZomatoApiService::class.java)
    }

    private fun getHomeRemoteDataSource(): RestaurantListRemoteDataSource =
        RestaurantListRemoteDataSource.getInstance(provideZomatoApiService())

    private fun getHomeRepository(): RestaurantListRepository {
        return RestaurantListRepository.getInstance(getHomeRemoteDataSource())
    }

    fun provideHomeViewModelFactory(queryString: String): RestaurantListViewModelFactory {
        val repository = getHomeRepository()
        return RestaurantListViewModelFactory(repository, queryString)
    }
}