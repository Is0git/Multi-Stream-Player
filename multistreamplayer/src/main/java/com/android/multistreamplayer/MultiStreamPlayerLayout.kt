package com.android.multistreamplayer

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ScrollView
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

    private var settings: SettingsLayout? = null
    private var settingsScrollView: ScrollView? = null
    private var settingsIconView: ImageButton? = null
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

    override fun onFinishInflate() {
        super.onFinishInflate()
        allocateViews()
    }


    private fun allocateViews() {
        playerView = findViewById(R.id.player)

        playerView?.player = multiStreamPlayer.player.apply {

        }

        settingsScrollView = findViewById(R.id.settings_scroll_view)
        settings = settingsScrollView?.findViewById(R.id.settings)
        settingsIconView = playerView?.findViewById(R.id.settings_icon)

        settingsExpandAnimation = ExpandAnimation(context, R.transition.expand_transition).also { settings?.expandAnimation = it }

        settingsIconView?.setOnClickListener { settingsExpandAnimation?.playAnimation(settingsScrollView, rootView = this) }
        settings?.backButton?.setOnClickListener { settingsExpandAnimation?.playAnimation(settingsScrollView, rootView = this) }
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

