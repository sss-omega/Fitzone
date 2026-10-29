package org.example.controller

import org.example.common.ApiResponse
import org.example.common.PageResult
import org.example.model.Congestion
import org.example.repository.CongestionRepository
import org.springframework.web.bind.annotation.*
import kotlin.math.*

data class RouteRequest(
    val pathWay: List<List<Double>> = emptyList(),
    val mode: String = "driving"
)

data class RouteResponse(
    val code: Int = 200,
    val msg: String = "success",
    val distance: String,
    val time: String,
    val path: List<List<Double>>,
    val startName: String = "Bayside Drive",
    val endName: String = "North Little Creek Road",
    val mode: String
)

@RestController
@CrossOrigin(origins = ["*"])
class RouteController(
    private val congestionRepository: CongestionRepository
) {

    @GetMapping("/system/api/congestion/list")
    fun congestions(
        @RequestParam(required = false) pageNum: Int?,
        @RequestParam(required = false) pageSize: Int?,
        @RequestParam(required = false) roadName: String?,
        @RequestParam(required = false) startLat: Double?,
        @RequestParam(required = false) startLng: Double?,
        @RequestParam(required = false) endLat: Double?,
        @RequestParam(required = false) endLng: Double?
    ): ApiResponse<PageResult<Congestion>> {
        val cleanName = roadName?.trim()?.ifEmpty { null }

        val filtered = congestionRepository.findAll().filter { c ->
            cleanName == null || c.roadName.contains(cleanName, ignoreCase = true)
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

    @PostMapping("/map_test/way")
    fun planRoute(@RequestBody req: RouteRequest): RouteResponse {
        val points = req.pathWay
        val fullPath = mutableListOf<List<Double>>()

        var totalDistMeters = 0.0

        if (points.isNotEmpty()) {
            fullPath.add(points.first())
            for (i in 0 until points.size - 1) {
                val p1 = points[i]
                val p2 = points[i + 1]
                val midLng = (p1[0] + p2[0]) / 2.0
                val midLat = (p1[1] + p2[1]) / 2.0
                fullPath.add(listOf(midLng, midLat))
                fullPath.add(p2)

                val dLat = Math.toRadians(p2[1] - p1[1])
                val dLng = Math.toRadians(p2[0] - p1[0])
                val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(p1[1])) * cos(Math.toRadians(p2[1])) * sin(dLng / 2).pow(2)
                val c = 2 * atan2(sqrt(a), sqrt(1 - a))
                totalDistMeters += 6371000.0 * c
            }
        }

        if (totalDistMeters < 100.0) totalDistMeters = 7942.0

        val speedMps = when (req.mode.lowercase()) {
            "walking" -> 1.4
            "cycling" -> 4.2
            else -> 11.1
        }
        val durationMs = ((totalDistMeters / speedMps) * 1000).toLong()

        return RouteResponse(
            code = 200,
            msg = "success",
            distance = "${totalDistMeters.roundToInt()}m",
            time = "${durationMs}ms",
            path = fullPath,
            startName = "Bayside Drive",
            endName = "North Little Creek Road",
            mode = req.mode
        )
    }
}
