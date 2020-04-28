package com.getfood.data.restaurantlist

class RestaurantListRepository private constructor(private val remoteDataSource: RestaurantListRemoteDataSource) {

    companion object {
        @Volatile
        private var INSTANCE: RestaurantListRepository? = null

        fun getInstance(remoteDataSource: RestaurantListRemoteDataSource): RestaurantListRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RestaurantListRepository(remoteDataSource).also {
                    INSTANCE = it
                }
            }
        }
    }

    suspend fun search(query: String, itemsCount: Int, maxItems: Int) =
        remoteDataSource.search(query, itemsCount, maxItems)
}