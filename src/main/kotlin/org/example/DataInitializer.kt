package org.example

import org.example.model.*
import org.example.repository.*
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val restaurantRepository: RestaurantRepository,
    private val scenicSpotRepository: ScenicSpotRepository,
    private val reviewRepository: ReviewRepository,
    private val congestionRepository: CongestionRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        if (userRepository.count() == 0L) {
            val admin = User(
                username = "admin",
                password = "123456",
                nickName = "Admin",
                email = "admin@example.com",
                phone = "13800138000",
                avatar = "/upload/20260602/639160099670709379.png",
                createTime = "2026-05-11 15:14:12"
            )
            val user1 = User(
                username = "user1",
                password = "123456",
                nickName = "Zhang San",
                email = "user1@example.com",
                phone = "13900139000",
                avatar = "/upload/20260602/avatar_user1.png",
                createTime = "2026-05-12 10:20:00"
            )
            userRepository.saveAll(listOf(admin, user1))
        }

        if (restaurantRepository.count() == 0L) {
            restaurantRepository.saveAll(
                listOf(
                    Restaurant(
                        restaurantName = "Quanjude Roast Duck",
                        address = "30 Qianmen Street, Dongcheng District, Beijing",
                        phone = "010-12345678",
                        rating = "4.5",
                        priceRange = "$15-45",
                        restaurantType = "Beijing Cuisine",
                        imageUrl = "/upload/20260602/639160074121552317.png",
                        description = "Century-old roast duck restaurant",
                        distance = "3.4",
                        businessType = "restaurant",
                        businessHours = "10:00-22:00",
                        remainingParkingSpaces = 15
                    ),
                    Restaurant(
                        restaurantName = "Lu Ding Ji",
                        address = "12 Wangfujing Street, Dongcheng District, Beijing",
                        phone = "010-87654321",
                        rating = "4.8",
                        priceRange = "$20-60",
                        restaurantType = "Sichuan Cuisine",
                        imageUrl = "/upload/20260602/sample2.png",
                        description = "Famous spicy hotpot",
                        distance = "1.8",
                        businessType = "restaurant",
                        businessHours = "11:00-23:00",
                        remainingParkingSpaces = 8
                    )
                )
            )
        }

        if (scenicSpotRepository.count() == 0L) {
            scenicSpotRepository.saveAll(
                listOf(
                    ScenicSpot(
                        spotName = "Palace Museum",
                        address = "4 Jingshan Front Street, Dongcheng District, Beijing",
                        latitude = "39.9163",
                        longitude = "116.3972",
                        phone = "010-85007047",
                        rating = "5.0",
                        ticketPrice = "$9",
                        openingHours = "8:30-17:00",
                        touristPeriod = "09:00-11:00",
                        description = "Imperial palace of Ming and Qing dynasties",
                        distance = "3.4",
                        imageUrl = "/upload/20260602/639160112022167429.png",
                        businessType = "scenic"
                    ),
                    ScenicSpot(
                        spotName = "Canton Tower",
                        address = "222 Yuejiang West Road, Haizhu District, Guangzhou",
                        latitude = "23.1064",
                        longitude = "113.3245",
                        phone = "020-89338222",
                        rating = "4.7",
                        ticketPrice = "$22",
                        openingHours = "9:00-22:30",
                        touristPeriod = "18:00-21:00",
                        description = "Landmark television and sightseeing tower",
                        distance = "5.1",
                        imageUrl = "/upload/20260602/canton_tower.png",
                        businessType = "scenic"
                    ),
                    ScenicSpot(
                        spotName = "Summer Palace",
                        address = "19 Xinjiangongmen Road, Haidian District, Beijing",
                        latitude = "39.9999",
                        longitude = "116.2755",
                        phone = "010-62881144",
                        rating = "4.9",
                        ticketPrice = "$5",
                        openingHours = "6:30-18:00",
                        touristPeriod = "08:00-12:00",
                        description = "Vast ensemble of lakes, gardens and palaces",
                        distance = "12.0",
                        imageUrl = "/upload/20260602/summer_palace.png",
                        businessType = "scenic"
                    )
                )
            )
        }

        if (reviewRepository.count() == 0L) {
            val user1 = userRepository.findByUsername("user1") ?: userRepository.findAll().first()
            val admin = userRepository.findByUsername("admin") ?: user1

            val reviews = mutableListOf<Review>()
            for (i in 1..13) {
                reviews.add(
                    Review(
                        userId = user1.userId!!,
                        userName = user1.nickName,
                        businessType = "restaurant",
                        businessId = 1L,
                        rating = "5",
                        reviewContent = "Delicious food! Review #$i",
                        reviewTime = "2026-05-15 12:00:00",
                        imageUrls = mutableListOf("/upload/20260602/639160074121552317.png"),
                        likeCount = 10 + i
                    )
                )
            }
            for (i in 1..10) {
                reviews.add(
                    Review(
                        userId = user1.userId!!,
                        userName = user1.nickName,
                        businessType = "scenic",
                        businessId = 1L,
                        rating = "5",
                        reviewContent = "Beautiful spot! Review #$i",
                        reviewTime = "2026-05-16 14:00:00",
                        imageUrls = mutableListOf("/upload/20260602/639160112022167429.png"),
                        likeCount = 5 + i
                    )
                )
            }
            reviews.add(
                Review(
                    userId = admin.userId!!,
                    userName = admin.nickName,
                    businessType = "scenic",
                    businessId = 2L,
                    rating = "5",
                    reviewContent = "Hi, This is TEST",
                    reviewTime = "2026-05-28 13:36:39",
                    imageUrls = mutableListOf("/upload/20260602/639160112022167429.png"),
                    likeCount = 10
                )
            )
            reviewRepository.saveAll(reviews)
        }

        if (congestionRepository.count() == 0L) {
            congestionRepository.saveAll(
                listOf(
                    Congestion(
                        roadName = "Central Ave",
                        startLatitude = "39.1500",
                        startLongitude = "-75.4500",
                        startAddress = "Dover Central Ave",
                        endLatitude = "39.1600",
                        endLongitude = "-75.4400",
                        endAddress = "Dover Capitol",
                        congestionLevel = "severe",
                        congestionLength = "900",
                        delayTime = "15 minutes"
                    ),
                    Congestion(
                        roadName = "North DuPont Hwy",
                        startLatitude = "39.1700",
                        startLongitude = "-75.5200",
                        startAddress = "Dover Mall",
                        endLatitude = "39.1800",
                        endLongitude = "-75.5100",
                        endAddress = "Silver Lake",
                        congestionLevel = "moderate",
                        congestionLength = "450",
                        delayTime = "5 minutes"
                    )
                )
            )
        }
    }
}
