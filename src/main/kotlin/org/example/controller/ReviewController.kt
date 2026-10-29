package org.example.controller

import org.example.common.ApiResponse
import org.example.common.PageResult
import org.example.model.Review
import org.example.repository.ReviewRepository
import org.example.security.TokenService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class AddReviewRequest(
    val businessType: String = "",
    val businessId: Long = 0,
    val rating: String = "5",
    val reviewContent: String = "",
    val imageUrls: List<String> = emptyList()
)

@RestController
@RequestMapping("/system/api/review")
@CrossOrigin(origins = ["*"])
class ReviewController(
    private val reviewRepository: ReviewRepository,
    private val tokenService: TokenService
) {
    @GetMapping("/list")
    fun list(
        @RequestParam(required = false) pageNum: Int?,
        @RequestParam(required = false) pageSize: Int?,
        @RequestParam(required = false) businessType: String?,
        @RequestParam(required = false) businessId: Long?
    ): ApiResponse<PageResult<Review>> {
        val cleanType = businessType?.trim()?.ifEmpty { null }

        val filtered = reviewRepository.findAll().filter { r ->
            (cleanType == null || r.businessType.equals(cleanType, ignoreCase = true)) &&
            (businessId == null || r.businessId == businessId)
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

    @GetMapping("/mine")
    fun mine(
        @RequestHeader(value = "Authorization", required = false) auth: String?,
        @RequestParam(required = false) pageNum: Int?,
        @RequestParam(required = false) pageSize: Int?,
        @RequestParam(required = false) businessType: String?,
        @RequestParam(required = false) businessId: Long?
    ): ResponseEntity<ApiResponse<PageResult<Review>>> {
        val user = tokenService.getUser(auth)
            ?: return ResponseEntity.status(401).body(ApiResponse.error(401, "The token is invalid or expired"))

        val cleanType = businessType?.trim()?.ifEmpty { null }

        val filtered = reviewRepository.findByUserId(user.userId!!).filter { r ->
            (cleanType == null || r.businessType.equals(cleanType, ignoreCase = true)) &&
            (businessId == null || r.businessId == businessId)
        }

        val total = filtered.size.toLong()
        val rows = if ((pageNum != null && pageNum <= 0) || (pageSize != null && pageSize <= 0)) {
            filtered
        } else {
            val page = (pageNum ?: 1) - 1
            val size = pageSize ?: 10
            filtered.drop(maxOf(0, page) * maxOf(1, size)).take(maxOf(1, size))
        }

        return ResponseEntity.ok(ApiResponse.success(PageResult(total, rows)))
    }

    @PostMapping("/add")
    fun add(
        @RequestHeader(value = "Authorization", required = false) auth: String?,
        @RequestBody req: AddReviewRequest
    ): ResponseEntity<ApiResponse<Int>> {
        val user = tokenService.getUser(auth)
            ?: return ResponseEntity.status(401).body(ApiResponse.error(401, "The token is invalid or expired"))

        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val review = Review(
            userId = user.userId!!,
            userName = user.nickName.ifEmpty { user.username },
            businessType = req.businessType,
            businessId = req.businessId,
            rating = req.rating,
            reviewContent = req.reviewContent,
            reviewTime = now,
            imageUrls = req.imageUrls.toMutableList(),
            likeCount = 0
        )
        reviewRepository.save(review)
        return ResponseEntity.ok(ApiResponse.success(1))
    }

    @DeleteMapping("/{reviewIds}")
    fun delete(
        @RequestHeader(value = "Authorization", required = false) auth: String?,
        @PathVariable reviewIds: String
    ): ResponseEntity<ApiResponse<Int>> {
        val user = tokenService.getUser(auth)
            ?: return ResponseEntity.status(401).body(ApiResponse.error(401, "The token is invalid or expired"))

        val ids = reviewIds.split(",").mapNotNull { it.trim().toLongOrNull() }
        var deletedCount = 0

        for (id in ids) {
            val review = reviewRepository.findById(id).orElse(null)
            if (review != null && review.userId == user.userId) {
                reviewRepository.delete(review)
                deletedCount++
            }
        }

        return ResponseEntity.ok(ApiResponse.success(deletedCount))
    }
}
