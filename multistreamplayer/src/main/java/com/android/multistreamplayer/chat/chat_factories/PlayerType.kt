package com.android.multistreamplayer.chat.chat_factories

import com.android.multistreamchat.chat.listeners.DataListener
import com.android.multistreamchat.chat.listeners.EmoteStateListener
import com.android.multistreamchat.twitch_chat.chat_emotes.TwitchEmotesManager

sealed class PlayerType(var dataListener: DataListener? = null, var  emoteStateListener: EmoteStateListener<*, *>? = null) {
    data class TwitchChatType(val username: String?, val token: String?, val channelName: String?) : PlayerType(null, null) {
        constructor( username: String?,  token: String?,  channelName: String?,  dataListener: DataListener? = null,  emoteStateListener: EmoteStateListener<String, TwitchEmotesManager.TwitchEmote>? = null) : this(username, token, channelName) {
            this.dataListener = dataListener
            this.emoteStateListener = emoteStateListener
        }
    }
    object MixerPlayerType : PlayerType(null, null)
}