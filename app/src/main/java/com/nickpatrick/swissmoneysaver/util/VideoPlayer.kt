package com.nickpatrick.swissmoneysaver.util

import android.content.Context
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.nickpatrick.swissmoneysaver.R
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.RawResourceDataSource

@Composable
fun VideoPlayerComponent(
    player: SimpleExoPlayer,
    context: Context,
    playVideo: Boolean,
    onTogglePlayback: (Boolean) -> Unit,
    onStopPlayback: () -> Unit,
) {
    val videoRawResId = R.raw.mm_intro // Replace with your video file

    val videoSource = remember {
        MediaItem.Builder()
            .setUri(RawResourceDataSource.buildRawResourceUri(videoRawResId))
            .build()
    }

    // Handle video playback when playVideo state changes
    LaunchedEffect(playVideo) {
        if (playVideo) {
            player.setMediaItem(videoSource)
            player.prepare()
            player.playWhenReady = true // Set to true to start playback
        } else {
            player.stop()
            player.clearMediaItems()
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    this.player = player
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    alpha = if (playVideo) 1f else 0f
                    isClickable = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}