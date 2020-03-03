package com.android.livestreamvideoplayer

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.android.multistreamplayer.LiveStreamController
import com.android.multistreamplayer.MultiStreamPlayer
import com.android.multistreamplayer.MultiStreamPlayer.Companion.LIVE_STREAM
import com.android.multistreamplayer.VideoController
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.android.synthetic.main.activity_main.view.*

class MultiStreamPlayerLayout : ConstraintLayout, LifecycleObserver {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    lateinit var multiStreamPlayer: MultiStreamPlayer

    private var playerView: PlayerView? = null
    private var playerType: Int = LIVE_STREAM

    private fun init(context: Context?, attrs: AttributeSet? = null) {
        (context as AppCompatActivity).lifecycle.addObserver(this)
        context.obtainStyledAttributes(attrs, R.styleable.MultiStreamPlayerLayout)?.apply {
            playerType = getInt(R.styleable.MultiStreamPlayerLayout_playerType, LIVE_STREAM).also { multiStreamPlayer = MultiStreamPlayer(context, it) }
            recycle()
            }
        }

    override fun onViewAdded(view: View?) {
        super.onViewAdded(view)
        if (view?.id == R.id.player) {
            playerView = findViewById(R.id.player)
            playerView?.player = multiStreamPlayer.player
        }

    }
    fun play(uri: String) {
        multiStreamPlayer.play(uri)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        playerView?.player?.apply {
            stop()
            release()
        }
    }
}

