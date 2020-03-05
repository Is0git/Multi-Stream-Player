package com.android.multistreamplayer

import android.content.Context
import android.util.AttributeSet
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView

class MultiStreamPlayer(val context: Context, val playerType: Int)  {
    companion object {
        const val LIVE_STREAM: Int = 1
        const val VIDEO: Int = 2
    }

    var trackSelector = DefaultTrackSelector()
    var controller: MultiStreamController? = null
    var player: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)

    init {
        controller = if (playerType == LIVE_STREAM) LiveStreamController(context, player) else VideoController(context, player)
    }

    fun play(uri: String) {
        controller?.play(uri)
    }

    fun setTrackSelectorParams(params: DefaultTrackSelector.Parameters) {
        trackSelector.parameters = params
    }
}