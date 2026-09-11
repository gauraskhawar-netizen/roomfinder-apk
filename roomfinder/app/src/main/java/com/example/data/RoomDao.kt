package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomListingDao {
    @Query("SELECT * FROM room_listings ORDER BY createdAt DESC")
    fun getAllListings(): Flow<List<RoomListingEntity>>

    @Query("SELECT * FROM room_listings WHERE id = :id LIMIT 1")
    fun getListingById(id: String): Flow<RoomListingEntity?>

    @Query("SELECT * FROM room_listings WHERE ownerEmail = :ownerEmail ORDER BY createdAt DESC")
    fun getListingsByOwner(ownerEmail: String): Flow<List<RoomListingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: RoomListingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListings(listings: List<RoomListingEntity>)

    @Update
    suspend fun updateListing(listing: RoomListingEntity)

    @Query("DELETE FROM room_listings WHERE id = :id")
    suspend fun deleteListing(id: String)

    @Query("UPDATE room_listings SET isRented = :isRented WHERE id = :id")
    suspend fun updateRentedStatus(id: String, isRented: Boolean)

    @Query("SELECT COUNT(*) FROM room_listings")
    suspend fun countListings(): Int

    @Query("SELECT * FROM room_listings")
    suspend fun getAllListingsSnapshot(): List<RoomListingEntity>

    // Favorites
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE listingId = :listingId AND userEmail = :userEmail")
    suspend fun removeFavorite(listingId: String, userEmail: String)

    @Query("SELECT listingId FROM favorites WHERE userEmail = :userEmail")
    fun getFavoriteListingIds(userEmail: String): Flow<List<String>>

    @Query("""
        SELECT r.* FROM room_listings r
        INNER JOIN favorites f ON r.id = f.listingId
        WHERE f.userEmail = :userEmail
        ORDER BY f.savedAt DESC
    """)
    fun getFavoriteListings(userEmail: String): Flow<List<RoomListingEntity>>
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    fun getUser(email: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun findUser(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) AND password = :password LIMIT 1")
    suspend fun authenticate(email: String, password: String): UserEntity?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
}
