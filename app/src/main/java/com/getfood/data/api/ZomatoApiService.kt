package com.getfood.data.api

import com.getfood.data.models.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ZomatoApiService {

    companion object {
        const val ENDPOINT = "https://developers.zomato.com/"
    }

    @GET("api/v2.1/search")
    suspend fun searchRestaurants(
        @Query("q") keyword: String,
        @Query("start") startingCount: Int,
        @Query("count") maxItems: Int
    ) : Response<SearchResponse>
}