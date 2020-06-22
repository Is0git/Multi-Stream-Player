package com.iso.player.player.controller

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.iso.player.listeners.ResourceListener

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

    fun stop() {
        player.stop()
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

    override fun onPlayerError(error: ExoPlaybackException?) {
        listeners.forEach { it.onFailed(error) }
        super.onPlayerError(error)
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

    fun hasListeners() : Boolean {
        return listeners.isNotEmpty()
    }
}