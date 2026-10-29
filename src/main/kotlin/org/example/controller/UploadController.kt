package org.example.controller

import org.example.common.ApiResponse
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class FileDetail(
    val fileName: String,
    val url: String,
    val size: Long
)

data class UploadResult(
    val success: Int,
    val fail: Int,
    val total: Int,
    val details: List<FileDetail>
)

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = ["*"])
class UploadController {

    @PostMapping("/images")
    fun uploadImages(@RequestParam("files") files: Array<MultipartFile>): ApiResponse<UploadResult> {
        val details = mutableListOf<FileDetail>()
        val dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val uploadDir = File("./uploads/$dateFolder")
        if (!uploadDir.exists()) uploadDir.mkdirs()

        var successCount = 0
        var failCount = 0

        for (file in files) {
            try {
                if (file.isEmpty) {
                    failCount++
                    continue
                }
                val originalName = file.originalFilename ?: "image.png"
                val ext = if (originalName.contains(".")) originalName.substringAfterLast(".") else "png"
                val generatedName = "${System.nanoTime()}.$ext"
                val destFile = File(uploadDir, generatedName)
                file.transferTo(destFile)

                val relativePath = "/upload/$dateFolder/$generatedName"
                val fullUrl = "http://localhost:5127$relativePath"

                details.add(
                    FileDetail(
                        fileName = relativePath,
                        url = fullUrl,
                        size = file.size
                    )
                )
                successCount++
            } catch (e: Exception) {
                failCount++
            }
        }

        return ApiResponse(
            code = 200,
            msg = "Upload completed",
            data = UploadResult(
                success = successCount,
                fail = failCount,
                total = files.size,
                details = details
            )
        )
    }
}
