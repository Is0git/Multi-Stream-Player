package com.iso.player.chat.chat_factories

import android.content.Context
import com.iso.chat.chat.Chat

object ChatFactory {

    fun create(chatType: PlayerData, context: Context, autoConnect: Boolean): Chat {
        return when (chatType) {
            is PlayerData.TwitchChatType -> createTwitchChat(chatType, context, autoConnect)
            is PlayerData.MixerPlayerType -> createMixerChat(chatType, context)
        }
    }

    private fun createTwitchChat(
        chatType: PlayerData.TwitchChatType,
        context: Context,
        autoConnect: Boolean
    ): Chat {

        return Chat.Builder()
            .setClient(Chat.HOST, Chat.PORT)
            .apply {
                if (chatType.username != null && chatType.token != null) {
                    setUsername(chatType.username!!)
                    setUserToken(chatType.token!!)
                }
                if (autoConnect && chatType.channelName != null) {
                    autoConnect(chatType.channelName!!)
                }
                chatType.dataListener?.also { addDataListener(it) }
                chatType.emoteStateListener?.also { addEmoteStateListener(it) }
                chatType.connectivityListener?.also { addChatConnectivityListener(it) }
                setOnRoomStateListener(chatType.onRoomStateListener)
                setPlatformName("Twitch")
            }
            .build(context)
    }

    private fun createMixerChat(chatType: PlayerData.MixerPlayerType, context: Context): Chat {
        return Chat.Builder().build(context)
    }
}

