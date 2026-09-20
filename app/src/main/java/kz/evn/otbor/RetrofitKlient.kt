package kz.evn.otbor

import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitKlient {

    var baseUrl = "http://192.168.20.176:5000/"
    var token = ""

    private val okHttp = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val builder = chain.request().newBuilder()
            if (token.isNotEmpty()) {
                builder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(builder.build())
        }
        .build()

    val servis: ApiServis
        get() {
            val url = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
            return Retrofit.Builder()
                .baseUrl(url)
                .client(okHttp)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiServis::class.java)
        }

    fun <T> poluchitOshibku(response: Response<T>, poUmolchaniyu: String): String {
        return try {
            val text = response.errorBody()?.string() ?: ""
            val json = JSONObject(text)
            json.optString("error", poUmolchaniyu)
        } catch (e: Exception) {
            poUmolchaniyu
        }
    }
}

typealias Api = RetrofitKlient
