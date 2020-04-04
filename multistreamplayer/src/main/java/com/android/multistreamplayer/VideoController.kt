package com.android.multistreamplayer

import android.content.Context
import android.net.Uri
import com.android.multistreamplayer.player.MultiStreamController
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

class VideoController(ctx: Context, player: SimpleExoPlayer) : MultiStreamController(ctx, player) {
    override fun onMediaSourceBuilder(
        dataSourceFactory: DefaultDataSourceFactory,
        url: Uri
    ): MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(url)
}

