package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "room_listings")
data class RoomListingEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val city: String,
    val area: String,
    val fullAddress: String,
    val monthlyRent: Double,
    val securityDeposit: Double,
    val roomType: String, // "Single Room", "Double Room", "Studio Apartment", "1BHK", "Shared Room"
    val furnishingStatus: String, // "Furnished", "Semi-Furnished", "Unfurnished"
    val availableDate: String, // "Available Now", "Oct 15, 2026", etc.
    val isAvailableNow: Boolean,
    val isRented: Boolean = false,
    val facilities: String, // comma-separated: "Wi-Fi,Water 24/7,Electricity,Parking,Attached Bathroom,Air Conditioning"
    val description: String,
    val ownerName: String,
    val ownerPhone: String,
    val ownerEmail: String,
    val imageUrl: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getFacilityList(): List<String> {
        return if (facilities.isBlank()) emptyList()
        else facilities.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }
}

@Entity(tableName = "favorites", primaryKeys = ["listingId", "userEmail"])
data class FavoriteEntity(
    val listingId: String,
    val userEmail: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val email: String,
    val name: String,
    val role: String, // "SEEKER" or "OWNER"
    val phone: String = "",
    val password: String = ""
)
