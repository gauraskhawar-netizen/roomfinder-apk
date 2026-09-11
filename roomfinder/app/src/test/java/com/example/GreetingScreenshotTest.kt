package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.RoomListingEntity
import com.example.ui.components.RoomCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun roomfinder_screenshot() {
    val sampleRoom = RoomListingEntity(
        id = "test-1",
        title = "Modern Studio Apartment",
        city = "Austin",
        area = "Downtown",
        fullAddress = "123 Main St",
        monthlyRent = 800.0,
        securityDeposit = 500.0,
        roomType = "Studio Apartment",
        furnishingStatus = "Furnished",
        availableDate = "Available Now",
        isAvailableNow = true,
        isRented = false,
        facilities = "Wi-Fi,Parking,Attached Bathroom",
        description = "A lovely studio in downtown.",
        ownerName = "Marcus Vance",
        ownerPhone = "+15125550192",
        ownerEmail = "marcus@roomfinder.com",
        imageUrl = ""
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        RoomCard(
            room = sampleRoom,
            isFavorite = true,
            onFavoriteClick = {},
            onDetailsClick = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}

