package org.example.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var userId: Long? = null,
    var username: String = "",
    var password: String = "",
    var nickName: String = "",
    var email: String = "",
    var phone: String = "",
    var avatar: String = "",
    var createTime: String = ""
)

@Entity
@Table(name = "restaurants")
class Restaurant(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var restaurantId: Long? = null,
    var restaurantName: String = "",
    var address: String = "",
    var phone: String = "",
    var rating: String = "5.0",
    var priceRange: String = "",
    var restaurantType: String = "",
    var imageUrl: String = "",
    var description: String = "",
    var distance: String = "0.0",
    var businessType: String = "restaurant",
    var businessHours: String = "09:00-21:00",
    var remainingParkingSpaces: Int = 0
)

@Entity
@Table(name = "scenic_spots")
class ScenicSpot(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var spotId: Long? = null,
    var spotName: String = "",
    var address: String = "",
    var latitude: String = "",
    var longitude: String = "",
    var phone: String = "",
    var rating: String = "5.0",
    var ticketPrice: String = "",
    var openingHours: String = "",
    var touristPeriod: String = "",
    var description: String = "",
    var distance: String = "0.0",
    var imageUrl: String = "",
    var businessType: String = "scenic"
)

@Entity
@Table(name = "reviews")
class Review(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var reviewId: Long? = null,
    var userId: Long = 0,
    var userName: String = "",
    var businessType: String = "",
    var businessId: Long = 0,
    var rating: String = "5",
    var reviewContent: String = "",
    var reviewTime: String = "",
    @ElementCollection(fetch = FetchType.EAGER)
    var imageUrls: MutableList<String> = mutableListOf(),
    var likeCount: Int = 0
)

@Entity
@Table(name = "congestions")
class Congestion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var congestionId: Long? = null,
    var roadName: String = "",
    var startLatitude: String = "",
    var startLongitude: String = "",
    var startAddress: String = "",
    var endLatitude: String = "",
    var endLongitude: String = "",
    var endAddress: String = "",
    var congestionLevel: String = "",
    var congestionLength: String = "",
    var delayTime: String = ""
)
