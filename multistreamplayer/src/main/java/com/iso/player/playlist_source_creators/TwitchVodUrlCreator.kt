package com.iso.player.playlist_source_creators

import android.net.Uri
import com.iso.player.api.twitch.VideoService
import com.iso.player.api.twitch.models.Token
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

class TwitchVodUrlCreator(retrofit: Retrofit) : UrlCreator<Token, String>(){

    private val service: VideoService by lazy { retrofit.create(VideoService::class.java) }

    override suspend fun getAccessToken(key: String?): Token? {
        super.getAccessToken(key)
        return try {
         service.getVideoAuthToken(key).body()
        } catch (ex: Exception) {
            null
        }
    }

    override suspend fun getDataFromUrl(channelKey: String): Uri {
     return  getAccessToken(channelKey)?.let { token ->
           withContext(Dispatchers.Default) {
               Uri.Builder()
                   .scheme("http")
                   .authority("usher.ttvnw.net")
                   .encodedPath("/vod/${channelKey}.m3u8")
                   .encodedQuery("token=${token.token}")
                   .appendQueryParameter("sig", token.sig)
                   .appendQueryParameter("player", "twitchweb")
                   .appendQueryParameter("player_backend", "mediaplayer")
                   .appendQueryParameter("playlist_include_framerate", "true")
                   .appendQueryParameter("reassignments_supported", "true")
                   .appendQueryParameter("cdm", "wv")
                   .appendQueryParameter("allow_source", "true")
                   .appendQueryParameter("p", "7973488")
                   .build()
            }
        } ?: throw CancellationException("null uri")
    }

}