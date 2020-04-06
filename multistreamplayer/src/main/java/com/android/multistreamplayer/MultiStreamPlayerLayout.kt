package com.android.multistreamplayer

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView
import com.android.multistreamchat.chat.Chat
import com.android.multistreamchat.twitch_chat.api.RetrofitInstance
import com.android.multistreamplayer.player.MultiStreamPlayer.Companion.LIVE_STREAM
import com.android.multistreamplayer.alarm.Alarm
import com.android.multistreamplayer.chat.adapters.ChatAdapter
import com.android.multistreamplayer.chat.chat_factories.ChatFactory
import com.android.multistreamplayer.chat.chat_factories.ChatType
import com.android.multistreamplayer.media_source.MediaSource
import com.android.multistreamplayer.media_source.TwitchMediaSource
import com.android.multistreamplayer.media_source.TwitchMediaSource.Companion.TWITCH_URL
import com.android.multistreamplayer.player.MultiStreamPlayer
import com.android.multistreamplayer.settings.ResourceListener
import com.android.multistreamplayer.settings.SettingsLayout
import com.android.multistreamplayer.settings.animations.ExpandAnimation
import com.android.multistreamplayer.settings.groups.Group
import com.android.multistreamplayer.settings.groups.selection_group.SelectionGroup
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
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
    private var chatList: RecyclerView? = null
    var chatAdapter: ChatAdapter? = null
    set(value) {
        field = value
        chatList?.adapter = value
    }
    private var chat: Chat? = null
    private var settings: SettingsLayout? = null
    private var settingsScrollView: ScrollView? = null
    private var settingsIconView: ImageButton? = null
    private var settingsExpandAnimation: ExpandAnimation? = null

    lateinit var alarm: Alarm
    var alarmImageButton: ImageButton? = null

    private var playerType: Int = LIVE_STREAM

    private fun init(context: Context?, attrs: AttributeSet? = null) {

        context?.obtainStyledAttributes(attrs, R.styleable.MultiStreamPlayerLayout)?.apply {
            playerType = getInt(
                R.styleable.MultiStreamPlayerLayout_playerType,
                LIVE_STREAM
            ).also { playerType ->
                multiStreamPlayer = MultiStreamPlayer(context, playerType).also {
                    it.chat = chat
                }
            }
            addOnResourceReadyListener(object : ResourceListener {
                val qualityHeader: String = context.getString(R.string.quality)
                override fun onResourceTracksReady(player: TrackSelectionArray) {

                    //to make sure we use only initial settings and do not  create duplicate groups
                    if (settings == null || settings?.groups?.get(qualityHeader) != null) return

                    val size = player[0]?.length() ?: 0
                    val groupItemsArray = Array<SelectionGroup.Builder.SelectionGroupItem?>(size) { null }
                    val track = player[0]

                    for (i in 0 until size) {
                        groupItemsArray[i] = track?.getFormat(i).let { format ->
                            SelectionGroup.Builder.SelectionGroupItem(
                                "${format?.height}P", "${format?.bitrate}", R.drawable.full_hd_icon,
                                track?.selectedFormat == format
                            )
                        }
                    }
                    val group = SelectionGroup.Builder(context)
                        .addHeader(qualityHeader)
                        .addItems(groupItemsArray)
                        .addSelectionListener { selectionGroup, position ->
                            selectionGroup.items?.forEach {
                                it.findViewById<ImageView>(R.id.selectionIcon)?.visibility =
                                    View.INVISIBLE
                            }
                            selectionGroup.items?.get(position)
                                ?.findViewById<ImageView>(R.id.selectionIcon)?.visibility =
                                View.VISIBLE
                            playAnimation()
                            multiStreamPlayer.buildTracksParams(track, position)
                            Toast.makeText(context, "CLICKED: $position", Toast.LENGTH_SHORT).show()
                        }
                        .build()

                    addSettingsGroup(group)
                }

                override fun onTrackChanged(
                    trackGroups: TrackGroupArray?,
                    trackSelections: TrackSelectionArray?
                ) {

                }
            })
            recycle()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun init(chatType:  ChatType) {
        chat = buildChat(chatType)
        multiStreamPlayer.chat = chat
        val mediaSource = when(chatType) {
            is ChatType.TwitchChatType -> TwitchMediaSource(RetrofitInstance.getRetrofit(TWITCH_URL))
            is ChatType.MixerChatType -> return
        }
        multiStreamPlayer.apply {
            this.mediaSource = mediaSource as MediaSource<Any>
            play(chat?.channelName ?: return)
        }
    }

    private fun buildChat(chatType: ChatType): Chat {
        return ChatFactory.create(chatType, context)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        allocateViews()
    }

    private fun allocateViews() {
        playerView = findViewById(R.id.player)

        playerView?.player = multiStreamPlayer.player.apply {
            playWhenReady = true
        }

        chatList = findViewById(R.id.chat)

        chatList?.adapter = chatAdapter


        alarmImageButton = playerView?.findViewById(R.id.alarm_icon)

        settingsScrollView = findViewById(R.id.settings_scroll_view)
        settings = settingsScrollView?.findViewById(R.id.settings)
        settingsIconView = playerView?.findViewById(R.id.settings_icon)

        settingsExpandAnimation = ExpandAnimation(
            context,
            R.transition.expand_transition
        ).also { settings?.expandAnimation = it }

        settingsIconView?.setOnClickListener {
            playAnimation()
        }
        settings?.backButton?.setOnClickListener {
            playAnimation()
        }
    }

    fun initAlarm(supportFragmentManager: FragmentManager) {
        Alarm(
            context,
            supportFragmentManager
        ).also { alarm -> alarmImageButton?.setOnClickListener { alarm.showDialog() } }
    }

    private fun addOnResourceReadyListener(listener: ResourceListener) {
        multiStreamPlayer.controller?.listeners?.add(listener)
    }

    fun playAnimation() {
        settingsExpandAnimation?.playAnimation(settingsScrollView, rootView = this)
    }

    fun addSettingsGroup(group: Group) {
        settings?.addGroup(group)
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

