package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.RoomFinderDatabase
import com.example.data.RoomListingEntity
import com.example.data.RoomRepository
import com.example.data.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class RoomFinderViewModel(application: Application) : AndroidViewModel(application) {

    private val db = RoomFinderDatabase.getDatabase(application)
    private val repository = RoomRepository(db.roomListingDao(), db.userDao())
    
    // Firebase instances
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Current logged-in user state
    val currentUser = MutableStateFlow<UserEntity?>(null)

    val allListings: StateFlow<List<RoomListingEntity>> = repository.allListings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and Filters
    val searchQuery = MutableStateFlow("")
    val minRent = MutableStateFlow<Float?>(null)
    val maxRent = MutableStateFlow<Float?>(null)
    val selectedRoomType = MutableStateFlow("All")
    val selectedFurnishing = MutableStateFlow("All")
    val onlyAvailableNow = MutableStateFlow(false)

    // Selected listing for Details Screen
    val selectedListingId = MutableStateFlow<String?>("room-101")

    // Room being edited
    val editingRoom = MutableStateFlow<RoomListingEntity?>(null)

    // Feedback message
    val snackbarMessage = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            repository.seedSampleDataIfEmpty()
        }
        // Check if a Firebase user is already logged in
        checkCurrentFirebaseUser()
    }

    private fun checkCurrentFirebaseUser() {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null && firebaseUser.email != null) {
            viewModelScope.launch {
                val email = firebaseUser.email!!
                val existingUser = repository.findUser(email)
                if (existingUser != null) {
                    currentUser.value = existingUser
                } else {
                    // Fallback profile if not in local DB
                    currentUser.value = UserEntity(
                        email = email,
                        name = firebaseUser.displayName ?: "User",
                        role = "SEEKER",
                        phone = firebaseUser.phoneNumber ?: "",
                        password = ""
                    )
                }
            }
        }
    }

    // Favorites IDs for current user reactively
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val favoriteIds: StateFlow<Set<String>> = currentUser.flatMapLatest { user ->
        if (user == null || user.email.isBlank()) {
            flowOf(emptySet())
        } else {
            repository.getFavoriteIds(user.email).map { it.toSet() }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // Filtered listings based on search query, price, room type, furnishing, availability
    val filteredListings: StateFlow<List<RoomListingEntity>> = combine(
        allListings,
        searchQuery,
        minRent,
        maxRent,
        combine(selectedRoomType, selectedFurnishing, onlyAvailableNow) { type, furn, avail ->
            Triple(type, furn, avail)
        }
    ) { listings, query, minP, maxP, tripleFilters ->
        val (typeFilter, furnFilter, availFilter) = tripleFilters
        listings.filter { room ->
            val matchesQuery = query.isBlank() ||
                    room.city.contains(query, ignoreCase = true) ||
                    room.area.contains(query, ignoreCase = true) ||
                    room.title.contains(query, ignoreCase = true) ||
                    room.fullAddress.contains(query, ignoreCase = true)

            val matchesMin = minP == null || room.monthlyRent >= minP
            val matchesMax = maxP == null || room.monthlyRent <= maxP
            val matchesType = typeFilter == "All" || room.roomType.equals(typeFilter, ignoreCase = true)
            val matchesFurn = furnFilter == "All" || room.furnishingStatus.equals(furnFilter, ignoreCase = true)
            val matchesAvail = !availFilter || (room.isAvailableNow && !room.isRented)

            matchesQuery && matchesMin && matchesMax && matchesType && matchesFurn && matchesAvail
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedListing: StateFlow<RoomListingEntity?> = combine(
        allListings,
        selectedListingId
    ) { listings, id ->
        listings.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val ownerRooms: StateFlow<List<RoomListingEntity>> = combine(
        allListings,
        currentUser
    ) { listings, user ->
        if (user == null) emptyList()
        else listings.filter { it.ownerEmail.equals(user.email, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteListings: StateFlow<List<RoomListingEntity>> = combine(
        allListings,
        favoriteIds
    ) { listings, favs ->
        listings.filter { favs.contains(it.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) { searchQuery.value = query }
    fun setMinRent(value: Float?) { minRent.value = value }
    fun setMaxRent(value: Float?) { maxRent.value = value }
    fun setRoomType(type: String) { selectedRoomType.value = type }
    fun setFurnishing(furnishing: String) { selectedFurnishing.value = furnishing }
    fun setOnlyAvailableNow(enabled: Boolean) { onlyAvailableNow.value = enabled }

    fun resetFilters() {
        searchQuery.value = ""
        minRent.value = null
        maxRent.value = null
        selectedRoomType.value = "All"
        selectedFurnishing.value = "All"
        onlyAvailableNow.value = false
    }

    fun selectListing(id: String?) { selectedListingId.value = id }

    fun toggleFavorite(listingId: String) {
        val user = currentUser.value
        if (user == null) {
            snackbarMessage.value = "Please sign in to save favorites"
            return
        }
        viewModelScope.launch {
            val isFav = favoriteIds.value.contains(listingId)
            repository.toggleFavorite(listingId, user.email, isFav)
            snackbarMessage.value = if (isFav) "Removed from favorites" else "Saved to favorites!"
        }
    }

    fun toggleRentedStatus(id: String, currentRented: Boolean) {
        viewModelScope.launch {
            repository.setRentedStatus(id, !currentRented)
            snackbarMessage.value = if (currentRented) "Marked as Available" else "Marked as Rented"
        }
    }

    fun deleteRoom(id: String) {
        viewModelScope.launch {
            repository.deleteListing(id)
            firestore.collection("rooms").document(id).delete().await()
            snackbarMessage.value = "Room listing deleted"
        }
    }

    fun setEditingRoom(room: RoomListingEntity?) { editingRoom.value = room }

    fun publishRoom(
        title: String,
        city: String,
        area: String,
        fullAddress: String,
        monthlyRent: Double,
        securityDeposit: Double,
        roomType: String,
        furnishingStatus: String,
        availableDate: String,
        facilities: String,
        description: String,
        ownerName: String,
        ownerPhone: String,
        imageUrl: String,
        existingId: String? = null
    ): Boolean {
        val user = currentUser.value
        if (user == null) {
            snackbarMessage.value = "Please log in as a Room Owner to publish a listing"
            return false
        }

        if (title.isBlank() || city.isBlank() || monthlyRent <= 0.0) {
            snackbarMessage.value = "Please fill in all required room details"
            return false
        }

        viewModelScope.launch {
            val isAvailNow = availableDate.contains("Now", ignoreCase = true)
            val fallbackImg = if (imageUrl.isNotBlank()) imageUrl
            else "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?auto=format&fit=crop&w=1000&q=80"

            val roomId = existingId ?: "room-${UUID.randomUUID().toString().take(8)}"
            val listing = RoomListingEntity(
                id = roomId,
                title = title.trim(),
                city = city.trim(),
                area = area.trim(),
                fullAddress = fullAddress.trim(),
                monthlyRent = monthlyRent,
                securityDeposit = securityDeposit,
                roomType = roomType,
                furnishingStatus = furnishingStatus,
                availableDate = if (availableDate.isBlank()) "Available Now" else availableDate.trim(),
                isAvailableNow = isAvailNow,
                isRented = false,
                facilities = facilities,
                description = description.trim(),
                ownerName = ownerName.ifBlank { user.name },
                ownerPhone = ownerPhone.ifBlank { user.phone },
                ownerEmail = user.email,
                imageUrl = fallbackImg,
                createdAt = System.currentTimeMillis()
            )

            // Save locally and sync to Cloud Firestore
            if (existingId != null) {
                repository.updateListing(listing)
                snackbarMessage.value = "Room updated successfully!"
            } else {
                repository.insertListing(listing)
                snackbarMessage.value = "Room published successfully!"
            }
            
            // Push to Firestore Cloud Database
            firestore.collection("rooms").document(roomId).set(listing).await()
            editingRoom.value = null
        }
        return true
    }

    // Real Firebase Authentication Login
    fun loginWithCredentials(
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val cleanEmail = email.trim()
        val cleanPassword = password.trim()

        if (cleanEmail.isBlank() || cleanPassword.isBlank()) {
            onResult(false, "Please enter email and password")
            return
        }

        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(cleanEmail, cleanPassword).await()
                val existing = repository.findUser(cleanEmail)
                val user = existing ?: UserEntity(
                    email = cleanEmail,
                    name = cleanEmail.substringBefore("@"),
                    role = "SEEKER",
                    phone = "",
                    password = ""
                )
                currentUser.value = user
                repository.registerOrUpdateUser(user)
                snackbarMessage.value = "Welcome back, ${user.name}!"
                onResult(true, "Login successful")
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Authentication failed")
            }
        }
    }

    // Real Firebase Authentication Registration
    fun registerWithCredentials(
        name: String,
        phone: String,
        email: String,
        password: String,
        role: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val cleanName = name.trim()
        val cleanPhone = phone.trim()
        val cleanEmail = email.trim()
        val cleanPassword = password.trim()
        val cleanRole = if (role.equals("OWNER", ignoreCase = true)) "OWNER" else "SEEKER"

        if (cleanName.isBlank() || cleanEmail.isBlank() || cleanPassword.length < 4) {
            onResult(false, "Please fill valid registration details")
            return
        }

        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(cleanEmail, cleanPassword).await()
                val newUser = UserEntity(
                    email = cleanEmail,
                    name = cleanName,
                    role = cleanRole,
                    phone = cleanPhone,
                    password = cleanPassword
                )
                repository.registerOrUpdateUser(newUser)
                currentUser.value = newUser
                
                // Store user profile in Firestore
                firestore.collection("users").document(cleanEmail).set(newUser).await()
                
                snackbarMessage.value = "Account created! Welcome, $cleanName"
                onResult(true, "Registration successful")
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Registration failed")
            }
        }
    }

    fun quickLogin(email: String, name: String, role: String, phone: String, password: String = "") {
        val user = UserEntity(
            email = email.trim(),
            name = name.ifBlank { "User" }.trim(),
            role = role,
            phone = phone.trim(),
            password = password.ifBlank { "pass123" }
        )
        currentUser.value = user
        viewModelScope.launch {
            repository.registerOrUpdateUser(user)
        }
        snackbarMessage.value = "Logged in as $name"
    }

    fun switchRole(newRole: String) {
        val user = currentUser.value ?: return
        val updated = user.copy(role = newRole)
        currentUser.value = updated
        viewModelScope.launch {
            repository.registerOrUpdateUser(updated)
        }
        snackbarMessage.value = "Switched role successfully"
    }

    fun login(email: String, name: String, role: String, phone: String) {
        quickLogin(email, name, role, phone)
    }

    fun logout() {
        auth.signOut()
        currentUser.value = null
        snackbarMessage.value = "Logged out successfully"
    }

    fun clearSnackbar() {
        snackbarMessage.value = null
    }
}