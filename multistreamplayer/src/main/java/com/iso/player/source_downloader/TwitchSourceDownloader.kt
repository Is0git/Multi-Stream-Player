package com.iso.player.source_downloader

interface TwitchSourceDownloader : SourceDownloader<String> {
    fun getAccessToken(channelName: String) : String
}