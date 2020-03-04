package com.android.multistreamplayer

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

abstract class MultiStreamController(val ctx: Context, val player: SimpleExoPlayer) :
    Player.EventListener {

    var listener: Player.EventListener? = null

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

    fun addListener(listener: Player.EventListener) {
        this.listener = listener
        player.addListener(this.listener)
    }

    fun removeListener() {
        player.removeListener(listener)
        listener = null
    }

}