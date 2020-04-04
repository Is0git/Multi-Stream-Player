package com.android.multistreamplayer.source_downloader

interface TwitchSourceDownloader : SourceDownloader<String> {
    fun getAccessToken(channelName: String) : String
}