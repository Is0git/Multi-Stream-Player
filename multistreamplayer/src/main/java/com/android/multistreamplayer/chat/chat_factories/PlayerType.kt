package com.android.multistreamplayer.chat.chat_factories

import com.android.multistreamchat.chat.listeners.DataListener
import com.android.multistreamchat.chat.listeners.EmoteStateListener
import com.android.multistreamchat.twitch_chat.chat_emotes.TwitchEmotesManager

sealed class PlayerType(
    var dataListener: DataListener? = null,
    var emoteStateListener: EmoteStateListener<*, *>? = null,
    var title: String? = null,
    var channelDisplayName: String? = null,
    var category: String? = null,
    var imageUrl: String? = null
) {
    class TwitchChatType(
        var username: String?,
        var token: String?,
        var channelName: String?,
        title: String? = null,
        channelDisplayName: String? = null,
        category: String? = null,
        dataListener: DataListener? = null,
        imageUrl: String? = null,
        emoteStateListener: EmoteStateListener<String, TwitchEmotesManager.TwitchEmote>? = null
    ) :
        PlayerType(dataListener, emoteStateListener, title, channelDisplayName, category, imageUrl)

    object MixerPlayerType : PlayerType(null, null)
}