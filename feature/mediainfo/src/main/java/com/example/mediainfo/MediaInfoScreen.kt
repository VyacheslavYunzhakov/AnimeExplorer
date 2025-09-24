package com.example.mediainfo

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.shape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex


@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaInfoScreen(
    onBackClick: () -> Unit,
    mediaInfoViewModel: MediaInfoViewModel = hiltViewModel(),
    bannerColor: Color = MaterialTheme.colorScheme.primary
) {
    val mediaState = mediaInfoViewModel.mediaState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { mediaInfoViewModel.getMediaInfo() }
    val listState = rememberLazyListState()

    when {
        mediaState.value.mediaInfo != null -> {
            val media =  mediaState.value.mediaInfo!!
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        if (!media.bannerUrl.isNullOrBlank()) {
                            // Отображаем баннер
                            AsyncImage(
                                model = media.bannerUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize()
                            )
                        } else {
                            // Нет баннера — рисуем вертикальный градиент на основе цвета
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            // сверху более прозрачный, снизу — насыщенный цвет
                                            colors = listOf(
                                                bannerColor.copy(alpha = 0.15f),
                                                bannerColor.copy(alpha = 0.35f),
                                                bannerColor.copy(alpha = 0.9f)
                                            ),
                                            startY = 0f,
                                            endY = Float.POSITIVE_INFINITY
                                        )
                                    )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.background
                                        ),
                                        startY = 120f
                                    )
                                )
                        )

                        media.coverUrl?.let {
                            // CardDefaults для elevation и shape (Material3)
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(160.dp)
                                    .offset(x = 16.dp, y = 100.dp)
                                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant), shape = shape)
                                    .zIndex(2f)
                            ) {
                                AsyncImage(
                                    model = it,
                                    contentDescription = "cover",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .padding(12.dp)
                                .align(Alignment.TopStart)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 106.dp)) {
                        Text(
                            text = media.title,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        media.subTitle?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Rating
                            media.score?.let { score ->
                                ChipSimple(text = String.format("%.1f/10", score), leading = {
                                    Text("★", color = Color(0xFFFFD700))
                                })
                            }

                            media.year?.let {
                                ChipSimple(text = it.toString())
                            }

                            media.status?.let {
                                ChipSimple(text = it)
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Button(onClick = { /* add to list */ }) {
                                Text(text = "Add to List")
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        media.genres.take(8).forEach { g ->
                            ChipSimple(text = g)
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column {
                            Text("Episodes", style = MaterialTheme.typography.labelSmall); Text(
                            media.episodes?.toString() ?: "-",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        }
                        Column {
                            Text(
                                "Duration",
                                style = MaterialTheme.typography.labelSmall
                            ); Text(
                            "${media.durationMinutes ?: "-"} min",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        }
                        Column {
                            Text(
                                "Studio",
                                style = MaterialTheme.typography.labelSmall
                            ); Text(media.studio ?: "-", style = MaterialTheme.typography.bodyLarge)
                        }
                        Column {
                            Text(
                                "Status",
                                style = MaterialTheme.typography.labelSmall
                            ); Text(media.status ?: "-", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    CollapsibleHtmlText(
                        html = media.descriptionHtml ?: "",
                        maxLinesCollapsed = 5
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun ChipSimple(
    text: String,
    leading: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leading != null) {
            leading()
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(text = text, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun CollapsibleHtmlText(html: String, maxLinesCollapsed: Int = 5) {
    var expanded by remember { mutableStateOf(false) }
    val displayText: AnnotatedString = remember(html) {
        try {
            AnnotatedString.fromHtml(html)
        } catch (_: Throwable) {
            AnnotatedString(android.text.Html.fromHtml(html, android.text.Html.FROM_HTML_MODE_LEGACY).toString())
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .animateContentSize()
    ) {
        Text(
            text = displayText,
            maxLines = if (expanded) Int.MAX_VALUE else maxLinesCollapsed,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            val rotation by animateFloatAsState(if (expanded) 180f else 0f)
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
                modifier = Modifier
                    .size(32.dp)
                    .graphicsLayer(rotationZ = rotation)
                    .clickable { expanded = !expanded },
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}