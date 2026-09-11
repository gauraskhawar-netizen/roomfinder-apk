package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class RoomRepository(
    private val roomDao: RoomListingDao,
    private val userDao: UserDao
) {
    val allListings: Flow<List<RoomListingEntity>> = roomDao.getAllListings()

    fun getListingById(id: String): Flow<RoomListingEntity?> = roomDao.getListingById(id)

    fun getListingsByOwner(ownerEmail: String): Flow<List<RoomListingEntity>> =
        roomDao.getListingsByOwner(ownerEmail)

    fun getFavoriteIds(userEmail: String): Flow<List<String>> =
        roomDao.getFavoriteListingIds(userEmail)

    fun getFavoriteListings(userEmail: String): Flow<List<RoomListingEntity>> =
        roomDao.getFavoriteListings(userEmail)

    fun getUser(email: String): Flow<UserEntity?> = userDao.getUser(email)

    suspend fun insertListing(listing: RoomListingEntity) = withContext(Dispatchers.IO) {
        roomDao.insertListing(listing)
    }

    suspend fun updateListing(listing: RoomListingEntity) = withContext(Dispatchers.IO) {
        roomDao.updateListing(listing)
    }

    suspend fun deleteListing(id: String) = withContext(Dispatchers.IO) {
        roomDao.deleteListing(id)
    }

    suspend fun setRentedStatus(id: String, isRented: Boolean) = withContext(Dispatchers.IO) {
        roomDao.updateRentedStatus(id, isRented)
    }

    suspend fun toggleFavorite(listingId: String, userEmail: String, isCurrentlyFav: Boolean) =
        withContext(Dispatchers.IO) {
            if (isCurrentlyFav) {
                roomDao.removeFavorite(listingId, userEmail)
            } else {
                roomDao.addFavorite(FavoriteEntity(listingId = listingId, userEmail = userEmail))
            }
        }

    suspend fun registerOrUpdateUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.insertUser(user)
    }

    suspend fun findUser(email: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.findUser(email.trim())
    }

    suspend fun authenticateUser(email: String, password: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.authenticate(email.trim(), password.trim())
    }

    suspend fun registerUser(user: UserEntity): Result<UserEntity> = withContext(Dispatchers.IO) {
        val existing = userDao.findUser(user.email.trim())
        if (existing != null) {
            Result.failure(Exception("An account with this email already exists"))
        } else {
            userDao.insertUser(user)
            Result.success(user)
        }
    }

    suspend fun seedSampleDataIfEmpty() = withContext(Dispatchers.IO) {
        val indianSampleRooms = listOf(
            RoomListingEntity(
                id = "room-101",
                title = "Bright Single Room near City Center",
                city = "Gwalior",
                area = "City Center, Madhya Pradesh",
                fullAddress = "Plot 24, Patel Nagar, Near City Center Mall, Gwalior, MP 474011",
                monthlyRent = 3500.0,
                securityDeposit = 7000.0,
                roomType = "Single Room",
                furnishingStatus = "Furnished",
                availableDate = "Available Now",
                isAvailableNow = true,
                isRented = false,
                facilities = "Wi-Fi, Water 24/7, Electricity, Attached Bathroom, RO Water, Bed & Almirah",
                description = "Well-ventilated furnished single room with attached bathroom in prime City Center area. Walking distance to coaching centers, supermarkets, and local transport. 24/7 water supply, power backup, and high-speed Wi-Fi included.",
                ownerName = "Rahul Sharma",
                ownerPhone = "+919826012345",
                ownerEmail = "rahul.owner@roomfinder.in",
                imageUrl = "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?auto=format&fit=crop&w=1000&q=80"
            ),
            RoomListingEntity(
                id = "room-102",
                title = "Independent 1BHK Flat in MP Nagar",
                city = "Bhopal",
                area = "MP Nagar Zone-II, Madhya Pradesh",
                fullAddress = "B-42, Zone-II, Maharana Pratap Nagar, Bhopal, MP 462011",
                monthlyRent = 9500.0,
                securityDeposit = 19000.0,
                roomType = "1BHK",
                furnishingStatus = "Furnished",
                availableDate = "Available Now",
                isAvailableNow = true,
                isRented = false,
                facilities = "Wi-Fi, Water 24/7, Electricity, Parking, Kitchen Access, Balcony, Air Conditioning",
                description = "Spacious modern 1BHK with separate living room, modular kitchen, and private balcony. Located in the commercial hub of Bhopal, close to DB City Mall and Habibganj (Rani Kamlapati) Railway Station. Ideal for working professionals or small families.",
                ownerName = "Anita Saxena",
                ownerPhone = "+919827054321",
                ownerEmail = "anita.saxena@roomfinder.in",
                imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=1000&q=80"
            ),
            RoomListingEntity(
                id = "room-103",
                title = "Budget Single Room in Vijay Nagar",
                city = "Indore",
                area = "Vijay Nagar, Madhya Pradesh",
                fullAddress = "105 Scheme No. 54, Near Meghdoot Garden, Vijay Nagar, Indore, MP 452010",
                monthlyRent = 4500.0,
                securityDeposit = 9000.0,
                roomType = "Single Room",
                furnishingStatus = "Semi-Furnished",
                availableDate = "Oct 1, 2026",
                isAvailableNow = false,
                isRented = false,
                facilities = "Wi-Fi, Water 24/7, Electricity, Kitchen Access, Bike Parking",
                description = "Peaceful and affordable single room in a vibrant neighborhood. Close to Vijay Nagar square, food streets (Chhappan/Sarafa access), and IT tech parks on AB Road. Includes bed, study table, ceiling fan, and geyser.",
                ownerName = "Vikram Chouhan",
                ownerPhone = "+919893011223",
                ownerEmail = "vikram.chouhan@roomfinder.in",
                imageUrl = "https://images.unsplash.com/photo-1595526114035-0d45ed16cfbf?auto=format&fit=crop&w=1000&q=80"
            ),
            RoomListingEntity(
                id = "room-104",
                title = "Premium 2BHK Apartment near Metro",
                city = "Delhi",
                area = "Lajpat Nagar / South Delhi",
                fullAddress = "D-18, Block D, Lajpat Nagar III, New Delhi, Delhi 110024",
                monthlyRent = 22000.0,
                securityDeposit = 44000.0,
                roomType = "2BHK",
                furnishingStatus = "Furnished",
                availableDate = "Available Now",
                isAvailableNow = true,
                isRented = false,
                facilities = "Wi-Fi, Water 24/7, Electricity, Parking, Attached Bathroom, Air Conditioning, Balcony, Lift",
                description = "Luxurious, fully furnished 2BHK flat with 2 attached bathrooms, modular kitchen, inverter backup, and air conditioning. Just 3 minutes walk to Lajpat Nagar Metro interchange station (Violet & Pink lines). Gated community with 24/7 security guard.",
                ownerName = "Manish Gupta",
                ownerPhone = "+919811099887",
                ownerEmail = "manish.gupta@roomfinder.in",
                imageUrl = "https://images.unsplash.com/photo-1502005229762-ee152da7c5d6?auto=format&fit=crop&w=1000&q=80"
            ),
            RoomListingEntity(
                id = "room-105",
                title = "Cozy PG Room for Students & Professionals",
                city = "Bhopal",
                area = "Arera Colony, Madhya Pradesh",
                fullAddress = "E-3/120, Arera Colony, Near 10 No. Market, Bhopal, MP 462016",
                monthlyRent = 5500.0,
                securityDeposit = 5500.0,
                roomType = "PG",
                furnishingStatus = "Furnished",
                availableDate = "Available Now",
                isAvailableNow = true,
                isRented = false,
                facilities = "Wi-Fi, Water 24/7, Electricity, Attached Bathroom, Meals Available, RO Water, Washing Machine",
                description = "Clean, homely PG accommodation with twin-sharing or private single bed option in posh Arera Colony. Daily nutritious home-cooked breakfast and dinner option, high-speed Wi-Fi, laundry facility, and housekeeping included.",
                ownerName = "Sunita Joshi",
                ownerPhone = "+919826188776",
                ownerEmail = "sunita.joshi@roomfinder.in",
                imageUrl = "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=1000&q=80"
            ),
            RoomListingEntity(
                id = "room-106",
                title = "Modern 1BHK Sea-Breeze Flat in Andheri West",
                city = "Mumbai",
                area = "Andheri West, Maharashtra",
                fullAddress = "402, Sea Green Heights, Lokhandwala Complex, Andheri West, Mumbai, MH 400053",
                monthlyRent = 14500.0,
                securityDeposit = 30000.0,
                roomType = "1BHK",
                furnishingStatus = "Furnished",
                availableDate = "Available Now",
                isAvailableNow = true,
                isRented = false,
                facilities = "Wi-Fi, Water 24/7, Electricity, Parking, Attached Bathroom, Air Conditioning, Lift, Security",
                description = "Stylish 1BHK apartment in Lokhandwala Complex, Andheri West. Quick access to DN Nagar Metro Station, Versova beach, and prime entertainment hotspots. Fully air-conditioned, with modular kitchen, piped gas connection, and 24/7 CCTV security.",
                ownerName = "Rajesh Kulkarni",
                ownerPhone = "+919820011223",
                ownerEmail = "rajesh.kulkarni@roomfinder.in",
                imageUrl = "https://images.unsplash.com/photo-1540518614846-7ede433c4550?auto=format&fit=crop&w=1000&q=80"
            ),
            RoomListingEntity(
                id = "room-107",
                title = "Budget-Friendly PG Accommodation in Lashkar",
                city = "Gwalior",
                area = "Lashkar, Madhya Pradesh",
                fullAddress = "12, Daulat Ganj, Lashkar, Gwalior, MP 474001",
                monthlyRent = 3000.0,
                securityDeposit = 3000.0,
                roomType = "PG",
                furnishingStatus = "Furnished",
                availableDate = "Available Now",
                isAvailableNow = true,
                isRented = false,
                facilities = "Wi-Fi, Water 24/7, Electricity, RO Water, Study Table, Bed & Mattress",
                description = "Affordable PG room ideal for college students and job aspirants in the heart of Lashkar market. Very close to railway station, bus stand, and major coaching institutes. Purified water, regular cleaning, and peaceful atmosphere.",
                ownerName = "Dinesh Tiwari",
                ownerPhone = "+919425112233",
                ownerEmail = "dinesh.tiwari@roomfinder.in",
                imageUrl = "https://images.unsplash.com/photo-1555854877-bab0e564b8d5?auto=format&fit=crop&w=1000&q=80"
            ),
            RoomListingEntity(
                id = "room-108",
                title = "Spacious 2BHK Family Flat in Palasia",
                city = "Indore",
                area = "Old Palasia, Madhya Pradesh",
                fullAddress = "204, Royal Palms, Greater Kailash Road, Old Palasia, Indore, MP 452001",
                monthlyRent = 16000.0,
                securityDeposit = 32000.0,
                roomType = "2BHK",
                furnishingStatus = "Semi-Furnished",
                availableDate = "Oct 15, 2026",
                isAvailableNow = false,
                isRented = false,
                facilities = "Wi-Fi, Water 24/7, Electricity, Parking, Lift, Balcony, Security",
                description = "Elegantly constructed 2BHK with vitrified tiles, 2 bathrooms, 2 wide balconies, and designated covered car parking. Located in the posh residential colony of Old Palasia, near renowned schools, hospitals, and Saket Club.",
                ownerName = "Rahul Sharma",
                ownerPhone = "+919826012345",
                ownerEmail = "rahul.owner@roomfinder.in",
                imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=1000&q=80"
            )
        )

        // Seed or update initial Indian owner and seeker users with passwords
        userDao.insertUser(
            UserEntity(
                email = "rahul.owner@roomfinder.in",
                name = "Rahul Sharma",
                role = "OWNER",
                phone = "+919826012345",
                password = "owner123"
            )
        )
        userDao.insertUser(
            UserEntity(
                email = "priya.seeker@roomfinder.in",
                name = "Priya Verma",
                role = "SEEKER",
                phone = "+919876543210",
                password = "seeker123"
            )
        )

        if (roomDao.countListings() == 0) {
            roomDao.insertListings(indianSampleRooms)
        } else {
            // Automatically migrate any legacy/foreign listings to realistic Indian listings
            val existing = roomDao.getAllListingsSnapshot()
            val sampleMap = indianSampleRooms.associateBy { it.id }

            for (room in existing) {
                // If it's one of our sample IDs or still has foreign city / low prices
                if (sampleMap.containsKey(room.id)) {
                    val indianRoom = sampleMap[room.id]!!
                    roomDao.updateListing(indianRoom)
                } else if (room.city in listOf("Austin", "Seattle", "Chicago", "San Francisco", "Denver", "New York")) {
                    // Replace legacy foreign city
                    val fallback = indianSampleRooms.first()
                    roomDao.updateListing(
                        room.copy(
                            city = fallback.city,
                            area = fallback.area,
                            fullAddress = fallback.fullAddress,
                            monthlyRent = fallback.monthlyRent,
                            securityDeposit = fallback.securityDeposit
                        )
                    )
                }
            }

            // Also ensure the newer 107 and 108 sample rooms are added if not present
            for (newRoom in listOf(indianSampleRooms[6], indianSampleRooms[7])) {
                if (existing.none { it.id == newRoom.id }) {
                    roomDao.insertListing(newRoom)
                }
            }
        }
    }
}
