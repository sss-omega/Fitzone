package org.example.controller

import org.example.common.ApiResponse
import org.example.model.User
import org.example.repository.UserRepository
import org.example.security.TokenService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class LoginRequest(
    val username: String = "",
    val password: String = ""
)

data class RegisterRequest(
    val username: String = "",
    val password: String = "",
    val nickName: String = "",
    val email: String = ""
)

data class LoginResponse(
    val token: String,
    val expiresIn: Long = 3600,
    val user: User
)

data class UserInfoResponse(
    val user: User
)

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["*"])
class AuthController(
    private val userRepository: UserRepository,
    private val tokenService: TokenService
) {
    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest): ResponseEntity<ApiResponse<Any>> {
        val user = userRepository.findByUsername(req.username)
        if (user == null || user.password != req.password) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "Invalid username or password"))
        }

        val token = tokenService.createToken(user)
        return ResponseEntity.ok(ApiResponse.success(LoginResponse(token = token, expiresIn = 3600, user = user)))
    }

    @PostMapping("/register")
    fun register(@RequestBody req: RegisterRequest): ResponseEntity<ApiResponse<Any>> {
        if (req.username.isBlank() || req.password.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Username and password cannot be empty"))
        }
        if (userRepository.findByUsername(req.username) != null) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Username already exists"))
        }
        val user = User(
            username = req.username,
            password = req.password,
            nickName = if (req.nickName.isNotBlank()) req.nickName else req.username,
            email = req.email,
            avatar = "/upload/20260602/avatar_user1.png",
            createTime = "2026-10-25 12:00:00"
        )
        val saved = userRepository.save(user)
        val token = tokenService.createToken(saved)
        return ResponseEntity.ok(ApiResponse.success(LoginResponse(token = token, expiresIn = 3600, user = saved)))
    }

    @GetMapping("/info")
    fun info(@RequestHeader(value = "Authorization", required = false) auth: String?): ResponseEntity<ApiResponse<Any>> {
        val user = tokenService.getUser(auth)
            ?: return ResponseEntity.status(401).body(ApiResponse.error(401, "The token is invalid or expired"))
        return ResponseEntity.ok(ApiResponse.success(UserInfoResponse(user = user)))
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader(value = "Authorization", required = false) auth: String?): ApiResponse<String> {
        tokenService.removeToken(auth)
        return ApiResponse.success("Exit successful")
    }

    @GetMapping("/check")
    fun check(@RequestHeader(value = "Authorization", required = false) auth: String?): ResponseEntity<ApiResponse<String>> {
        if (!tokenService.isValid(auth)) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "The token is invalid or expired"))
        }
        return ResponseEntity.ok(ApiResponse.success("success"))
    }
}
