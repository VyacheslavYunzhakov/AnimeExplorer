package com.example.mediainfo

import android.app.Activity
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material3.CircularProgressIndicator
import android.text.Spanned
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat


@Composable
fun MediaInfoScreen(
    onBackClick: () -> Unit,
    viewModel: MediaInfoViewModel = hiltViewModel()
) {
    val mediaState = viewModel.mediaState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.getMediaInfo() }

    val errorMessage = mediaState.value.error
    val mediaInfo = mediaState.value.mediaInfo

    val bannerHeight = 160.dp
    val bannerGradientHeight = 120.dp

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(bannerHeight)
        ) {
            AsyncImage(
                model = mediaInfo?.bannerImage,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(), // <- важно: занимает весь Box
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bannerGradientHeight)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )
        }

        Column(
            modifier = Modifier
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Компактный coverImage (160dp)
            mediaInfo?.coverImage?.let {
                AsyncImage(
                    model = it,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .height(240.dp)
                        .aspectRatio(2f / 3f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                mediaState.value.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                errorMessage != null -> {
                    Text(text = errorMessage)
                }

                mediaInfo != null -> {
                    var expanded by remember { mutableStateOf(false) }
                    val descriptionHtml = mediaInfo.description.orEmpty()

                    AndroidView(
                        factory = { context ->
                            TextView(context).apply {
                                movementMethod = LinkMovementMethod.getInstance()
                                ellipsize = TextUtils.TruncateAt.END
                                setTextAppearance(android.R.style.TextAppearance_Material_Body1)
                            }
                        },
                        update = { tv ->
                            val sp: Spanned = HtmlCompat.fromHtml(descriptionHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)
                            tv.text = sp
                            tv.maxLines = if (expanded) Int.MAX_VALUE else 5
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        val rotation by animateFloatAsState(if (expanded) 180f else 0f)
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowDropDown,
                            contentDescription = if (expanded) "Collapse" else "Expand",
                            modifier = Modifier.graphicsLayer(rotationZ = rotation)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
