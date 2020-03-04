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



    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        if (playbackState == STATE_READY) {
//            for(a in 0 until player.currentTrackSelections.length) {
//                for (b in 0 until player.currentTrackSelections[a]?.length!!) {
//                    Log.d("PALYER", "TRACKS: ${player.currentTrackSelections[a]?.getFormat(b)?.frameRate}")
//                }
//            }

                    player.currentTrackSelections
        }
    }
}

