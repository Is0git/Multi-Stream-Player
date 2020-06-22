package com.iso.player.source_downloader

interface SourceDownloader<T> {
   fun getLiveStreamUrl(channelKey: T) : String
}