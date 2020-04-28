package com.getfood.viewmodels.restaurantlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.getfood.data.Result
import com.getfood.data.restaurantlist.RestaurantListRepository
import com.getfood.data.models.FinalRestaurant
import com.getfood.data.models.Restaurant
import com.getfood.data.models.SearchResponse
import com.getfood.utilities.exhaustive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Data class mapping the key based on which we're requesting data and the page
 */
data class SearchProgressModel(val isLoading: Boolean)

class RestaurantListViewModel(
    private val repository: RestaurantListRepository,
    private val query: String,
    private var itemsCount: Int = 1,
    private var maxItems: Int = 10
) :
    ViewModel() {

    private var isDataFinished = false
    private val _results = MutableLiveData<List<FinalRestaurant>>()
    private val _errorResult = MutableLiveData<String>()
    private val _searchProgress = MutableLiveData<SearchProgressModel>()
    val results: LiveData<List<FinalRestaurant>>
        get() = _results
    val errorResult: LiveData<String>
        get() = _errorResult
    val searchProgress: LiveData<SearchProgressModel>
        get() = _searchProgress

    private val tempList = mutableListOf<Restaurant>()

    init {
        searchRestaurants()
    }

    private fun searchRestaurants() {
        if (query.isNotEmpty())
            viewModelScope.launch(Dispatchers.IO) {
                _searchProgress.postValue(SearchProgressModel(isLoading = true))
                val searchResult = repository.search(query, itemsCount, maxItems)
                when (searchResult) {
                    is Result.Success -> {
                        _searchProgress.postValue(SearchProgressModel(isLoading = false))
                        if (searchResult.data.resultsFound == 0) {
                            isDataFinished = true
                            _errorResult.postValue("Result not found")
                        } else {
                            val response = searchResult.data
                            if (response.resultsShown < maxItems || itemsCount == response.resultsFound)
                                isDataFinished = true
                            updateRestaurantData(response)
                        }
                    }
                    is Result.Error -> {
                        _searchProgress.postValue(SearchProgressModel(isLoading = false))
                        _errorResult.postValue(searchResult.exception.toString())
                    }
                }.exhaustive
            }
    }

    private fun updateRestaurantData(newItems: SearchResponse) {
        _results.postValue(getItemsForDisplay(tempList, newItems.restaurants))
    }

    fun loadMoreData() = viewModelScope.launch {
        if (!isDataFinished) {
            itemsCount += maxItems
            searchRestaurants()
        }
    }

    private fun getItemsForDisplay(
        oldItems: List<Restaurant>,
        newItems: List<Restaurant>
    ): List<FinalRestaurant> {
        val itemsToBeDisplayed = oldItems.toMutableList()
        itemsToBeDisplayed.addAll(newItems)
        val finalList = mutableListOf<FinalRestaurant>()
        val groupedData = itemsToBeDisplayed.groupBy { it.restaurant.cuisines }
        groupedData.flatMap {
            finalList.add(
                FinalRestaurant(
                    isHeader = true,
                    restaurant = it.value[0],
                    headerText = it.key
                )
            )
            it.value.forEach { restaurant ->
                finalList.add(
                    FinalRestaurant(
                        isHeader = false,
                        restaurant = restaurant,
                        headerText = ""
                    )
                )
            }
            finalList
        }
        tempList.addAll(newItems)
        return finalList
    }
}
