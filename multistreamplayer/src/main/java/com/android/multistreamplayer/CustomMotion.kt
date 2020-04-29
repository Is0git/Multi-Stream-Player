package com.android.multistreamplayer

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.FragmentManager
import com.google.android.exoplayer2.ui.PlayerView

class CustomMotion : MotionLayout, MotionLayout.TransitionListener {

    companion object {
        const val PLAYER_FULLSCREEN = 0
        const val PLAYER_MINIMIZED = 1
        const val PLAYER_HIDDEN = 2
    }

    var isScrollable: Boolean = true

    var playerFragmentContainer: FrameLayout? = null

    var playerViewState = PLAYER_FULLSCREEN

    var transitionListener: TransitionListener? = null

    private val includedViews: MutableList<Int> by lazy { mutableListOf<Int>() }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        this.setTransitionListener(this)

        includeView(R.id.settings_icon)
        includeView(R.id.exo_play)
        includeView(R.id.exo_pause)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        playerFragmentContainer = findViewById(R.id.player_fragment)
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        //no need to handle player motion layout since player fragment is not active
        val containersLayout = getPlayerLayout() ?: return false

        //allow to handle child view that overlap playerView
        if (containersLayout.settingsExpandAnimation?.isExpanded!!) return false


        Log.d("TSTS", isTouchEventOnLeftSide(containersLayout.playerView!!, event).toString())

        if (playerViewState == PLAYER_FULLSCREEN && isTouchEventOnLeftSide(
                containersLayout.playerView!!,
                event
            )
        ) return false

        //when player fragment in idle state we need to make sure all touch events are passed to children

        for (a in includedViews) {
            val view = containersLayout.findViewById<View>(a)
            if (isOverLapping(view, event)) {
                return false
            }
        }
        return true
    }

    private fun isOverLapping(view: View, event: MotionEvent?): Boolean {
        val windowPosition = IntArray(2)
        view.getLocationInWindow(windowPosition)

        return Rect(
            windowPosition.first(),
            windowPosition[1],
            windowPosition.first() + view.width,
            windowPosition[1] + view.height
        ).let {
            it.contains(
                event?.rawX?.toInt()!!,
                event.rawY.toInt()
            )
        }
    }

    private fun isTouchEventOnLeftSide(view: PlayerView, event: MotionEvent?): Boolean {
        val windowPosition = IntArray(2)
        view.getLocationInWindow(windowPosition)

        return Rect(
            windowPosition.first(),
            windowPosition[1],
            (windowPosition.first() + windowPosition.first() + view.width) / 2,
            (windowPosition[1] + view.height)
        ).let {
            it.contains(
                event?.rawX?.toInt()!!,
                event.rawY.toInt()
            )
        }
    }

    fun setDefaultTransitionHandler(fragmentManager: FragmentManager) {
        transitionListener = object : CustomMotion.TransitionListener {
            override fun onFullScreen() {

            }

            override fun onMinimize() {
            }

            override fun onHide() {
                val fragment = fragmentManager.findFragmentById(R.id.player_fragment)
                fragment?.also {
                    fragmentManager.beginTransaction().remove(it).commit()
                }
            }
        }
    }

    fun includeView(@IdRes viewId: Int) {
        includedViews.add(viewId)
    }

    fun isViewIncluded(@IdRes viewId: Int): Boolean {
        return includedViews.contains(viewId)
    }

    private fun getPlayerLayout(): MultiStreamPlayerLayout? {
        playerFragmentContainer?.children?.apply {
            return if (count() != 0) playerFragmentContainer?.children?.first() as MultiStreamPlayerLayout? else null
        }
        return null
    }

    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {

    }

    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

    }

    override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
        val visibility = when {
            p3 > 0.50f -> View.INVISIBLE
            p3 < 0.50f && p1 == R.id.start -> View.VISIBLE
            else -> return
        }

        getPlayerLayout()?.apply {
            chatTextInput?.visibility = visibility
            settingsIconView?.visibility = visibility
            alarmImageButton?.visibility = visibility
            followButton?.visibility = visibility
            fullscreenButton?.visibility = visibility
            minimizeButton?.visibility = visibility
            sendButton?.visibility = visibility
            chatMenuDrawer?.visibility = visibility
        }
    }

    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
        val playerLayout = getPlayerLayout()

        playerViewState = when (p1) {
            R.id.start -> {
                playerLayout?.apply {
                    (pauseButton?.layoutParams as LayoutParams).also {
                        it.startToStart = LayoutParams.PARENT_ID
                        it.endToEnd = LayoutParams.UNSET
                        it.topToTop = LayoutParams.UNSET
                        it.bottomToBottom = LayoutParams.PARENT_ID
                        pauseButton?.layoutParams = it
                    }

                    (playButton?.layoutParams as LayoutParams).also {
                        it.startToStart = LayoutParams.PARENT_ID
                        it.endToEnd = LayoutParams.UNSET
                        it.topToTop = LayoutParams.UNSET
                        it.bottomToBottom = LayoutParams.PARENT_ID
                        playButton?.layoutParams = it
                    }
                }
                PLAYER_FULLSCREEN
            }
            R.id.end -> {
                playerLayout?.apply {
                    (pauseButton?.layoutParams as LayoutParams).also {
                        it.startToStart = LayoutParams.PARENT_ID
                        it.endToEnd = LayoutParams.PARENT_ID
                        it.topToTop = LayoutParams.PARENT_ID
                        it.bottomToBottom = LayoutParams.PARENT_ID
                        pauseButton?.layoutParams = it
                    }

                    (playButton?.layoutParams as LayoutParams).also {
                        it.startToStart = LayoutParams.PARENT_ID
                        it.endToEnd = LayoutParams.PARENT_ID
                        it.topToTop = LayoutParams.PARENT_ID
                        it.bottomToBottom = LayoutParams.PARENT_ID
                        playButton?.layoutParams = it
                    }
                }
                PLAYER_MINIMIZED
            }
            R.id.slide -> {
                transitionListener?.onHide()
                PLAYER_HIDDEN
            }
            else -> PLAYER_FULLSCREEN
        }
    }

    interface TransitionListener {
        fun onFullScreen()

        fun onMinimize()

        fun onHide()
    }
}