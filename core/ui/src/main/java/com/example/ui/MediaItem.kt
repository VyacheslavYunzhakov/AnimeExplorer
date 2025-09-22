package com.example.ui

import android.graphics.Bitmap
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

@Composable
fun MediaItem(
    media: MediaUiModel,
    isVisible: Boolean,
    isOnline: Boolean,
    onMediaClick: (Int) -> Unit,
    onImageLoaded: () -> Unit,
    viewModel: ImagesViewModel
) {
    val brush = shimmerBrush()
    val density = LocalDensity.current

    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var blurredBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val originalImageBitmap = remember(originalBitmap) { originalBitmap?.asImageBitmap() }
    val blurredImageBitmap = remember(blurredBitmap) { blurredBitmap?.asImageBitmap() }

    val thresholdPx = with(density) { 100.dp.toPx() }

    var itemTopY by remember { mutableFloatStateOf(0f) }
    var itemHeightPx by remember { mutableFloatStateOf(0f) }

    val cacheKey = media.coverImage ?: media.id.toString()
    LaunchedEffect(cacheKey, isVisible, isOnline) {
        val cachedOrig = viewModel.getBitmap("${cacheKey}_orig")
        val cachedBlur = viewModel.getBitmap("${cacheKey}_blur")
        if (cachedOrig != null) {
            originalBitmap = cachedOrig
            blurredBitmap = cachedBlur ?: cachedOrig
            onImageLoaded()
        } else if (isVisible && isOnline) {
            viewModel.loadAndCache(media.coverImage!!) { orig, blur ->
                if (orig != null) { originalBitmap = orig; blurredBitmap = blur; onImageLoaded() }
            }
        }
    }

    val shouldDisplayImage = originalBitmap != null && (isVisible && isOnline)

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
            ) { onMediaClick(media.id) }
    ) {
        Card(
            shape = RoundedCornerShape(16),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation, RoundedCornerShape(16))
        ) {
            if (shouldDisplayImage && media.coverImage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f)
                        .onGloballyPositioned { coords ->
                            val pos = coords.positionInWindow()
                            itemTopY = pos.y
                            itemHeightPx = coords.size.height.toFloat()
                        }
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val canvasW = size.width
                        val canvasH = size.height
                        val splitY = (thresholdPx - itemTopY).coerceIn(-Float.MAX_VALUE, Float.MAX_VALUE)
                        val splitInCanvas = splitY.coerceIn(0f, canvasH)

                        val dstSize = IntSize(canvasW.toInt().coerceAtLeast(1), canvasH.toInt().coerceAtLeast(1))

                        if (blurredImageBitmap != null && splitInCanvas > 0f) {
                            clipRect(left = 0f, top = 0f, right = canvasW, bottom = splitInCanvas) {
                                drawImage(
                                    image = blurredImageBitmap,
                                    srcOffset = IntOffset.Zero,
                                    srcSize = IntSize(blurredImageBitmap.width, blurredImageBitmap.height),
                                    dstOffset = IntOffset.Zero,
                                    dstSize = dstSize
                                )
                            }
                        }

                        if (originalImageBitmap != null && splitInCanvas < canvasH) {
                            clipRect(left = 0f, top = splitInCanvas, right = canvasW, bottom = canvasH) {
                                drawImage(
                                    image = originalImageBitmap,
                                    srcOffset = IntOffset.Zero,
                                    srcSize = IntSize(originalImageBitmap.width, originalImageBitmap.height),
                                    dstOffset = IntOffset.Zero,
                                    dstSize = dstSize
                                )
                            }
                        }
                    }
                    if (originalBitmap == null && blurredBitmap == null) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(brush)
                        )
                    }
                }
            } else {
                // placeholder shimmer
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