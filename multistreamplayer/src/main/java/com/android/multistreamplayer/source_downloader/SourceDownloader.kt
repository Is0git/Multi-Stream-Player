package com.android.multistreamplayer.source_downloader

interface SourceDownloader<T> {
   fun getLiveStreamUrl(channelKey: T) : String
}