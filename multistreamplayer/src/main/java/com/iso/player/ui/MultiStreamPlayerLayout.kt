package com.iso.player.ui

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Rect
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.transition.*
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionScene
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.*
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.iso.chat.chat.Chat
import com.iso.chat.chat.chat_emotes.EmotesManager.Emote
import com.iso.chat.chat.chat_output_handler.ChatOutputHandler.Companion.FOLLOWERS_ONLY
import com.iso.chat.chat.chat_output_handler.ChatOutputHandler.Companion.NONE
import com.iso.chat.chat.chat_output_handler.ChatOutputHandler.Companion.ONLY_EMOTES_MODE
import com.iso.chat.chat.chat_output_handler.ChatOutputHandler.Companion.R9K_MODE
import com.iso.chat.chat.chat_output_handler.ChatOutputHandler.Companion.SLOW_MODE
import com.iso.chat.chat.chat_output_handler.ChatOutputHandler.Companion.SUB_MODE
import com.iso.chat.chat.listeners.DataListener
import com.iso.chat.chat.listeners.EmoteStateListener
import com.iso.chat.chat.socket.ChatConnectivityListener
import com.iso.chat.twitch_chat.api.RetrofitInstance
import com.iso.chat.twitch_chat.chat_emotes.TwitchEmotesManager
import com.iso.chat.twitch_chat.output_handler.OnRoomStateListener
import com.iso.player.R
import com.iso.player.alarm.Alarm
import com.iso.player.chat.adapters.ChatAdapter
import com.iso.player.chat.chat_factories.ChatFactory
import com.iso.player.chat.chat_factories.PlayerData
import com.iso.player.emoticon_picker.EmoticonPickerLayout
import com.iso.player.emoticon_picker.adapters.EmotesViewpagerAdapter
import com.iso.player.emoticon_picker.adapters.OnEmoteLayoutListener
import com.iso.player.listeners.ResourceListener
import com.iso.player.player.MultiStreamPlayer
import com.iso.player.player.MultiStreamPlayer.Companion.LIVE_STREAM
import com.iso.player.player.MultiStreamPlayer.Companion.VIDEO
import com.iso.player.playlist_source_creators.TwitchLiveStreamUrlCreator
import com.iso.player.playlist_source_creators.TwitchLiveStreamUrlCreator.Companion.TWITCH_URL
import com.iso.player.playlist_source_creators.TwitchVodUrlCreator
import com.iso.player.playlist_source_creators.UrlCreator
import com.iso.player.settings.SettingsLayout
import com.iso.player.settings.animations.AnimationController
import com.iso.player.settings.animations.ExpandAnimation
import com.iso.player.settings.groups.Group
import com.iso.player.settings.groups.selection_group.SelectionGroup
import kotlinx.android.synthetic.main.player_layout.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import kotlin.math.absoluteValue

class MultiStreamPlayerLayout : MotionLayout, LifecycleObserver,
    OnEmoteLayoutListener, PopupMenu.OnMenuItemClickListener, OnRoomStateListener,
    View.OnClickListener {
    companion object {
        const val PLAYER_TAG = "PLAYER_TAG"
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initializePlayer(context, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var isChatConnected: Boolean = false
    private var onStateChangeListener: OnStateChangeListener? = null
    private val changeBoundsTransition: Transition by lazy {
        TransitionInflater.from(context).inflateTransition(R.transition.change_bounds_transition)
    }
    lateinit var multiStreamPlayer: MultiStreamPlayer
    lateinit var playerView: PlayerView
    lateinit var chatList: RecyclerView
    var chatAdapter: ChatAdapter? = null
        set(value) {
            field = value
            chatList.adapter = value
        }
    lateinit var chatExpandAnimation: ExpandAnimation
    lateinit var settings: SettingsLayout
    lateinit var settingsScrollView: ScrollView
    lateinit var settingsIconView: ImageButton
    lateinit var settingsExpandAnimation: ExpandAnimation
    lateinit var channelInfoView: ConstraintLayout
    lateinit var profileImageView: ImageView
    lateinit var titleTextView: TextView
    lateinit var categoryView: TextView
    lateinit var channelNameView: TextView
    lateinit var chatMenuDrawer: ImageButton
    lateinit var chatTextInput: TextInputLayout
    private lateinit var playerGestureDetector: GestureDetector
    lateinit var alarmImageButton: ImageButton
    lateinit var followButton: ImageButton
    lateinit var minimizeButton: ImageButton
    lateinit var fullscreenButton: ImageButton
    private var playerType: Int = LIVE_STREAM
    var videoProgressBar: ProgressBar? = null
    lateinit var sendButton: Button
    lateinit var chatNotification: MaterialTextView
    lateinit var scaleGestureDetector: ScaleGestureDetector
    lateinit var emoticonPickerLayout: EmoticonPickerLayout
    lateinit var emoticonPickerExpandAnimation: ExpandAnimation
    private lateinit var emoteViewpagerAdapter: EmotesViewpagerAdapter
    lateinit var chatInputGroup: ConstraintLayout
    lateinit var liveIcon: View
    lateinit var inputText: TextInputEditText
    lateinit var viewersCount: MaterialTextView
    lateinit var flagInfoIcon: ImageView
    lateinit var flagText: MaterialTextView
    private lateinit var controllerView: View
    var startConstraint: Int = R.id.start
    var endConstraint: Int = R.id.end
    var onFollowButtonListener: OnFollowButtonListener? = null
    private val retrofitInstance = RetrofitInstance.getRetrofit(TWITCH_URL)
    private var followButtonEnabledColor: ColorStateList = ColorStateList.valueOf(
        ResourcesCompat.getColor(
            resources,
            R.color.colorOnSecondaryVariant,
            null
        )
    )
    private var followButtonDisabledColor: ColorStateList =
        ColorStateList.valueOf(ResourcesCompat.getColor(resources, android.R.color.white, null))
    private var alarm: Alarm = Alarm(context)
    var currentConstraint = R.id.start
    var currentTransition = R.id.fragment_minimize_transition
    var lastY = 0f
    var lastX = 0f
    var scrollDistanceY = 0f
    var scrollDistanceX = 0f
    var isStateUndefined: Boolean = false
    private var playerBoundStart = 0f
    lateinit var scaleAnimator: ObjectAnimator
    var forwardButton: ImageButton? = null
    var rewindButton: ImageButton? = null
    var seekBar: ViewGroup? = null
    var pauseButton: ImageButton? = null
    var startButton: ImageButton? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        allocateViews()
        setAdapters()
        setViewsParams()
        setupViewAnimations()
        setUserInteraction()
    }

    init {
        this.isFocusableInTouchMode = true
        this.isFocusable = true
        this.requestFocus()
    }

    private fun initializePlayer(context: Context?, attrs: AttributeSet? = null) {
        context?.obtainStyledAttributes(
            attrs,
            R.styleable.MultiStreamPlayerLayout
        )?.apply {
            playerType = getInt(
                R.styleable.MultiStreamPlayerLayout_playerType,
                LIVE_STREAM
            ).also { playerType ->
                multiStreamPlayer = MultiStreamPlayer(context, playerType)
            }
            addOnResourceReadyListener(createResourceListener())
            recycle()
        }
    }

    private fun createResourceListener(): ResourceListener {
        return object : ResourceListener {
            val qualityHeader: String = context.getString(R.string.quality)
            override fun onResourceTracksReady(player: TrackSelectionArray) {
                playerView.updateLayoutParams<LayoutParams> {
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
                videoProgressBar?.visibility = View.INVISIBLE
                //to make sure we use only initial settings and do not  create duplicate groups
                if (settings.groups[qualityHeader] != null) return
                val size = player[0]?.length() ?: 0
                val groupItemsArray =
                    Array<SelectionGroup.Builder.SelectionGroupItem?>(size) { null }
                val track = player[0]
                for (i in 0 until size) {
                    groupItemsArray[i] = track?.getFormat(i).let { format ->
                        SelectionGroup.Builder.SelectionGroupItem(
                            "${format?.height}P",
                            "${format?.frameRate}",
                            when (format?.height) {
                                1080 -> R.drawable.full_hd_icon
                                720 -> R.drawable.hd_icon
                                480 -> R.drawable.icon_480
                                360 -> R.drawable.icon_360
                                160 -> R.drawable.icon_unknown
                                else -> R.drawable.icon_unknown
                            },
                            track?.selectedFormat == format
                        )
                    }
                }
                buildSettingsGroup(
                    qualityHeader,
                    groupItemsArray,
                    track
                ).also {
                    addSettingsGroup(it)
                }
            }

            override fun onTrackChanged(
                trackGroups: TrackGroupArray?,
                trackSelections: TrackSelectionArray?
            ) {

            }

            override fun onStateIdle() {
                videoProgressBar?.visibility = View.VISIBLE
            }

            override fun onStateBuffering() {
                videoProgressBar?.visibility = View.VISIBLE
            }

            override fun onStateFinish() {

                videoProgressBar?.visibility = View.INVISIBLE
            }

            override fun onFailed(error: Exception?) {
                connectPlayer()
                Log.e(PLAYER_TAG, "failed to load video")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun initializePlayer(playerData: PlayerData, lifecycle: Lifecycle? = null) {
        lifecycle?.let { registerLifeCycle(it) }
            ?: if (context is AppCompatActivity) registerLifeCycle((context as AppCompatActivity).lifecycle) else throw InstantiationException(
                "lifecycle is not registered"
            )
        val key: String?
        val urlCreator = when (playerData) {
            is PlayerData.TwitchChatType -> when (playerData.player_type) {
                PlayerData.PlayerType.LIVE -> {
                    val chat = buildChat(playerData)
                    multiStreamPlayer.apply {
                        playerType = LIVE_STREAM
                        this.chat = chat
                        this.playerData = playerData
                    }
                    key = playerData.channelName
                    TwitchLiveStreamUrlCreator(retrofitInstance)
                }
                PlayerData.PlayerType.VOD -> {
                   multiStreamPlayer.playerType = VIDEO
                    key = playerData.videoId
                    val spannable =
                        SpannableString(context.getString(R.string.vod_chat_not_available))
                    chatAdapter?.addLine(spannable)
                    disableInputLayout()
                    TwitchVodUrlCreator(retrofitInstance)
                }
                else -> {
                    throw IllegalArgumentException("player type was not specified")
                }
            }
            is PlayerData.MixerPlayerType -> return
        }.also { it.key = key }
        multiStreamPlayer.apply {
            this.urlCreator = urlCreator as UrlCreator<Any?, Any>
            titleTextView.text = playerData.title
            channelNameView.text = playerData.channelDisplayName
            categoryView.text = playerData.category
            profileImageView.let {
                Glide.with(context).load(playerData.imageUrl).centerInside().into(it)
            }
            viewersCount.text = playerData.viewerCount
            connectPlayer()
        }
    }

    private fun disableInputLayout() {
        val alpha = 0.60f
        chatTextInput.isEnabled = false
        chatInputGroup.alpha = alpha
        sendButton.isEnabled = false
        sendButton.alpha = alpha
        chatMenuDrawer.isEnabled = false
        chatMenuDrawer.alpha = alpha
    }

    private fun connectPlayer() {
        multiStreamPlayer.play(multiStreamPlayer.urlCreator?.key ?: {
            Toast.makeText(context, "KEY IS NEEDED", Toast.LENGTH_SHORT).show()
        })
    }

    private fun buildChat(chatType: PlayerData): Chat {
        addDefaultListeners(chatType)
        return ChatFactory.create(chatType, context, true)
    }

    fun buildSettingsGroup(
        header: String,
        groupItems: Array<SelectionGroup.Builder.SelectionGroupItem?>,
        track: TrackSelection?
    ): Group {
        return SelectionGroup.Builder(context)
            .addHeader(header)
            .addItems(groupItems)
            .addSelectionListener { selectionGroup, position ->
                selectionGroup.items?.forEach {
                    it.findViewById<ImageView>(R.id.selectionIcon)?.visibility =
                        View.INVISIBLE
                }
                selectionGroup.items?.get(position)
                    ?.findViewById<ImageView>(R.id.selectionIcon)?.visibility =
                    View.VISIBLE
                playSettingsAnimation()
                //builds tracks based on params(changes video quality onClick)
                multiStreamPlayer.buildTracksParams(track, position)
            }
            .build()
    }

    private fun addDefaultListeners(chatType: PlayerData) {
        chatType.dataListener = object : DataListener {
            override fun onReceive(message: Spannable) {
                chatAdapter?.addLine(message)
                resolveChatNotification(chatList, true)
            }
        }
        chatType.emoteStateListener = object :
            EmoteStateListener<String, TwitchEmotesManager.TwitchEmote> {
            override fun onComplete() {
                putChatData("Search", emptyList())
                emoticonPickerLayout.postDelayed({ emoticonPickerLayout.forceLayout() }, 1000L)
                emoticonPickerLayout.progressBar.hide()
            }

            override fun onDownload() {
                Log.d("EMOTELISTENER", "onDownloadD")
            }

            override fun onEmotesFetched(emoteSet: List<TwitchEmotesManager.TwitchEmote>) {
                putChatData(context.getString(R.string.global_emote), emoteSet)
                Log.d("EMOTELISTENER", "onEmotesFetched")
            }

            override fun onFailed(throwable: Throwable?) {
                emoticonPickerLayout.progressBar.hide()
            }

            override fun onStartFetch() {
                emoticonPickerLayout.progressBar.show()
            }

        }
        chatType.connectivityListener = object : ChatConnectivityListener {
            override fun onConnectivityStateChange(isConnected: Boolean, platformName: String) {
                isChatConnected = isConnected
                val message = if (isConnected) resources.getString(
                    R.string.connection_establised_message,
                    platformName
                ) else resources.getString(R.string.connection_failed_message, platformName)
                val spannable = SpannableString(message)
                chatAdapter?.addLine(spannable)
            }

            override fun onChatModeChange(isAnonymous: Boolean) {
                if (isAnonymous) chatTextInput.visibility = View.INVISIBLE
            }
        }
        chatType.onRoomStateListener = this
    }

    private fun setAdapters() {
        chatList.adapter = chatAdapter
        chatList.itemAnimator = null
        emoteViewpagerAdapter = EmotesViewpagerAdapter().apply {
            onItemClickListener = this@MultiStreamPlayerLayout
        }
        chatAdapter = ChatAdapter(context)
    }

    private fun getConstraintSetConstraint(
        constraintSetId: Int,
        constraintId: Int
    ): ConstraintSet.Constraint {
        return getConstraintSet(constraintSetId).getConstraint(constraintId)
    }

    private fun showChannelInfo() {
        TransitionManager.beginDelayedTransition(this, changeBoundsTransition)
        getConstraintSetConstraint(
            R.id.start,
            R.id.channel_info_view
        ).layout.apply {
            if (isOrientationLandscape()) {
                bottomToTop = emoticonPickerLayout.id
                topToBottom = UNSET
                getConstraintSetConstraint(R.id.start, playerView.id).layout.bottomToTop =
                    channelInfoView.id
            } else {
                mHeight = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }
    }

    private fun hideChannelInfo() {
        TransitionManager.beginDelayedTransition(this, changeBoundsTransition)
        getConstraintSetConstraint(
            R.id.start,
            R.id.channel_info_view
        ).layout.apply {
            if (isOrientationLandscape()) {
                bottomToTop = UNSET
                topToBottom = PARENT_ID
                getConstraintSetConstraint(R.id.start, playerView.id).layout.bottomToTop =
                    emoticonPickerLayout.id
            } else {
                mHeight = 1
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViewAnimations() {
        channelInfoView.setOnTouchListener { v, event ->
            chatExpandAnimation.playAnimation(this)
            true
        }
        settingsExpandAnimation = ExpandAnimation(
            settings_scroll_view,
            Fade(), object : AnimationController {
                override fun expand(view: View?, isExpanded: Boolean) {
                    settings.requestFocus()
                    chatNotification.visibility = View.INVISIBLE
                    view?.visibility = View.VISIBLE
                }

                override fun hide(view: View?, isExpanded: Boolean) {
                    chatNotification.visibility = View.INVISIBLE
                    view?.visibility = View.INVISIBLE
                }

            }).also { settings.expandAnimation = it }
        chatExpandAnimation =
            ExpandAnimation(chatList,
                R.transition.expand_transition, object : AnimationController {

                    override fun expand(view: View?, isExpanded: Boolean) {
                        chatInputLayout?.visibility = View.VISIBLE
                        chatNotification.visibility = View.VISIBLE
                        view?.visibility = View.VISIBLE
                    }

                    override fun hide(view: View?, isExpanded: Boolean) {
                        chatNotification.visibility = View.INVISIBLE
                        chatInputLayout?.visibility = View.GONE
                        view?.visibility = View.GONE
                    }
                })
        val tran = AutoTransition().excludeChildren(chatList, true)
            .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
            .removeTarget(chatList)
        emoticonPickerExpandAnimation =
            ExpandAnimation(emoticonPickerLayout,
                tran, object : AnimationController {
                    override fun expand(view: View?, isExpanded: Boolean) {
                        if (!isOrientationLandscape()) {
                            getConstraintSetConstraint(R.id.start, R.id.chat).layout?.apply {
                                mHeight = chatList.height
                                bottomToTop = UNSET
                            }
                        }

                        view?.visibility = View.VISIBLE
                        view?.requestFocus()
                    }

                    override fun hide(view: View?, isExpanded: Boolean) {
                        view?.clearFocus()
//                        getConstraintSetConstraint(R.id.start, emoticonPickerLayout.id).layout.apply {
//                            topToBottom = PARENT_ID
//                            bottomToBottom = UNSET
//                        }
                        if (!isOrientationLandscape()) {
                            getConstraintSetConstraint(R.id.start, chatList.id).layout?.apply {
                                mHeight = MATCH_CONSTRAINT
                                bottomToTop = chatInputGroup.id
                            }
                        }
                        view?.visibility = View.GONE
                    }
                })
        scaleAnimator = ObjectAnimator.ofFloat(playerView, "alpha", 1f, 0.50f, 1f)
        scaleAnimator.duration =
            resources.getInteger(android.R.integer.config_longAnimTime).toLong()
        chatList.isNestedScrollingEnabled = false

    }

    public override fun setTransition(transition: MotionScene.Transition) {
        super.setTransition(transition)
    }

    private fun cancelTransition(
        startBounds: Float,
        endBounds: Float
    ): Float {
        var newScrollDistance = 0f
        val transition = getTransition(currentTransition)
        when (currentConstraint) {
            transition.startConstraintSetId -> {
                newScrollDistance = if (progress >= 0.25f) {
                    transitionToEnd()
                    startBounds
                } else {
                    transitionToStart()
                    startBounds
                }
            }
            transition.endConstraintSetId -> {
                newScrollDistance = if (progress <= 0.90f) {
                    transitionToStart()
                    startBounds
                } else {
                    transitionToEnd()
                    endBounds
                }
            }
        }
        return newScrollDistance
    }

    private fun cancelTransitions(): Boolean {
        if (currentTransition == R.id.fragment_minimize_transition) scrollDistanceY =
            cancelTransition(
                this.top.toFloat(),
                this.bottom.toFloat()
            ) else scrollDistanceX =
            cancelTransition(this.top.toFloat(), this.bottom.toFloat())
        return false
    }

    private fun animateTransition(event: MotionEvent): Boolean {
        if (isStateUndefined) {
            val scrolledY = (event.rawY - lastY).absoluteValue
            val scrolledX = (event.rawX - lastX).absoluteValue
            if (scrolledY > scrolledX) {
                if (currentTransition != R.id.fragment_minimize_transition) {
                    currentTransition = R.id.fragment_minimize_transition
                    setTransition(R.id.fragment_minimize_transition)
                }
            } else {
                if (currentTransition != R.id.slide_transition) {
                    currentTransition = R.id.slide_transition
                    setTransition(R.id.slide_transition)
                }
            }
            isStateUndefined = false
            return true
        }
        if (currentTransition == R.id.fragment_minimize_transition) {
            onScrollY(
                event,
                this.top.toFloat(),
                this.bottom.toFloat()
            )
        } else {
            onScrollX(event, 0f, player?.width?.toFloat()!!)
        }
        return true
    }

    private fun onStartTransition(event: MotionEvent): Boolean {
        lastY = event.rawY
        lastX = event.rawX
        when (currentTransition) {
            R.id.fragment_minimize_transition -> scrollDistanceY =
                if (currentConstraint == R.id.start) 0f else this.bottom.toFloat()
            R.id.slide_transition -> scrollDistanceX = if (currentConstraint == R.id.end) {
                playerBoundStart = playerView.left.toFloat()
                0f
            } else this.right.toFloat()
        }
        return true
    }

    private fun onScrollY(event: MotionEvent, startBounds: Float, endBounds: Float) {
        val scrolledY = lastY - event.rawY
        lastY = event.rawY
        scrollDistanceY =
            calculateScrollDistance(scrolledY, scrollDistanceY, startBounds, endBounds)
        val position = scrollDistanceY / endBounds
        progress = position
    }


    private fun onScrollX(event: MotionEvent, startBounds: Float, endBounds: Float) {
        val scrolledX = lastX - event.rawX
        lastX = event.rawX
        scrollDistanceX =
            calculateScrollDistance(scrolledX, scrollDistanceX, startBounds, endBounds)
        val position = scrollDistanceX / endBounds
        progress = position
    }

    private fun calculateScrollDistance(
        scrolled: Float,
        totalScrollDistance: Float,
        startBounds: Float,
        endBounds: Float
    ): Float {
        return if (scrolled < 0) {
            (totalScrollDistance + scrolled.absoluteValue).coerceAtMost(endBounds)
        } else {
            (totalScrollDistance - scrolled).coerceAtLeast(startBounds)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun setUserInteraction() {
        playerView.setOnTouchListener { v, event ->
            val scaleResult = scaleGestureDetector.onTouchEvent(event)
            playerGestureDetector.onTouchEvent(event)
            when (event.action) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    return@setOnTouchListener cancelTransitions()
                }
                MotionEvent.ACTION_MOVE -> {
                    if (event.pointerCount == 1 && !scaleGestureDetector.isInProgress) {
                        return@setOnTouchListener animateTransition(event)
                    }
                }
                MotionEvent.ACTION_DOWN -> {
                    return@setOnTouchListener onStartTransition(event)
                }

            }
            false
        }
        playerGestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent?): Boolean {
                    if (isOrientationLandscape()) chatExpandAnimation.playAnimation(this@MultiStreamPlayerLayout)
                    return true
                }

                override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                    playerView.apply {
                        if (isControllerVisible) hideController() else showController()
                    }
                    return true
                }

                override fun onScroll(
                    e1: MotionEvent,
                    e2: MotionEvent,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {
                    return false
                }
            })
        chatMenuDrawer.setOnClickListener {
            PopupMenu(context, chatMenuDrawer).apply {
                menuInflater.inflate(R.menu.chat_menu, menu)
                this.menu.findItem(R.id.chatEnabled).isChecked = isChatConnected
                setOnMenuItemClickListener(this@MultiStreamPlayerLayout)
                this.show()
            }
        }
        chatList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                resolveChatNotification(recyclerView, false)
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                resolveChatNotification(recyclerView, false)
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        settingsIconView.setOnClickListener {
            playSettingsAnimation()
        }
        settings.backButton?.setOnClickListener {
            playSettingsAnimation()
        }
        settings.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP && settingsExpandAnimation.isExpanded) {
                playSettingsAnimation()
                determineNextFocus()
                return@setOnKeyListener true
            }
            false
        }
        playerView.apply {
            setControllerVisibilityListener {
                when (it) {
                    View.VISIBLE -> {
                        showChannelInfo()
                    }
                    View.GONE -> {
                        hideChannelInfo()
                    }
                    else -> {
                        hideChannelInfo()
                    }
                }
            }
        }
        chatInputGroup.setOnTouchListener { v, event -> true }
        alarmImageButton.setOnClickListener { initAlarm() }
        scaleGestureDetector =
            ScaleGestureDetector(context, object : ScaleGestureDetector.OnScaleGestureListener {
                var spanY: Float = 0f
                override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                    spanY = detector?.currentSpanY ?: 0f
                    return true
                }

                override fun onScaleEnd(detector: ScaleGestureDetector?) {
                    scaleAnimator.start()
                    TransitionManager.beginDelayedTransition(
                        this@MultiStreamPlayerLayout,
                        changeBoundsTransition
                    )
                    if (isOrientationLandscape()) {
                        playerView.resizeMode = if (detector?.currentSpanY!! > spanY) {
                            AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        } else AspectRatioFrameLayout.RESIZE_MODE_FIT
                    }
                }

                override fun onScale(detector: ScaleGestureDetector?): Boolean {
                    return false
                }

            })
        fullscreenButton.setOnClickListener {
            if (resources?.configuration?.orientation == ORIENTATION_PORTRAIT) {
                (context as Activity).requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else {
                (context as Activity).requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            }
            postDelayed({
                (context as Activity).requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
            }, 2000)
        }
        followButton.clicks()
            .onEach {
                ViewCompat.setBackgroundTintList(
                    it,
                    if (it.isSelected) {
                        onFollowButtonListener?.unFollow(it)
                        followButtonDisabledColor
                    } else {
                        onFollowButtonListener?.follow(it)
                        followButtonEnabledColor
                    }
                )

                it.isSelected = !it.isSelected
            }
            .buffer(Channel.CONFLATED)
            .launchIn((context as AppCompatActivity).lifecycleScope)
        chatTextInput.apply {
            setEndIconOnClickListener {
                this@MultiStreamPlayerLayout.requestFocus()
                emoticonPickerExpandAnimation.playAnimation(this@MultiStreamPlayerLayout)
            }
            editText?.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    typeMessage()
                    inputText.apply {
                        editableText?.clear()
                        chatTextInput.clearFocus()
                    }
                    false
                } else false
            }
        }
        emoticonPickerLayout.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP && emoticonPickerExpandAnimation.isExpanded) {
                emoticonPickerExpandAnimation.playAnimation(this)
                determineNextFocus()
                return@setOnKeyListener true
            }
            false
        }
        flagInfoIcon.setOnClickListener(this)
        flagText.setOnClickListener(this)
    }

    fun determineNextFocus() {
        when {
            settingsExpandAnimation.isExpanded -> settings.requestFocus()
            emoticonPickerExpandAnimation.isExpanded -> emoticonPickerLayout.requestFocus()
            else -> this.requestFocus()
        }
    }

    fun resolveChatNotification(recyclerView: RecyclerView, scroll: Boolean) {
        (recyclerView.layoutManager as LinearLayoutManager).apply {
            val lastVisible = findLastCompletelyVisibleItemPosition()
            val firstVisible = findFirstVisibleItemPosition()
            when {
                firstVisible == 0 && lastVisible == itemCount - 1 -> chatNotification.hide()
                firstVisible >= 0 && lastVisible < itemCount - 8 -> chatNotification.show()
                lastVisible > itemCount - 8 -> {
                    if (scroll) chatList.smoothScrollToPosition(itemCount - 1)
                    chatNotification.hide()
                }
                else -> {
                    chatNotification.hide()
                }
            }
        }
    }

    fun setButtonStateNotFollowing() {
        ViewCompat.setBackgroundTintList(followButton, followButtonDisabledColor)
    }

    fun setButtonStateFollowing() {
        ViewCompat.setBackgroundTintList(followButton, followButtonEnabledColor)
    }

    private fun setViewsParams() {
        emoticonPickerLayout.setBackgroundColor(
            ResourcesCompat.getColor(
                resources,
                R.color.colorSurface,
                null
            )
        )
        emoticonPickerLayout.apply {
            onSearchTextChange = { text: String? ->
                text?.let {
                    emoteViewpagerAdapter.find(it)
                }

            }
            emotesViewPager?.adapter = emoteViewpagerAdapter
            TabLayoutMediator(emotesTabLayout!!, emotesViewPager!!) { tab, position ->
                tab.text = emoteViewpagerAdapter.data?.keys?.elementAt(position)
            }.attach()
            playerView.setOnClickListener(null)
        }

        matchPlayerWithScreenAspectRatio()
    }

    private fun matchPlayerWithScreenAspectRatio() {
        val constraint = getConstraintSetConstraint(R.id.end, R.id.player).layout
        val filledHeight = (constraint.mWidth / 1.77777).toInt()
        constraint.mHeight = filledHeight
        getConstraintSetConstraint(R.id.slide, R.id.player).layout.mHeight = filledHeight
    }

    private fun initAlarm() {
        alarm.showDialog()
    }

    private fun addOnResourceReadyListener(listener: ResourceListener) {
        multiStreamPlayer.controller?.listeners?.add(listener)
    }

    private fun playSettingsAnimation() {
        settingsExpandAnimation.playAnimation(this)
    }

    fun addSettingsGroup(group: Group) {
        settings.addGroup(group)
    }

    private fun registerLifeCycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        release()
        emoticonPickerLayout.clear()
    }


    fun release() {
        multiStreamPlayer.chat?.clear()
        playerView.player?.apply {
            this.release()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        val constraintSet = ConstraintSet()
        newConfig?.let {
            if (newConfig.orientation == ORIENTATION_PORTRAIT) {
                setPortraitConstraints(constraintSet)
                chatTextInput.isHintEnabled = true
            } else {
                setLandScapeConstraints(constraintSet)
                chatTextInput.isHintEnabled = false
            }
            matchPlayerWithScreenAspectRatio()
            constraintSet.applyTo(this)
            onRoomStateChanged(
                multiStreamPlayer.chat?.getCurrentChatFlagSet() ?: NONE,
                multiStreamPlayer.chat?.getCurrentChatFlag() ?: NONE
            )
            if (playerView.isControllerVisible) showChannelInfo() else hideChannelInfo()
        }
        super.onConfigurationChanged(newConfig)
    }

    private fun setLandScapeConstraints(constraintSet: ConstraintSet) {
        constraintSet.clone(
            context,
            R.layout.player_layout_land
        )
        chatInputGroup.setConstraintSet(
            ConstraintSet().apply {
                clone(context, R.layout.chat_input_layout_land)
            }
        )
        inputText.updateLayoutParams<FrameLayout.LayoutParams> {
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        loadLayoutDescription(R.xml.player_land_motion)
    }

    private fun setPortraitConstraints(constraintSet: ConstraintSet) {
        constraintSet.clone(
            context,
            R.layout.chat_input_layout
        )
        chatInputGroup.setConstraintSet(
            ConstraintSet().apply {
                clone(context, R.layout.chat_input_layout)
            }
        )
        inputText.updateLayoutParams<FrameLayout.LayoutParams> {
            height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        loadLayoutDescription(R.xml.player_motion_scene)
    }

    private fun typeMessage() {
        multiStreamPlayer.chat?.typeMessage(chatTextInput.editText?.text.toString())
    }


    fun isOrientationLandscape(): Boolean {
        return resources.configuration.orientation == ORIENTATION_LANDSCAPE
    }

    fun putChatData(data: String, emote: List<Emote>) {
        emoteViewpagerAdapter.putData(data, emote)
    }

    private fun allocateViews() {
        playerView = findViewById(R.id.player)
        followButton = player.findViewById(R.id.icon_love)
        minimizeButton = player.findViewById(R.id.down_icon)
        fullscreenButton = player.findViewById(R.id.fullscreen_icon)
        channelInfoView = findViewById(R.id.channel_info_view)
        channelInfoView.apply {
            profileImageView = this.findViewById(R.id.profile_image)
            titleTextView = this.findViewById(R.id.title_text)
            channelNameView = this.findViewById(R.id.channel_name)
            categoryView = this.findViewById(R.id.game_name)
        }
        playerView.player = multiStreamPlayer.player.apply {
            playWhenReady = true
        }
        controllerView = playerView.findViewById(R.id.controller_view)
        chatList = findViewById(R.id.chat)
        alarmImageButton = playerView.findViewById(R.id.alarm_icon)
        settingsScrollView = findViewById(R.id.settings_scroll_view)
        settings = settingsScrollView.findViewById(R.id.settings)
        settingsIconView = playerView.findViewById(R.id.settings_icon)
        liveIcon = playerView.findViewById(R.id.live_icon)
        viewersCount = playerView.findViewById(R.id.viewersCount)
        chatMenuDrawer = findViewById(R.id.menu_drawer_icon)
        videoProgressBar = findViewById(R.id.video_progress_bar)
        sendButton = findViewById(R.id.send_button)
        sendButton.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.colorOnSecondaryVariant,
                null
            )
        )
        chatNotification = findViewById(R.id.chatScrollNotificationText)
        chatNotification.visibility = View.INVISIBLE
        sendButton.setOnClickListener {
            if (inputText.text.isNullOrBlank()) return@setOnClickListener
            typeMessage()
            if (emoticonPickerExpandAnimation.isExpanded) emoticonPickerExpandAnimation.playAnimation(
                this
            )
            inputText.editableText?.clear()
            inputText.clearFocus()
        }
        emoticonPickerLayout = findViewById(R.id.emoticon_picker)
        chatInputGroup = findViewById(R.id.chatInputLayout)
        chatTextInput = chatInputGroup.findViewById(R.id.chatTextField)
        flagInfoIcon = chatInputGroup.findViewById(R.id.info_icon)
        flagText = chatInputGroup.findViewById(R.id.info_text)
        inputText = chatTextInput.findViewById(R.id.textInput)
        seekBar = findViewById(R.id.seekBar)
        forwardButton = controllerView.findViewById(R.id.exo_ffwd)
        rewindButton = controllerView.findViewById(R.id.exo_rew)
        startButton = playerView.findViewById(R.id.exo_play)
        pauseButton = playerView.findViewById(R.id.exo_pause)

    }


    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentConstraint == R.id.start && currentTransition == R.id.fragment_minimize_transition) {
                this.transitionToEnd()
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onEmoteItemClick(view: View, itemPosition: Int) {
        val viewPagerPosition = emoticonPickerLayout.emotesViewPager?.currentItem ?: return
        emoticonPickerLayout.progressBar.hide()
        val item =
            emoteViewpagerAdapter.data?.values?.elementAt(viewPagerPosition)?.get(itemPosition)
        inputText.text?.append("${item?.code} ")
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.chatEnabled) {
            if (item.isChecked) multiStreamPlayer.chat?.disconnect() else multiStreamPlayer.chat?.reconnect()
            return true
        }
        return false
    }

    override fun onRoomStateChanged(roomStateFlags: Int, firstEnabledFlag: Int) {
        val showMessage = when (firstEnabledFlag) {
            NONE -> {
                hideChatFlags()
                return
            }
            FOLLOWERS_ONLY -> resources.getString(R.string.followers_only)
            R9K_MODE -> resources.getString(R.string.r9k_mode)
            SLOW_MODE -> resources.getString(R.string.slow_mode)
            SUB_MODE -> resources.getString(R.string.sub_only)
            ONLY_EMOTES_MODE -> resources.getString(R.string.emote_only)
            else -> {
                hideChatFlags()
                return
            }
        }
        flagText.text = showMessage
        showChatFlags()
    }

    private fun hideChatFlags() {
        flagText.hideCompletely()
        flagInfoIcon.hideCompletely()
    }

    private fun showChatFlags() {
        flagInfoIcon.show()
        flagText.show()
    }

    override fun onClick(v: View?) {
        showChatModeDialog()
    }

    private fun showChatModeDialog() {
        getChatModeTitleDescription(multiStreamPlayer.chat?.getCurrentChatFlag() ?: NONE)?.apply {
            MaterialAlertDialogBuilder(context)
                .setTitle(this.first.toUpperCase(Locale.getDefault()))
                .setMessage(Html.fromHtml(this.second))
                .show()
        }
    }

    fun getPlayerType() : Int {
        return multiStreamPlayer.playerType
    }

    private fun getChatModeTitleDescription(flag: Int): Pair<String, String>? {
        val title: String
        val description: String
        when (flag) {
            FOLLOWERS_ONLY -> {
                title = resources.getString(R.string.followers_only)
                description = resources.getString(
                    R.string.followers_only_description,
                    multiStreamPlayer.chat?.getFollowersOnlyTime() ?: 0
                )
            }
            R9K_MODE -> {
                title = resources.getString(R.string.r9k_mode)
                description = resources.getString(R.string.r9k_mode_description)
            }
            SLOW_MODE -> {
                title = resources.getString(R.string.slow_mode)
                description = resources.getString(
                    R.string.slow_mode_description,
                    multiStreamPlayer.chat?.getSlowChatTime() ?: 0
                )
            }
            SUB_MODE -> {
                title = resources.getString(R.string.sub_only)
                description = resources.getString(R.string.sub_only_description)
            }
            ONLY_EMOTES_MODE -> {
                title = resources.getString(R.string.emote_only)
                description = resources.getString(R.string.emote_only_description)
            }
            else -> {
                return null
            }
        }
        return Pair(title, description)
    }

}

fun isOverLapping(view: View, event: MotionEvent?): Boolean {
    val windowPosition = IntArray(2)
    view.getLocationInWindow(windowPosition)
    return Rect(
        windowPosition.first(),
        windowPosition[1],
        windowPosition.first() + view.width,
        windowPosition[1] + view.height
    ).contains(
        event?.rawX?.toInt()!!,
        event.rawY.toInt()
    )
}

interface OnStateChangeListener {
    fun onStateChange(state: Int)
}

interface TransitionListener {
    fun onFullScreen()
    fun onMinimize()
    fun onHide()
}


interface OnFollowButtonListener {
    fun follow(view: View)
    fun unFollow(view: View)
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hideCompletely() {
    this.visibility = View.GONE
}

@ExperimentalCoroutinesApi
fun View.clicks() = callbackFlow<View> {
    setOnClickListener { offer(it) }
    awaitClose { setOnClickListener(null) }
}

