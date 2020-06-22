package com.iso.player.playlist_source_creators

import android.net.Uri
import com.iso.player.api.twitch.VideoService
import com.iso.player.api.twitch.models.Token
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

class TwitchLiveStreamUrlCreator(retrofit: Retrofit) : UrlCreator<Token, String>() {
    companion object {
        const val TWITCH_URL = "https://api.twitch.tv"
    }
    private val service: VideoService by lazy { retrofit.create(VideoService::class.java) }

    override suspend fun getAccessToken(channelKey: String?): Token? {
        super.getAccessToken(channelKey)
        val url = "https://api.twitch.tv/api/channels/$channelKey/access_token"
        return try {
            withContext(Dispatchers.IO) {
                service.getAccessToken(url).run {
                    when {
                        isSuccessful && body() != null -> body()!!
                        else -> throw CancellationException(message())
                    }
                }
            }
        } catch (ex: Exception) {
            null
        }
    }

    override suspend fun getDataFromUrl(channelKey: String): Uri {
        getAccessToken(channelKey).also { token ->
            return withContext(Dispatchers.Default) {
                Uri.Builder()
                    .scheme("http")
                    .authority("usher.twitch.tv")
                    .encodedPath("api/channel/hls/$channelKey.m3u8")
                    .encodedQuery("token=${token?.token}")
                    .appendQueryParameter("sig", token?.sig)
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