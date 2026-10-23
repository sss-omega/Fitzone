package kz.evn.wssession2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NearbyViewModel : ViewModel() {

    private val _restaurants = MutableStateFlow<List<CardItemRes>>(emptyList())
    val restaurants = _restaurants.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        loadRestaurants()
    }

    fun retry() {
        loadRestaurants()
    }

    fun loadRestaurants() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val list = mutableListOf<CardItemRes>()

                try {
                    val resResponse = RetrofitClient.api.getRestaurants()
                    resResponse.data?.rows?.forEach { item ->
                        list.add(
                            CardItemRes(
                                resname = item.restaurantName,
                                distancemap = item.distance?.toFloatOrNull() ?: 0f,
                                reviewres = item.rating?.toFloatOrNull() ?: 0f,
                                objecttype = item.restaurantType ?: "Restaurant",
                                imageUrl = RetrofitClient.resolveImageUrl(item.imageUrl)
                            )
                        )
                    }
                } catch (e: Exception) {
                    _errorMessage.value = e.message
                }

                try {
                    val scenicResponse = RetrofitClient.api.getScenicSpots()
                    scenicResponse.data?.rows?.forEach { item ->
                        list.add(
                            CardItemRes(
                                resname = item.spotName,
                                distancemap = item.distance?.toFloatOrNull() ?: 0f,
                                reviewres = item.rating?.toFloatOrNull() ?: 0f,
                                objecttype = "Attraction",
                                imageUrl = RetrofitClient.resolveImageUrl(item.imageUrl)
                            )
                        )
                    }
                } catch (e: Exception) {
                    if (_errorMessage.value == null) {
                        _errorMessage.value = e.message
                    }
                }

                if (list.isNotEmpty()) {
                    _errorMessage.value = null
                    _restaurants.value = list
                } else if (_errorMessage.value == null) {
                    _errorMessage.value = "No places found"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
