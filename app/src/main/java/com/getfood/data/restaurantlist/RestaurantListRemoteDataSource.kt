package com.getfood.data.restaurantlist

import com.getfood.data.Result
import com.getfood.data.api.ZomatoApiService
import com.getfood.data.models.SearchResponse
import retrofit2.Response
import java.io.IOException

class RestaurantListRemoteDataSource private constructor(private val service: ZomatoApiService) {

    companion object {
        @Volatile
        private var INSTANCE: RestaurantListRemoteDataSource? = null
        fun getInstance(service: ZomatoApiService): RestaurantListRemoteDataSource {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RestaurantListRemoteDataSource(service).also {
                    INSTANCE = it
                }
            }
        }
    }

    suspend fun search(query: String, itemsCount: Int, maxItems: Int): Result<SearchResponse> {
        return try {
            val searchResult = service.searchRestaurants(query, itemsCount, maxItems)
            getResult(response = searchResult, onError = {
                Result.Error(
                    IOException("Error getting stories ${searchResult.code()} ${searchResult.message()}")
                )
            })
        } catch (e: Exception) {
            Result.Error(IOException("Error searching $query", e))
        }
    }

    private inline fun getResult(
        response: Response<SearchResponse>,
        onError: () -> Result.Error
    ): Result<SearchResponse> {
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                return Result.Success(body)
            }
        }
        return onError.invoke()
    }
}