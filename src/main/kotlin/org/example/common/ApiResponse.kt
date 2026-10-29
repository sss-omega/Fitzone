package org.example.common

data class ApiResponse<T>(
    val code: Int = 200,
    val msg: String = "success",
    val data: T? = null
) {
    companion object {
        fun <T> success(data: T?): ApiResponse<T> = ApiResponse(code = 200, msg = "success", data = data)
        fun <T> error(code: Int, msg: String): ApiResponse<T> = ApiResponse(code = code, msg = msg, data = null)
    }
}

data class PageResult<T>(
    val total: Long,
    val rows: List<T>
)
