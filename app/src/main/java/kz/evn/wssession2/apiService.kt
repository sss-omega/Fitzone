package kz.evn.wssession2

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

data class ApiResponse<T>(
    val code: Int = 200,
    val msg: String = "",
    val data: T? = null
)

data class PageResult<T>(
    val total: Long = 0,
    val rows: List<T> = emptyList()
)

data class RestaurantDto(
    val restaurantId: Long? = null,
    val restaurantName: String = "",
    val address: String? = null,
    val phone: String? = null,
    val rating: String? = "0.0",
    val priceRange: String? = null,
    val restaurantType: String? = null,
    val imageUrl: String? = null,
    val description: String? = null,
    val distance: String? = "0.0",
    val businessHours: String? = null,
    val remainingParkingSpaces: Int? = 0
)

data class ScenicSpotDto(
    val spotId: Long? = null,
    val spotName: String = "",
    val address: String? = null,
    val phone: String? = null,
    val rating: String? = "0.0",
    val ticketPrice: String? = null,
    val openingHours: String? = null,
    val touristPeriod: String? = null,
    val description: String? = null,
    val distance: String? = "0.0",
    val imageUrl: String? = null
)

data class ReviewDto(
    val reviewId: Long? = null,
    val userId: Long? = null,
    val userName: String? = null,
    val businessType: String? = null,
    val businessId: Long? = null,
    val rating: String? = "5",
    val reviewContent: String? = "",
    val reviewTime: String? = "",
    val imageUrls: List<String> = emptyList(),
    val likeCount: Int = 0
)

data class UserDto(
    val userId: Long? = null,
    val username: String = "",
    val nickName: String = "",
    val email: String = "",
    val avatar: String = ""
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val nickName: String = "",
    val email: String = ""
)

data class LoginResponse(
    val token: String,
    val user: UserDto
)

data class AddReviewRequest(
    val businessType: String,
    val businessId: Long,
    val rating: String,
    val reviewContent: String,
    val imageUrls: List<String> = emptyList()
)

interface ApiService {
    @GET("system/api/restaurant/list")
    suspend fun getRestaurants(
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): ApiResponse<PageResult<RestaurantDto>>

    @GET("system/api/scenicSpot/list")
    suspend fun getScenicSpots(
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): ApiResponse<PageResult<ScenicSpotDto>>

    @GET("system/api/review/list")
    suspend fun getReviews(
        @Query("businessType") businessType: String?,
        @Query("businessId") businessId: Long?,
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): ApiResponse<PageResult<ReviewDto>>

    @GET("system/api/review/mine")
    suspend fun getMyReviews(
        @Header("Authorization") token: String,
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): ApiResponse<PageResult<ReviewDto>>

    @POST("system/api/review/add")
    suspend fun addReview(
        @Header("Authorization") token: String,
        @Body request: AddReviewRequest
    ): ApiResponse<Int>

    @DELETE("system/api/review/{reviewIds}")
    suspend fun deleteReview(
        @Header("Authorization") token: String,
        @Path("reviewIds") reviewIds: String
    ): ApiResponse<Int>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): ApiResponse<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): ApiResponse<LoginResponse>
}

object RetrofitClient {
    const val BASE_URL = "https://pureness-overbite-cod.ngrok-free.dev/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("ngrok-skip-browser-warning", "true")
                .build()
            chain.proceed(request)
        }
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    fun resolveImageUrl(rawUrl: String?): String? {
        if (rawUrl.isNullOrBlank()) return null
        return if (rawUrl.startsWith("http")) rawUrl else "${BASE_URL.trimEnd('/')}/${rawUrl.trimStart('/')}"
    }
}
