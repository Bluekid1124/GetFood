package com.getfood.viewmodels.restaurantlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.getfood.data.restaurantlist.RestaurantListRepository

class RestaurantListViewModelFactory(
    private val repository: RestaurantListRepository,
    private val queryString: String
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RestaurantListViewModel(repository = repository, query = queryString) as T
    }
}