package com.android.multistreamplayer.media_source

import android.net.Uri

interface DataDownloader<K> {
    suspend fun getLiveStreamDataUrl(channelKey: K) : Uri
}