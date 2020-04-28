package com.getfood.data.models

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("restaurants")
    val restaurants: List<Restaurant> = emptyList(),
    @SerializedName("results_found")
    val resultsFound: Int,
    @SerializedName("results_shown")
    val resultsShown: Int,
    @SerializedName("results_start")
    val resultsStart: Int
)

data class Restaurant(
    @SerializedName("restaurant")
    val restaurant: RestaurantX
)

data class FinalRestaurant(
    val restaurant: Restaurant,
    val isHeader: Boolean,
    val headerText: String
)