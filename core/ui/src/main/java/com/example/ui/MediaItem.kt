package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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

@Composable
fun MediaItem(
    media: MediaUiModel,
    isVisible: Boolean,
    isOnline: Boolean,
    onMediaClick: (Int) -> Unit
) {
    val brush = shimmerBrush()
    val context = LocalContext.current

    val request = ImageRequest.Builder(context)
        .data(media.coverImage)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    val painter = rememberAsyncImagePainter(request)
    val painterState = painter.state

    val shouldDisplayImage = painterState is AsyncImagePainter.State.Success ||
            (isVisible && isOnline)

    Column(
        modifier = Modifier
            .width(150.dp)
            .padding(16.dp)
            .clickable {
                onMediaClick(media.id)
            }
    ) {
        if (shouldDisplayImage && media.coverImage != null) {
            Image(
                painter = painter,
                contentDescription = null,
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

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = media.title.orEmpty(),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2, minLines = 2
        )
        Text(
            text = "Score: ${media.averageScore ?: "N/A"}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}