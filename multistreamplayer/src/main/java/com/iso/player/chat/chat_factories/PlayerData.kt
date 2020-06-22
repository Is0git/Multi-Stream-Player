package com.iso.player.chat.chat_factories

import com.iso.chat.chat.listeners.DataListener
import com.iso.chat.chat.listeners.EmoteStateListener
import com.iso.chat.chat.socket.ChatConnectivityListener
import com.iso.chat.twitch_chat.chat_emotes.TwitchEmotesManager
import com.iso.chat.twitch_chat.output_handler.OnRoomStateListener

sealed class PlayerData(
    var dataListener: DataListener? = null,
    var emoteStateListener: EmoteStateListener<*, *>? = null,
    var connectivityListener: ChatConnectivityListener? = null,
    var title: String? = null,
    var channelDisplayName: String? = null,
    var category: String? = null,
    var imageUrl: String? = null,
    var viewerCount: String? = null,
    var player_type: PlayerType = PlayerType.LIVE,
    var onRoomStateListener: OnRoomStateListener? = null
) {
    class TwitchChatType(
        player_type: PlayerType,
        var username: String?,
        var token: String?,
        var channelName: String?,
        title: String? = null,
        channelDisplayName: String? = null,
        category: String? = null,
        imageUrl: String? = null,
        viewerCount: String? = null,
        var videoId: String? = null,
        dataListener: DataListener? = null,
        emoteStateListener: EmoteStateListener<String, TwitchEmotesManager.TwitchEmote>? = null,
        connectivityListener: ChatConnectivityListener? = null

    ) : PlayerData(dataListener, emoteStateListener, connectivityListener, title, channelDisplayName, category, imageUrl, viewerCount, player_type)

    object MixerPlayerType : PlayerData(null, null)

    enum class PlayerType {
        LIVE, VOD, CLIP
    }
}