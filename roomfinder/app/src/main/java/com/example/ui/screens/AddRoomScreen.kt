package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.RoomFinderViewModel
import com.example.ui.components.RoomImage
import com.example.ui.theme.PrimaryBlue

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddRoomScreen(
    viewModel: RoomFinderViewModel,
    onRoomPublished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val editingRoom by viewModel.editingRoom.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var fullAddress by remember { mutableStateOf("") }
    var monthlyRent by remember { mutableStateOf("") }
    var securityDeposit by remember { mutableStateOf("") }
    var selectedRoomType by remember { mutableStateOf("Single Room") }
    var furnishingStatus by remember { mutableStateOf("Furnished") }
    var availableDate by remember { mutableStateOf("Available Now") }
    var description by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var ownerPhone by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    val selectedFacilities = remember {
        mutableStateListOf("Wi-Fi", "Water 24/7", "Electricity", "Attached Bathroom")
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUrl = uri.toString()
        }
    }

    // Populate when editing
    LaunchedEffect(editingRoom) {
        val r = editingRoom
        if (r != null) {
            title = r.title
            city = r.city
            area = r.area
            fullAddress = r.fullAddress
            monthlyRent = r.monthlyRent.toInt().toString()
            securityDeposit = r.securityDeposit.toInt().toString()
            selectedRoomType = r.roomType
            furnishingStatus = r.furnishingStatus
            availableDate = r.availableDate
            description = r.description
            ownerName = r.ownerName
            ownerPhone = r.ownerPhone
            imageUrl = r.imageUrl
            selectedFacilities.clear()
            selectedFacilities.addAll(r.getFacilityList())
        } else {
            ownerName = currentUser?.name ?: ""
            ownerPhone = currentUser?.phone ?: ""
        }
    }

    val availableFacilities = listOf(
        "Wi-Fi", "Water 24/7", "Electricity", "Parking",
        "Attached Bathroom", "Air Conditioning", "Kitchen Access",
        "Balcony", "Washing Machine", "Elevator"
    )

    val sampleImagePresets = listOf(
        "Modern Studio" to "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?auto=format&fit=crop&w=1000&q=80",
        "Cozy Bedroom" to "https://images.unsplash.com/photo-1595526114035-0d45ed16cfbf?auto=format&fit=crop&w=1000&q=80",
        "Sunny Master" to "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=1000&q=80",
        "Minimalist Room" to "https://images.unsplash.com/photo-1540518614846-7ede433c4550?auto=format&fit=crop&w=1000&q=80"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("add_room_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp)
    ) {
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (editingRoom != null) "Edit Room Listing" else "Post a Room for Rent",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Connect with verified seekers looking for rooms",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (editingRoom != null) {
                        OutlinedButton(onClick = { viewModel.setEditingRoom(null) }) {
                            Text("Cancel Edit")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Basic Details Section
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "1. Room Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )

                        // Title
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Room Title *") },
                            placeholder = { Text("e.g. Spacious Sunny Master Bedroom") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_room_title_input")
                        )

                        // City & Area
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("City *") },
                                placeholder = { Text("e.g. Gwalior") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("add_room_city_input")
                            )

                            OutlinedTextField(
                                value = area,
                                onValueChange = { area = it },
                                label = { Text("Area / Neighborhood *") },
                                placeholder = { Text("e.g. City Center") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("add_room_area_input")
                            )
                        }

                        // Full Address
                        OutlinedTextField(
                            value = fullAddress,
                            onValueChange = { fullAddress = it },
                            label = { Text("Full Street Address *") },
                            placeholder = { Text("e.g. 14, Gandhi Road, Near Phoolbagh") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_room_address_input")
                        )

                        // Rent & Deposit
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = monthlyRent,
                                onValueChange = { monthlyRent = it },
                                label = { Text("Monthly Rent (₹)") },
                                placeholder = { Text("2500") },
                                leadingIcon = {
                                    Icon(Icons.Default.CurrencyRupee, contentDescription = null, modifier = Modifier.size(18.dp))
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("add_room_rent_input")
                            )

                            OutlinedTextField(
                                value = securityDeposit,
                                onValueChange = { securityDeposit = it },
                                label = { Text("Security Deposit (₹)") },
                                placeholder = { Text("5000") },
                                leadingIcon = {
                                    Icon(Icons.Default.CurrencyRupee, contentDescription = null, modifier = Modifier.size(18.dp))
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("add_room_deposit_input")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Type & Furnishing Section
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "2. Room Specifications",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )

                        // Room Type
                        Text(text = "Room Type", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Single Room", "1BHK", "2BHK", "PG", "Double Room", "Studio Apartment", "Shared Room").forEach { type ->
                                FilterChip(
                                    selected = selectedRoomType == type,
                                    onClick = { selectedRoomType = type },
                                    label = { Text(type, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryBlue,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        // Furnishing
                        Text(text = "Furnishing Status", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Furnished", "Semi-Furnished", "Unfurnished").forEach { furn ->
                                FilterChip(
                                    selected = furnishingStatus == furn,
                                    onClick = { furnishingStatus = furn },
                                    label = { Text(furn, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryBlue,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        // Available Date
                        OutlinedTextField(
                            value = availableDate,
                            onValueChange = { availableDate = it },
                            label = { Text("Available Date") },
                            placeholder = { Text("e.g. Available Now, Oct 1, 2026") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_room_avail_date")
                        )

                        // Facilities Multi-select
                        Text(text = "Facilities & Amenities", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableFacilities.forEach { facility ->
                                val isSelected = selectedFacilities.contains(facility)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (isSelected) selectedFacilities.remove(facility)
                                        else selectedFacilities.add(facility)
                                    },
                                    leadingIcon = if (isSelected) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    } else null,
                                    label = { Text(facility, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryBlue,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        // Description
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Room Description") },
                            placeholder = { Text("Describe room features, sunlight, roommates, neighborhood, bills...") },
                            minLines = 3,
                            maxLines = 6,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_room_desc_input")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Photos & Owner Info
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "3. Photos & Contact Info",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )

                        // Photo Upload / Select
                        Text(
                            text = "Room Photos",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                modifier = Modifier.testTag("upload_photo_button")
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pick Room Photo")
                            }

                            if (imageUrl.isNotBlank()) {
                                OutlinedButton(onClick = { imageUrl = "" }) {
                                    Text("Clear Photo")
                                }
                            }
                        }

                        // Presets
                        Text(
                            text = "Or choose a sample room style:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            sampleImagePresets.forEach { (name, url) ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (imageUrl == url) PrimaryBlue else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .clickable { imageUrl = url }
                                        .padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = name,
                                        fontSize = 11.sp,
                                        color = if (imageUrl == url) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        // Image Preview if selected
                        if (imageUrl.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            ) {
                                RoomImage(
                                    imageUrl = imageUrl,
                                    contentDescription = "Selected Room Photo",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Owner Contact
                        OutlinedTextField(
                            value = ownerName,
                            onValueChange = { ownerName = it },
                            label = { Text("Owner Name *") },
                            placeholder = { Text("e.g. Rahul Sharma") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_room_owner_name")
                        )

                        OutlinedTextField(
                            value = ownerPhone,
                            onValueChange = { ownerPhone = it },
                            label = { Text("Phone Number (for Call & WhatsApp) *") },
                            placeholder = { Text("+919876543210") },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_room_owner_phone")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Publish Button
                Button(
                    onClick = {
                        val rentDouble = monthlyRent.toDoubleOrNull() ?: 0.0
                        val depositDouble = securityDeposit.toDoubleOrNull() ?: 0.0
                        val facilitiesStr = selectedFacilities.joinToString(",")

                        val success = viewModel.publishRoom(
                            title = title,
                            city = city,
                            area = area,
                            fullAddress = fullAddress,
                            monthlyRent = rentDouble,
                            securityDeposit = depositDouble,
                            roomType = selectedRoomType,
                            furnishingStatus = furnishingStatus,
                            availableDate = availableDate,
                            facilities = facilitiesStr,
                            description = description,
                            ownerName = ownerName,
                            ownerPhone = ownerPhone,
                            imageUrl = imageUrl,
                            existingId = editingRoom?.id
                        )
                        if (success) {
                            onRoomPublished()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("publish_room_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Icon(Icons.Default.Publish, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (editingRoom != null) "Update Room Listing" else "Publish Room",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
