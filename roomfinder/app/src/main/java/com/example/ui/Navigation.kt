package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.UserEntity
import com.example.ui.screens.AddRoomScreen
import com.example.ui.screens.AuthProfileScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MyRoomsScreen
import com.example.ui.screens.RoomDetailsScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SignUpScreen
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryDark

enum class NavDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    SEARCH("Search", Icons.Filled.Search, Icons.Outlined.Search, "nav_search"),
    ADD_ROOM("Add Room", Icons.Filled.AddCircle, Icons.Outlined.AddCircleOutline, "nav_add_room"),
    FAVORITES("Favorites", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder, "nav_favorites"),
    MY_ROOMS("My Rooms", Icons.Filled.HomeWork, Icons.Outlined.HomeWork, "nav_my_rooms"),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile"),
    LOGIN("Login", Icons.Filled.Login, Icons.Outlined.Login, "nav_login"),
    SIGN_UP("Sign Up", Icons.Filled.PersonAdd, Icons.Outlined.PersonAdd, "nav_signup")
}

@Composable
fun RoomFinderApp(
    viewModel: RoomFinderViewModel,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf(NavDestination.HOME) }
    var inDetailsScreen by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMsg by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    // Handle back button on Details screen
    BackHandler(enabled = inDetailsScreen) {
        inDetailsScreen = false
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 650.dp

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                if (isWideScreen && !inDetailsScreen) {
                    DesktopNavBar(
                        currentScreen = currentScreen,
                        onNavigate = { destination ->
                            currentScreen = destination
                            inDetailsScreen = false
                        },
                        favoritesCount = favoriteIds.size,
                        currentUser = currentUser
                    )
                }
            },
            bottomBar = {
                if (!isWideScreen && !inDetailsScreen) {
                    MobileBottomBar(
                        currentScreen = currentScreen,
                        onNavigate = { destination ->
                            currentScreen = destination
                            inDetailsScreen = false
                        },
                        favoritesCount = favoriteIds.size,
                        currentUser = currentUser
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (inDetailsScreen) {
                    RoomDetailsScreen(
                        viewModel = viewModel,
                        onBack = { inDetailsScreen = false }
                    )
                } else {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "ScreenTransition"
                    ) { screen ->
                        when (screen) {
                            NavDestination.HOME -> HomeScreen(
                                viewModel = viewModel,
                                onNavigateToDetails = { roomId ->
                                    viewModel.selectListing(roomId)
                                    inDetailsScreen = true
                                },
                                onNavigateToSearch = { currentScreen = NavDestination.SEARCH },
                                onNavigateToAddRoom = { currentScreen = NavDestination.ADD_ROOM }
                            )

                            NavDestination.SEARCH -> SearchScreen(
                                viewModel = viewModel,
                                onNavigateToDetails = { roomId ->
                                    viewModel.selectListing(roomId)
                                    inDetailsScreen = true
                                }
                            )

                            NavDestination.ADD_ROOM -> AddRoomScreen(
                                viewModel = viewModel,
                                onRoomPublished = {
                                    currentScreen = NavDestination.MY_ROOMS
                                }
                            )

                            NavDestination.FAVORITES -> FavoritesScreen(
                                viewModel = viewModel,
                                onNavigateToDetails = { roomId ->
                                    viewModel.selectListing(roomId)
                                    inDetailsScreen = true
                                },
                                onNavigateToHome = { currentScreen = NavDestination.HOME }
                            )

                            NavDestination.MY_ROOMS -> MyRoomsScreen(
                                viewModel = viewModel,
                                onNavigateToAddRoom = { currentScreen = NavDestination.ADD_ROOM },
                                onNavigateToDetails = { roomId ->
                                    viewModel.selectListing(roomId)
                                    inDetailsScreen = true
                                }
                            )

                            NavDestination.PROFILE -> AuthProfileScreen(
                                viewModel = viewModel,
                                onNavigateToLogin = { currentScreen = NavDestination.LOGIN },
                                onNavigateToSignUp = { currentScreen = NavDestination.SIGN_UP },
                                onNavigateToAddRoom = { currentScreen = NavDestination.ADD_ROOM },
                                onNavigateToMyRooms = { currentScreen = NavDestination.MY_ROOMS },
                                onNavigateToSearch = { currentScreen = NavDestination.SEARCH },
                                onNavigateToFavorites = { currentScreen = NavDestination.FAVORITES }
                            )

                            NavDestination.LOGIN -> LoginScreen(
                                viewModel = viewModel,
                                onNavigateToSignUp = { currentScreen = NavDestination.SIGN_UP },
                                onLoginSuccess = { currentScreen = NavDestination.PROFILE },
                                onNavigateToHome = { currentScreen = NavDestination.HOME }
                            )

                            NavDestination.SIGN_UP -> SignUpScreen(
                                viewModel = viewModel,
                                onNavigateToLogin = { currentScreen = NavDestination.LOGIN },
                                onSignUpSuccess = { currentScreen = NavDestination.PROFILE }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DesktopNavBar(
    currentScreen: NavDestination,
    onNavigate: (NavDestination) -> Unit,
    favoritesCount: Int,
    currentUser: UserEntity?
) {
    val destinations = when (currentUser?.role) {
        "OWNER" -> listOf(
            NavDestination.HOME,
            NavDestination.SEARCH,
            NavDestination.ADD_ROOM,
            NavDestination.MY_ROOMS,
            NavDestination.PROFILE
        )
        "SEEKER" -> listOf(
            NavDestination.HOME,
            NavDestination.SEARCH,
            NavDestination.FAVORITES,
            NavDestination.PROFILE
        )
        else -> listOf(
            NavDestination.HOME,
            NavDestination.SEARCH,
            NavDestination.LOGIN,
            NavDestination.PROFILE
        )
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onNavigate(NavDestination.HOME) }
                    .testTag("app_brand_logo")
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryBlue,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "RoomFinder",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryBlue
                )
            }

            // Navigation Links
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                destinations.forEach { destination ->
                    val isSelected = currentScreen == destination
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) PrimaryBlue.copy(alpha = 0.12f) else Color.Transparent,
                        modifier = Modifier
                            .clickable { onNavigate(destination) }
                            .testTag(destination.testTag)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = destination.title,
                                tint = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = destination.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MobileBottomBar(
    currentScreen: NavDestination,
    onNavigate: (NavDestination) -> Unit,
    favoritesCount: Int,
    currentUser: UserEntity?
) {
    val destinations = when (currentUser?.role) {
        "OWNER" -> listOf(
            NavDestination.HOME,
            NavDestination.ADD_ROOM,
            NavDestination.MY_ROOMS,
            NavDestination.PROFILE
        )
        "SEEKER" -> listOf(
            NavDestination.HOME,
            NavDestination.SEARCH,
            NavDestination.FAVORITES,
            NavDestination.PROFILE
        )
        else -> listOf(
            NavDestination.HOME,
            NavDestination.SEARCH,
            NavDestination.LOGIN,
            NavDestination.PROFILE
        )
    }

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        destinations.forEach { destination ->
            val isSelected = currentScreen == destination
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(destination) },
                icon = {
                    if (destination == NavDestination.FAVORITES && favoritesCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = Color(0xFFE11D48)) {
                                    Text(favoritesCount.toString())
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = destination.title
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                            contentDescription = destination.title
                        )
                    }
                },
                label = {
                    Text(
                        text = destination.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    indicatorColor = PrimaryBlue.copy(alpha = 0.15f)
                ),
                modifier = Modifier.testTag(destination.testTag)
            )
        }
    }
}
