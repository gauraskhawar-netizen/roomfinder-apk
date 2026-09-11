package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryLight

@Composable
fun RoomImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current

    if (imageUrl.isNullOrBlank()) {
        PlaceholderRoomImage(modifier = modifier)
    } else {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier,
            loading = {
                PlaceholderRoomImage(modifier = Modifier.fillMaxSize())
            },
            error = {
                PlaceholderRoomImage(modifier = Modifier.fillMaxSize())
            }
        )
    }
}

@Composable
fun PlaceholderRoomImage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        PrimaryBlue.copy(alpha = 0.85f),
                        PrimaryLight.copy(alpha = 0.7f),
                        Color(0xFF0284C7).copy(alpha = 0.75f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Bed,
            contentDescription = "Room Placeholder",
            modifier = Modifier.size(48.dp),
            tint = Color.White.copy(alpha = 0.9f)
        )
    }
}
