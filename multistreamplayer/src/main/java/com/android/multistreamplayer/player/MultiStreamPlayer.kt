package com.android.multistreamplayer.player

import android.content.Context
import com.android.multistreamchat.chat.Chat
import com.android.multistreamplayer.VideoController
import com.android.multistreamplayer.media_source.MediaSource
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import kotlinx.coroutines.*

class MultiStreamPlayer(val context: Context, playerType: Int) {
    companion object {
        const val LIVE_STREAM: Int = 1
        const val VIDEO: Int = 2
    }

    var chat: Chat? = null
    var trackSelector = DefaultTrackSelector()
    var controller: MultiStreamController? = null
    var player: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
    var mediaSource: MediaSource<in Any>? = null
    var playerStartJob: Job? = null

    init {
        controller = if (playerType == LIVE_STREAM) LiveStreamController(
            context,
            player
        ) else VideoController(context, player)
    }

    fun play(channelKey: Any) {
        CoroutineScope(Dispatchers.Default).launch {
            mediaSource?.getLiveStreamDataUrl(channelKey)
                .also { if (it != null) withContext(Dispatchers.Main) { controller?.play(it.toString()) } }
        }

    }

    fun buildTracksParams(track: TrackSelection?, position: Int) {
        track?.getFormat(position)?.apply {
            DefaultTrackSelector.ParametersBuilder()
                .setMaxVideoSize(this.width, this.height)
                .build().also { setTrackSelectorParams(it) }
        }
    }

    private fun setTrackSelectorParams(params: DefaultTrackSelector.Parameters) {
        trackSelector.parameters = params
    }

    fun clear() {
        playerStartJob?.cancel().also { playerStartJob = null }
    }
}