package com.android.multistreamplayer.chat.chat_factories

import android.content.Context
import com.android.multistreamchat.chat.Chat

object ChatFactory {


    fun create(chatType: ChatType, context: Context): Chat {
        return when (chatType) {
            is ChatType.TwitchChatType -> createTwitchChat(chatType, context)
            is ChatType.MixerChatType -> createMixerChat(chatType, context)
        }
    }


    private fun createTwitchChat(
        chatType: ChatType.TwitchChatType,
        context: Context
    ): Chat {

        return Chat.Builder()
            .setClient(Chat.HOST, Chat.PORT)
            .apply {
                if (chatType.username != null && chatType.token != null) {
                    setUsername(chatType.username)
                    setUserToken(chatType.token)
                }
                chatType.channelName?.also { autoConnect(it) }

                chatType.dataListener?.also { addDataListener(it) }

                chatType.emoteStateListener?.also { addEmoteStateListener(it) }

            }
            .build(context)

    }

    private fun createMixerChat(chatType: ChatType.MixerChatType, context: Context): Chat {
        return Chat.Builder().build(context)
    }
}

