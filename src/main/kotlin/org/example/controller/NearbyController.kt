package org.example.controller

import org.example.common.ApiResponse
import org.example.common.PageResult
import org.example.model.Restaurant
import org.example.model.ScenicSpot
import org.example.repository.RestaurantRepository
import org.example.repository.ScenicSpotRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/system/api/restaurant")
@CrossOrigin(origins = ["*"])
class RestaurantController(
    private val repository: RestaurantRepository
) {
    @GetMapping("/list")
    fun list(
        @RequestParam(required = false) pageNum: Int?,
        @RequestParam(required = false) pageSize: Int?,
        @RequestParam(required = false) restaurantName: String?,
        @RequestParam(required = false) restaurantType: String?
    ): ApiResponse<PageResult<Restaurant>> {
        val cleanName = restaurantName?.trim()?.ifEmpty { null }
        val cleanType = restaurantType?.trim()?.ifEmpty { null }

        val filtered = repository.findAll().filter { r ->
            (cleanName == null || r.restaurantName.contains(cleanName, ignoreCase = true)) &&
            (cleanType == null || r.restaurantType.equals(cleanType, ignoreCase = true))
        }

        val total = filtered.size.toLong()
        val rows = if ((pageNum != null && pageNum <= 0) || (pageSize != null && pageSize <= 0)) {
            filtered
        } else {
            val page = (pageNum ?: 1) - 1
            val size = pageSize ?: 10
            filtered.drop(maxOf(0, page) * maxOf(1, size)).take(maxOf(1, size))
        }

        return ApiResponse.success(PageResult(total, rows))
    }

    @GetMapping("/{restaurantId}")
    fun getById(@PathVariable restaurantId: Long): ApiResponse<Restaurant> {
        val item = repository.findById(restaurantId).orElse(null)
            ?: return ApiResponse.error(404, "Restaurant not found")
        return ApiResponse.success(item)
    }
}

@RestController
@RequestMapping("/system/api/scenicSpot")
@CrossOrigin(origins = ["*"])
class ScenicSpotController(
    private val repository: ScenicSpotRepository
) {
    @GetMapping("/list")
    fun list(
        @RequestParam(required = false) pageNum: Int?,
        @RequestParam(required = false) pageSize: Int?,
        @RequestParam(required = false) spotName: String?,
        @RequestParam(required = false) address: String?
    ): ApiResponse<PageResult<ScenicSpot>> {
        val cleanName = spotName?.trim()?.ifEmpty { null }
        val cleanAddress = address?.trim()?.ifEmpty { null }

        val filtered = repository.findAll().filter { s ->
            (cleanName == null || s.spotName.contains(cleanName, ignoreCase = true)) &&
            (cleanAddress == null || s.address.contains(cleanAddress, ignoreCase = true))
        }

        val total = filtered.size.toLong()
        val rows = if ((pageNum != null && pageNum <= 0) || (pageSize != null && pageSize <= 0)) {
            filtered
        } else {
            val page = (pageNum ?: 1) - 1
            val size = pageSize ?: 10
            filtered.drop(maxOf(0, page) * maxOf(1, size)).take(maxOf(1, size))
        }

        return ApiResponse.success(PageResult(total, rows))
    }

    @GetMapping("/{spotId}")
    fun getById(@PathVariable spotId: Long): ApiResponse<ScenicSpot> {
        val item = repository.findById(spotId).orElse(null)
            ?: return ApiResponse.error(404, "Scenic spot not found")
        return ApiResponse.success(item)
    }
}
