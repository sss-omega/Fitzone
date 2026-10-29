package org.example.security

import org.example.model.User
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Service
class TokenService {
    private val tokenToUser = ConcurrentHashMap<String, User>()

    fun createToken(user: User): String {
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." + UUID.randomUUID().toString().replace("-", "")
        tokenToUser[token] = user
        return token
    }

    fun extractToken(authHeader: String?): String? {
        if (authHeader.isNullOrBlank()) return null
        return authHeader.removePrefix("Bearer ").removePrefix("Token ").trim()
    }

    fun getUser(authHeader: String?): User? {
        val token = extractToken(authHeader) ?: return null
        return tokenToUser[token]
    }

    fun removeToken(authHeader: String?) {
        val token = extractToken(authHeader) ?: return
        tokenToUser.remove(token)
    }

    fun isValid(authHeader: String?): Boolean {
        return getUser(authHeader) != null
    }
}
