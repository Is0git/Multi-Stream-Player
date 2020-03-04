package com.android.multistreamplayer

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

class LiveStreamController(ctx: Context, player: SimpleExoPlayer) : MultiStreamController(ctx, player) {
    override fun onMediaSourceBuilder(
        dataSourceFactory: DefaultDataSourceFactory,
        url: Uri
    ): MediaSource = HlsMediaSource.Factory(dataSourceFactory).createMediaSource(url)
}

