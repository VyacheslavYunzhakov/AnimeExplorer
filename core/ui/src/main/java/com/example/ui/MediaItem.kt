package com.example.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.shadow

@Composable
fun MediaItem(
    media: MediaUiModel,
    isVisible: Boolean,
    isOnline: Boolean,
    onMediaClick: (Int) -> Unit,
    hasLoadedBefore: Boolean,
    onImageLoaded: () -> Unit
) {
    val brush = shimmerBrush()

    val request = ImageRequest.Builder(LocalContext.current)
        .data(media.coverImage)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    val painter = rememberAsyncImagePainter(model = request)
    val painterState = painter.state

    LaunchedEffect(painterState) {
        if (painterState is AsyncImagePainter.State.Success) {
            onImageLoaded()
        }
    }

    val shouldDisplayImage = painterState is AsyncImagePainter.State.Success ||
            hasLoadedBefore ||
            (isVisible && isOnline)

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val elevation by animateDpAsState(if (isPressed) 0.dp else 20.dp)

    Column(
        modifier = Modifier
            .width(150.dp)
            .padding(16.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {onMediaClick(media.id)}
    ) {
        Card(
            shape = RoundedCornerShape(16),
            modifier = Modifier.fillMaxWidth()
                .shadow(elevation, RoundedCornerShape(16))

        ) {
            if (shouldDisplayImage && media.coverImage != null) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f)
                        .background(brush)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = media.title.orEmpty(),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2
        )
        Text(
            text = "Score: ${media.averageScore ?: "N/A"}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
