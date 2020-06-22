package com.iso.player.playlist_source_creators

import kotlinx.coroutines.CancellationException

abstract class UrlCreator<T, K> : DataDownloader<K> {

    var key: String? = null

    open suspend fun getAccessToken(channelKey: K?): T? {
        if (channelKey == null) throw CancellationException("Channel key wasn't provided")
        return null
    }
}
