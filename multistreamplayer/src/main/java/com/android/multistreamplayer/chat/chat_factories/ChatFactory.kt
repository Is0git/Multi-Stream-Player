package com.android.multistreamplayer.chat.chat_factories

import android.content.Context
import com.android.multistreamchat.chat.Chat

object ChatFactory {


    fun create(chatType: PlayerType, context: Context): Chat {
        return when (chatType) {
            is PlayerType.TwitchChatType -> createTwitchChat(chatType, context)
            is PlayerType.MixerPlayerType -> createMixerChat(chatType, context)
        }
    }


    private fun createTwitchChat(
        chatType: PlayerType.TwitchChatType,
        context: Context
    ): Chat {

        return Chat.Builder()
            .setClient(Chat.HOST, Chat.PORT)
            .apply {
                if (chatType.username != null && chatType.token != null) {
                    setUsername(chatType.username!!)
                    setUserToken(chatType.token!!)
                }
                chatType.channelName?.also { autoConnect(it) }

                chatType.dataListener?.also { addDataListener(it) }

                chatType.emoteStateListener?.also { addEmoteStateListener(it) }

            }
            .build(context)

    }

    private fun createMixerChat(chatType: PlayerType.MixerPlayerType, context: Context): Chat {
        return Chat.Builder().build(context)
    }
}

