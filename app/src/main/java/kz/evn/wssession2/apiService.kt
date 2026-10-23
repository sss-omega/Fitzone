package kz.evn.wssession2

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
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

// ========== НАЧАЛО: ПОЛЯ ДЛЯ ВТОРОГО ЭКРАНА С ДАННЫМИ (НАПИСАНО ЗДЕСЬ) ==========
data class RestaurantDto(
    val restaurantName: String = "",
    val rating: String? = "0.0",
    val restaurantType: String? = "Restaurant",
    val imageUrl: String? = null,
    val distance: String? = "0.0",
    val businessHours: String? = null,
    val remainingParkingSpaces: Int? = null,
    val priceRange: String? = null,
    val address: String? = null,
    val description: String? = null
)

data class ScenicSpotDto(
    val spotName: String = "",
    val rating: String? = "0.0",
    val imageUrl: String? = null,
    val distance: String? = "0.0",
    val openingHours: String? = null,
    val ticketPrice: String? = null,
    val touristPeriod: String? = null,
    val address: String? = null,
    val description: String? = null
)
// ========== КОНЕЦ: ПОЛЯ ДЛЯ ВТОРОГО ЭКРАНА С ДАННЫМИ ==========

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
