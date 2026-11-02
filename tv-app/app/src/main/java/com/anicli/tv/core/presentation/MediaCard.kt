package com.anicli.tv.core.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.anicli.tv.core.theme.KuroNamiColors
import com.anicli.tv.domain.model.MediaItem

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MediaCard(
    item: MediaItem,
    onClick: () -> Unit,
    onFocus: () -> Unit = {},
    modifier: Modifier = Modifier.width(190.dp)
) {
    var isFocused by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        colors = CardDefaults.colors(
            containerColor = KuroNamiColors.SurfaceLow,
            focusedContainerColor = KuroNamiColors.SurfaceHigh
        ),
        shape = CardDefaults.shape(
            shape = RoundedCornerShape(12.dp)
        ),
        scale = CardDefaults.scale(
            focusedScale = 1.04f
        ),
        border = CardDefaults.border(
            border = Border(BorderStroke(1.dp, KuroNamiColors.Outline.copy(alpha = 0.25f))),
            focusedBorder = Border(BorderStroke(2.5.dp, KuroNamiColors.Crimson))
        ),
        modifier = modifier
            .padding(6.dp)
            .onFocusChanged { state ->
                isFocused = state.isFocused
                if (state.isFocused) {
                    onFocus()
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(KuroNamiColors.SurfaceLow)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                    .background(KuroNamiColors.SurfaceHigh)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.posterUrl.takeIf { it.isNotBlank() } ?: item.backdropUrl)
                        .size(240, 360)
                        .precision(coil.size.Precision.INEXACT)
                        .crossfade(false)
                        .allowRgb565(true)
                        .build(),
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Bottom vignette gradient over image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, KuroNamiColors.SurfaceLow)
                            )
                        )
                )

                // Rating pill badge with gold star
                if (!item.rating.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.78f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .border(
                                width = 0.8.dp,
                                color = KuroNamiColors.Gold.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "★",
                                style = MaterialTheme.typography.labelSmall,
                                color = KuroNamiColors.Gold,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 3.dp)
                            )
                            Text(
                                text = item.rating.replace("★", "").trim(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Progress Bar if watched
                if (item.progressPercent != null && item.progressPercent > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .align(Alignment.BottomStart)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = item.progressPercent.coerceIn(0.01f, 1f))
                                .fillMaxHeight()
                                .background(KuroNamiColors.Crimson)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isFocused) Color.White else KuroNamiColors.OnSurface,
                    fontWeight = if (isFocused) FontWeight.Bold else FontWeight.SemiBold,
                    maxLines = 2,
                    lineHeight = 18.sp,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.heightIn(min = 38.dp)
                )

                val meta = listOfNotNull(item.releaseYear, item.tags.firstOrNull()).joinToString(" • ")
                if (meta.isNotBlank()) {
                    Text(
                        text = meta,
                        style = MaterialTheme.typography.labelSmall,
                        color = KuroNamiColors.OnSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            }
        }
    }
}

/**
 * Dedicated Widescreen 16:9 Card for Continue Watching
 * Displays episode thumbnail/backdrop, episode badge, play indicator on focus,
 * and a prominent crimson progress bar.
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ContinueWatchingCard(
    item: MediaItem,
    onClick: () -> Unit,
    onFocus: () -> Unit = {},
    onRemove: (() -> Unit)? = null,
    modifier: Modifier = Modifier.width(280.dp)
) {
    var isFocused by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        onLongClick = onRemove,
        colors = CardDefaults.colors(
            containerColor = KuroNamiColors.SurfaceLow,
            focusedContainerColor = KuroNamiColors.SurfaceHigh
        ),
        shape = CardDefaults.shape(
            shape = RoundedCornerShape(14.dp)
        ),
        scale = CardDefaults.scale(
            focusedScale = 1.05f
        ),
        border = CardDefaults.border(
            border = Border(BorderStroke(1.dp, KuroNamiColors.Outline.copy(alpha = 0.25f))),
            focusedBorder = Border(
                BorderStroke(
                    2.5.dp,
                    Brush.horizontalGradient(
                        listOf(KuroNamiColors.Crimson, KuroNamiColors.Cyan)
                    )
                )
            )
        ),
        modifier = modifier
            .padding(6.dp)
            .onFocusChanged { state ->
                isFocused = state.isFocused
                if (state.isFocused) {
                    onFocus()
                }
            }
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key.nativeKeyCode) {
                        android.view.KeyEvent.KEYCODE_DPAD_CENTER,
                        android.view.KeyEvent.KEYCODE_ENTER,
                        android.view.KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                            onClick()
                            return@onKeyEvent true
                        }
                        android.view.KeyEvent.KEYCODE_MENU,
                        android.view.KeyEvent.KEYCODE_DEL,
                        android.view.KeyEvent.KEYCODE_FORWARD_DEL -> {
                            if (onRemove != null) {
                                onRemove()
                                return@onKeyEvent true
                            }
                        }
                    }
                }
                false
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(KuroNamiColors.SurfaceLow)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9.2f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(KuroNamiColors.SurfaceHigh)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.backdropUrl.takeIf { !it.isNullOrBlank() } ?: item.posterUrl)
                        .size(400, 230)
                        .precision(coil.size.Precision.INEXACT)
                        .crossfade(false)
                        .allowRgb565(true)
                        .build(),
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Cinematic vignette & darkening gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.35f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.5f),
                                    Color.Black.copy(alpha = 0.92f)
                                )
                            )
                        )
                )

                // Episode Badge Top Left
                val episodeLabel = item.episodeLabel ?: "EP 1"
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .background(
                            color = KuroNamiColors.CyanContainer.copy(alpha = 0.95f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = KuroNamiColors.Cyan.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(KuroNamiColors.Cyan)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = episodeLabel.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = KuroNamiColors.Cyan,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Top Right: Progress percentage pill or Remove Hint on Focus
                val percentText = ((item.progressPercent ?: 0.5f) * 100).toInt().coerceIn(1, 99)
                if (isFocused && onRemove != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .background(
                                color = KuroNamiColors.Crimson.copy(alpha = 0.95f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = KuroNamiColors.Crimson,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Remove",
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "HOLD OK TO REMOVE",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.75f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .border(
                                width = 0.8.dp,
                                color = KuroNamiColors.Outline.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "$percentText% WATCHED",
                            style = MaterialTheme.typography.labelSmall,
                            color = KuroNamiColors.CyanSoft,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Glowing Play Indicator Center on Focus
                if (isFocused) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(KuroNamiColors.Crimson)
                            .border(2.dp, KuroNamiColors.Cyan.copy(alpha = 0.9f), CircleShape)
                            .align(Alignment.Center),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.PlayArrow,
                            contentDescription = "Resume",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Sleek Neon Progress Bar along bottom of thumbnail
                val progress = item.progressPercent ?: 0.5f
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .align(Alignment.BottomStart)
                        .background(Color.Black.copy(alpha = 0.75f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = progress.coerceIn(0.03f, 1f))
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(KuroNamiColors.Crimson, KuroNamiColors.CyanSoft)
                                )
                            )
                    )
                }
            }

            // Title & Subtitle Below Image
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isFocused) Color.White else KuroNamiColors.OnSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                if (isFocused) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "▶ View Details & Resume",
                            style = MaterialTheme.typography.labelSmall,
                            color = KuroNamiColors.Cyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• Hold OK to remove",
                            style = MaterialTheme.typography.labelSmall,
                            color = KuroNamiColors.OnSurfaceVariant
                        )
                    }
                } else {
                    val percent = ((item.progressPercent ?: 0.5f) * 100).toInt().coerceIn(1, 99)
                    Text(
                        text = "$percent% complete • Press OK to view & resume",
                        style = MaterialTheme.typography.labelSmall,
                        color = KuroNamiColors.OnSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
