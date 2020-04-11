package com.android.multistreamplayer.player

import android.content.Context
import android.net.Uri
import com.android.multistreamplayer.listeners.ResourceListener
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

abstract class MultiStreamController(val ctx: Context, val player: SimpleExoPlayer) :
    Player.EventListener {

    var listener: Player.EventListener? = null
    val listeners: MutableList<ResourceListener> by lazy { mutableListOf<ResourceListener>() }
    init {
        addListener(this@MultiStreamController)
    }
    fun play(uri: String) {
        buildMediaSource(uri).also { player.prepare(it) }

    }

    private fun buildMediaSource(uri: String) =
        onMediaSourceBuilder(DefaultDataSourceFactory(ctx, "app_name"), Uri.parse(uri))


    abstract fun onMediaSourceBuilder(
        dataSourceFactory: DefaultDataSourceFactory,
        url: Uri
    ): MediaSource

     private fun addListener(listener: Player.EventListener) {
        this.listener = listener
        player.addListener(this.listener)
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)

        listeners.forEach {
            when(playbackState) {
                Player.STATE_READY -> it.onResourceTracksReady(player.currentTrackSelections)
                Player.STATE_BUFFERING -> it.onStateBuffering()
                Player.STATE_IDLE -> it.onStateIdle()
                Player.STATE_ENDED -> it.onStateFinish()
            }

        }

    }

    override fun onTracksChanged(
        trackGroups: TrackGroupArray?,
        trackSelections: TrackSelectionArray?
    ) {
        super.onTracksChanged(trackGroups, trackSelections)
        listeners.forEach {
            it.onTrackChanged(trackGroups, trackSelections)
        }
    }



    fun removeListener() {
        player.removeListener(listener)
        listener = null
    }

}