package org.example.repository

import org.example.model.*
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
}

interface RestaurantRepository : JpaRepository<Restaurant, Long>

interface ScenicSpotRepository : JpaRepository<ScenicSpot, Long>

interface ReviewRepository : JpaRepository<Review, Long> {
    fun findByUserId(userId: Long): List<Review>
}

interface CongestionRepository : JpaRepository<Congestion, Long>
