package com.android.multistreamplayer

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.android.multistreamplayer.MultiStreamPlayer.Companion.LIVE_STREAM
import com.android.multistreamplayer.settings.SettingsLayout
import com.android.multistreamplayer.settings.animations.ExpandAnimation
import com.google.android.exoplayer2.ui.PlayerView

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
    private var settingsIconView: ImageView? = null

    private var settings: SettingsLayout? = null
    private var settingsExpandAnimation: ExpandAnimation? = null

    private var playerType: Int = LIVE_STREAM


    private fun init(context: Context?, attrs: AttributeSet? = null) {
        context?.obtainStyledAttributes(attrs, R.styleable.MultiStreamPlayerLayout)?.apply {
            playerType = getInt(
                R.styleable.MultiStreamPlayerLayout_playerType,
                LIVE_STREAM
            ).also { multiStreamPlayer = MultiStreamPlayer(context, it) }
            recycle()
        }
    }

    override fun onViewAdded(view: View?) {
        super.onViewAdded(view)
        if (view?.id == R.id.player) {
            playerView = findViewById(R.id.player)
            settingsIconView = playerView!!.findViewById<ImageButton>(R.id.settings_icon)
            playerView?.player = multiStreamPlayer.player.apply {
            }
        }
        else if(view?.id == R.id.settings) {
            settings = view as SettingsLayout
            settingsExpandAnimation = ExpandAnimation(context, R.transition.expand_transition).also { settings?.expandAnimation = it }
            settingsIconView?.setOnClickListener {
                settingsExpandAnimation?.playAnimation(settings, rootView =  this)
            }
            settings?.backButton?.setOnClickListener {
                settingsExpandAnimation?.playAnimation(settings, rootView =  this)
            }
        }
    }


    fun play(uri: String) {
        multiStreamPlayer.play(uri)
    }


    fun registerLifeCycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        release()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        release()
    }

    private fun release() {
        playerView?.player?.apply {
            stop()
            release()
        }
    }
}

