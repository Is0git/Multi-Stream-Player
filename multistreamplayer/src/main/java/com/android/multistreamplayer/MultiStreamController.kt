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

    fun play(uri: String) {
        buildMediaSource(uri).also { player.prepare(it) }

    }

    private fun buildMediaSource(uri: String) =
        onMediaSourceBuilder(DefaultDataSourceFactory(ctx, "app_name"), Uri.parse("http://video-weaver.fra05.hls.ttvnw.net/v1/playlist/CuEDa5rmX61hsKivWWVtc7x9xX6o_nCCmfBAkt9RylRA4L4Mk2ym_hWtzhLtEYKiNUv3qrJXKXHIlkcpMKhKDjTcd5x38WdQsUAcYYG3fPj_9lo93gJl013KFQN0YG0QN1Pa4kH-pKAaMumwj5_Ukf1v371adFNnv5ZisUx3GdWqoPqsrvpyw4D2eXMOz871gMW4bkXH0n1OahObNIkPCRbFO1sY0q4j61TOLkyqW_73EZ66uPHWagXf0aJzikd_6wqKCtpXvla7piOmOI1LIg4hBZgnSX7HCNr4enal0NmT2-MKf4Jd5hvn1TI9ux89Ipb6oOgBs8M9pJNv5UgzpSLN-atzoIaPABiDQANhRAl-0OlMe7sRQ_oTadOMpX5PJ7TuFJNoXLggM_0HzsOgeOX8R828oQO6bRO1uqg4JWhgDl0MzVMoIwm_PQ82Jf04DRg7dCdJDR9C_Z8z24gRYvII9y2m0iib36PZ_NFy6jNLSR5vTCxAu8oW6RbdSmhdqKu9H-h1kPPZ573v_1DAe1NGmJuWaNoRBjwcVdH1QE2QB4TtBzVflM988SVbR9L1rEeFqk8Yd7g5a1s92MSAJ82XBRrVBG4kmb9aYgiWdF9tdHe9OU2KXCkn8jxfLVi6zTUxwRIQa0Ckk-mOjMPztUPt2LUHLhoM4QsXS7SoeCMn483K.m3u8"))


    abstract fun onMediaSourceBuilder(
        dataSourceFactory: DefaultDataSourceFactory,
        url: Uri
    ): MediaSource


    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

    }

    override fun onSeekProcessed() {

    }

    override fun onTracksChanged(
        trackGroups: TrackGroupArray?,
        trackSelections: TrackSelectionArray?
    ) {

    }

    override fun onPlayerError(error: ExoPlaybackException?) {

    }

    override fun onLoadingChanged(isLoading: Boolean) {

    }

    override fun onPositionDiscontinuity(reason: Int) {

    }

    override fun onRepeatModeChanged(repeatMode: Int) {

    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {

    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

    }

}