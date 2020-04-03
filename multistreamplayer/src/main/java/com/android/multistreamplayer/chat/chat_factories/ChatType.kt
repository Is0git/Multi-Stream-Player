package com.android.multistreamplayer.chat.chat_factories

import com.android.multistreamchat.chat.listeners.DataListener
import com.android.multistreamchat.chat.listeners.EmoteStateListener

sealed class ChatType(var dataListener: DataListener?, var  emoteStateListener: EmoteStateListener<*, *>?) {
    data class TwitchChatType(val username: String?, val token: String?, val channelName: String?) : ChatType(null, null) {
        constructor( username: String?,  token: String?,  channelName: String?,  dataListener: DataListener?,  emoteStateListener: EmoteStateListener<*, *>?) : this(username, token, channelName) {
            this.dataListener = dataListener
            this.emoteStateListener = emoteStateListener
        }
    }
    object MixerChatType : ChatType(null, null)
}