package com.example.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
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
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.SuccessResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MediaItem(
    media: MediaUiModel,
    isVisible: Boolean,
    isOnline: Boolean,
    onMediaClick: (Int) -> Unit,
    hasLoadedBefore: Boolean,
    onImageLoaded: (Bitmap) -> Unit
) {
    val brush = shimmerBrush()
    val context = LocalContext.current
    val density = LocalDensity.current

    // Локальные состояния для битмапов
    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var blurredBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // ImageBitmap для Canvas (кэшируем)
    val originalImageBitmap = remember(originalBitmap) { originalBitmap?.asImageBitmap() }
    val blurredImageBitmap = remember(blurredBitmap) { blurredBitmap?.asImageBitmap() }

    // Порог 100.dp в px
    val thresholdPx = with(density) { 100.dp.toPx() }

    // Позиция элемента в окне (px)
    var itemTopY by remember { mutableStateOf(0f) }
    var itemHeightPx by remember { mutableStateOf(0f) }

    // -----------------------
    // Загрузка изображения (выполняется вручную через ImageLoader.execute)
    // Запускаем загрузку когда есть URL и когда:
    //  - пользователь видим и онлайн (isVisible && isOnline), или
    //  - hasLoadedBefore (например, заранее разрешено)
    // -----------------------
    LaunchedEffect(media.coverImage, isVisible, isOnline, hasLoadedBefore) {
        val url = media.coverImage
        if (url.isNullOrEmpty()) return@LaunchedEffect

        // если уже загружено — не загружаем снова
        if (originalBitmap != null && blurredBitmap != null) return@LaunchedEffect

        // условие, по которому стартуем загрузку
        if (hasLoadedBefore || (isVisible && isOnline)) {
            try {
                val loader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .allowHardware(false)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build()

                // Выполняем запрос в IO, затем блюрим в Default
                val result = withContext(Dispatchers.IO) { loader.execute(request) }
                if (result is SuccessResult) {
                    val drawable = result.drawable
                    val original = drawable.toBitmap() // преобразуем в Bitmap
                    val blurred = withContext(Dispatchers.Default) { blurBitmap(context, original, 20f) }

                    originalBitmap = original
                    blurredBitmap = blurred

                    // внешний коллбэк (как у тебя был)
                    onImageLoaded(blurred)
                }
            } catch (e: Exception) {
                // можно логировать ошибку, пока просто игнорируем
                Log.w("MediaItem", "image load failed: ${e.message}")
            }
        }
    }

    // Логика, показывать ли (или placeholder)
    val shouldDisplayImage = originalBitmap != null || hasLoadedBefore || (isVisible && isOnline)

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
                    // Canvas рисует две версии и режет по порогу
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val canvasW = size.width
                        val canvasH = size.height

                        // Позиция порога внутри канвы
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

                        // Если битмапы ещё не готовы — оставляем пустоту (placeholder под ним)
                    }

                    // Если ничего ещё нет — показываем shimmer поверх канвы (чтобы не было пустоты)
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