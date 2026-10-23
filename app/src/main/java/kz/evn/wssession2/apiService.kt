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

data class RestaurantDto(
    val restaurantName: String = "",
    val rating: String? = "0.0",
    val restaurantType: String? = "Restaurant",
    val imageUrl: String? = null,
    val distance: String? = "0.0"
)

data class ScenicSpotDto(
    val spotName: String = "",
    val rating: String? = "0.0",
    val imageUrl: String? = null,
    val distance: String? = "0.0"
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
}

object RetrofitClient {
    const val BASE_URL = "https://pureness-overbite-cod.ngrok-free.dev/"

    private val okHttpClient = OkHttpClient.Builder()
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
