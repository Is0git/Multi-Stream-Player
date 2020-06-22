package com.iso.player.playlist_source_creators

import android.net.Uri

interface DataDownloader<K> {
    suspend fun getDataFromUrl(channelKey: K) : Uri
}