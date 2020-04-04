package com.android.multistreamplayer.media_source

import android.net.Uri
import com.android.multistreamplayer.api.twitch.VideoService
import com.android.multistreamplayer.api.twitch.models.Token
import kotlinx.coroutines.*
import retrofit2.Retrofit

 class TwitchMediaSource(retrofit: Retrofit) : MediaSource<String>() {
    companion object {
        const val TWITCH_URL = "https://api.twitch.tv"
    }
    private val service: VideoService by lazy { retrofit.create(VideoService::class.java) }

    private suspend fun getAccessToken(channelKey: String?): Token {
        channelKey ?: throw CancellationException("channelKey wasnt provided")
        val url = "https://api.twitch.tv/api/channels/$channelKey/access_token"
        return withContext(Dispatchers.IO) {
            service.getAccessToken(url).run {
                when {
                    isSuccessful && body() != null -> body()!!
                    else -> throw CancellationException(message())
                }
            }
        }
    }

    override suspend fun getLiveStreamDataUrl(channelKey: String): Uri {
        getAccessToken(channelKey).also { token ->
            return withContext(Dispatchers.Default) {
                Uri.Builder()
                    .scheme("http")
                    .authority("usher.twitch.tv")
                    .encodedPath("api/channel/hls/$channelKey.m3u8")
                    .encodedQuery("token=${token.token}")
                    .appendQueryParameter("sig", token.sig)
                    .appendQueryParameter("player", "twitchweb")
                    .appendQueryParameter("allow_audio_only", "true")
                    .appendQueryParameter("allow_source", "true")
                    .appendQueryParameter("type", "any")
                    .appendQueryParameter("p", "9333029")
                    .build()
            }
        }
    }
}