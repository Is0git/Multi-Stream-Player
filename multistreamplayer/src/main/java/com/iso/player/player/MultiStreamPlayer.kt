package com.iso.player.player

import android.content.Context
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.iso.chat.chat.Chat
import com.iso.player.chat.chat_factories.PlayerData
import com.iso.player.player.controller.LiveStreamController
import com.iso.player.player.controller.MultiStreamController
import com.iso.player.player.controller.VideoController
import com.iso.player.playlist_source_creators.UrlCreator
import kotlinx.coroutines.*

class MultiStreamPlayer(val context: Context, playerType: Int) {
    companion object {
        const val LIVE_STREAM: Int = 1
        const val VIDEO: Int = 2
    }

    var playerType = LIVE_STREAM
    var chat: Chat? = null
    var trackSelector = DefaultTrackSelector()
    var controller: MultiStreamController? = null
    var player: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
    var urlCreator: UrlCreator<Any?, Any>? = null
    var playerStartJob: Job? = null
    var playerData: PlayerData? = null

    init {
        controller = if (playerType == LIVE_STREAM) LiveStreamController(
            context,
            player
        ) else VideoController(
            context,
            player
        )
    }

    fun play(channelKey: Any) {
        CoroutineScope(Dispatchers.Default).launch {
            urlCreator?.getDataFromUrl(channelKey)
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