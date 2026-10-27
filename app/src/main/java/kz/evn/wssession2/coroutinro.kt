package kz.evn.wssession2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CardItemRes(
    val id: Long = 0L,
    val resname: String,
    val distancemap: Float,
    val reviewres: Float,
    val objecttype: String,
    val imageUrl: String?,
    val workingHours: String = "10:00 - 22:00",
    val parking: Int = 0,
    val priceRange: String = "",
    val address: String = "",
    val description: String = ""
)

class NearbyViewModel : ViewModel() {

    private val _restaurants = MutableStateFlow<List<CardItemRes>>(emptyList())
    val restaurants = _restaurants.asStateFlow()

    private val _currentUser = MutableStateFlow<UserDto?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _token = MutableStateFlow<String?>(null)
    val token = _token.asStateFlow()

    private val _reviews = MutableStateFlow<List<ReviewDto>>(emptyList())
    val reviews = _reviews.asStateFlow()

    private val _myReviews = MutableStateFlow<List<ReviewDto>>(emptyList())
    val myReviews = _myReviews.asStateFlow()

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
                                id = item.restaurantId ?: 0L,
                                resname = item.restaurantName,
                                distancemap = item.distance?.toFloatOrNull() ?: 0f,
                                reviewres = item.rating?.toFloatOrNull() ?: 0f,
                                objecttype = item.restaurantType ?: "Restaurant",
                                imageUrl = RetrofitClient.resolveImageUrl(item.imageUrl),
                                workingHours = item.businessHours ?: "10:00 - 22:00",
                                parking = item.remainingParkingSpaces ?: 0,
                                priceRange = item.priceRange ?: "$15-45",
                                address = item.address ?: "",
                                description = item.description ?: ""
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
                                id = item.spotId ?: 0L,
                                resname = item.spotName,
                                distancemap = item.distance?.toFloatOrNull() ?: 0f,
                                reviewres = item.rating?.toFloatOrNull() ?: 0f,
                                objecttype = "Attraction",
                                imageUrl = RetrofitClient.resolveImageUrl(item.imageUrl),
                                workingHours = item.openingHours ?: "08:30 - 17:00",
                                parking = 0,
                                priceRange = item.ticketPrice ?: "$5-20",
                                address = item.address ?: "",
                                description = item.description ?: ""
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

    fun login(username: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val res = RetrofitClient.api.login(LoginRequest(username, password))
                if (res.code == 200 && res.data != null) {
                    _token.value = res.data.token
                    _currentUser.value = res.data.user
                    loadMyReviews()
                    onSuccess()
                } else {
                    onError(res.msg.ifBlank { "Invalid username or password, please try again." })
                }
            } catch (e: Exception) {
                onError("Invalid username or password, please try again.")
            }
        }
    }

    fun register(username: String, password: String, nickName: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val res = RetrofitClient.api.register(RegisterRequest(username = username, password = password, nickName = nickName))
                if (res.code == 200 && res.data != null) {
                    _token.value = res.data.token
                    _currentUser.value = res.data.user
                    loadMyReviews()
                    onSuccess()
                } else {
                    onError(res.msg.ifBlank { "Registration failed" })
                }
            } catch (e: Exception) {
                onError(e.message ?: "Registration error")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _token.value = null
        _myReviews.value = emptyList()
    }

    fun loadReviews(businessType: String, businessId: Long) {
        viewModelScope.launch {
            try {
                val type = if (businessType.startsWith("Restaurant", ignoreCase = true)) "restaurant" else "scenic"
                val res = RetrofitClient.api.getReviews(businessType = type, businessId = businessId)
                if (res.code == 200 && res.data != null) {
                    _reviews.value = res.data.rows
                }
            } catch (e: Exception) {
                _reviews.value = emptyList()
            }
        }
    }

    fun addReview(
        businessType: String,
        businessId: Long,
        rating: String,
        content: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val curToken = _token.value
        if (curToken == null) {
            onError("Please login first")
            return
        }
        viewModelScope.launch {
            try {
                val type = if (businessType.startsWith("Restaurant", ignoreCase = true)) "restaurant" else "scenic"
                val res = RetrofitClient.api.addReview(
                    token = curToken,
                    request = AddReviewRequest(
                        businessType = type,
                        businessId = businessId,
                        rating = rating,
                        reviewContent = content
                    )
                )
                if (res.code == 200) {
                    loadReviews(businessType, businessId)
                    loadMyReviews()
                    onSuccess()
                } else {
                    onError(res.msg.ifBlank { "Failed to add review" })
                }
            } catch (e: Exception) {
                onError(e.message ?: "Error sending review")
            }
        }
    }

    fun loadMyReviews() {
        val curToken = _token.value ?: return
        viewModelScope.launch {
            try {
                val res = RetrofitClient.api.getMyReviews(token = curToken)
                if (res.code == 200 && res.data != null) {
                    _myReviews.value = res.data.rows
                }
            } catch (e: Exception) {
                _myReviews.value = emptyList()
            }
        }
    }

    fun deleteReview(reviewId: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val curToken = _token.value ?: return
        viewModelScope.launch {
            try {
                val res = RetrofitClient.api.deleteReview(token = curToken, reviewIds = reviewId.toString())
                if (res.code == 200) {
                    _myReviews.value = _myReviews.value.filter { it.reviewId != reviewId }
                    onSuccess()
                } else {
                    onError(res.msg.ifBlank { "Failed to delete review" })
                }
            } catch (e: Exception) {
                onError(e.message ?: "Error deleting review")
            }
        }
    }
}
