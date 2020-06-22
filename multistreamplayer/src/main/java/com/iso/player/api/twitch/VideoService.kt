package com.iso.player.api.twitch


import com.iso.player.api.twitch.models.Token
import retrofit2.Response
import retrofit2.http.*

interface VideoService {
    @Headers(
        "Client-Id: kimne78kx3ncx6brgo4mv6wki5h1ko",
        "Accept: application/vnd.twitchtv.v5+json"
    )
    @GET
    suspend fun getAccessToken(@Url url: String = "https://api.twitch.tv/api/channels/greekgodx/access_token"): Response<Token>

    @Headers(
        "Client-Id: kimne78kx3ncx6brgo4mv6wki5h1ko",
        "Accept: application/vnd.twitchtv.v5+json"
    )
    @GET
    @FormUrlEncoded
    suspend fun getM3u8(
        @Query("token") token: String,
        @Query("sig") sig: String,
        @Url url: String = "http://usher.twitch.tv/api/channel/hls/greekgodx.m3u8",
        @Query("player") player: String = "twitchweb",
        @Query("allow_audio_only") audio: Boolean = true,
        @Query("allow_source") source: Boolean = true,
        @Query("type") type: String = "any",
        @Query("p") p: Int = 9333029
    ): Response<Token>

    @GET("/api/vods/{videoId}/access_token")
    @Headers(
        "Client-Id: kimne78kx3ncx6brgo4mv6wki5h1ko",
        "Accept: application/vnd.twitchtv.v5+json"
    )
    suspend fun getVideoAuthToken(
        @Path("videoId") videoId: String?,
        @Query("need_https") needHttps: Boolean = true,
        @Query("platform") platform: String = "web",
        @Query("player_backend") playerBackend: String = "mediaplayer",
        @Query("player_type") siteMini: String = "site_mini"
    ): Response<Token>

    suspend fun getVodM3u8(
        @Query("token") token: String,
        @Query("sig") sig: String,
        @Url url: String = "http://usher.twitch.tv/api/channel/hls/greekgodx.m3u8",
        @Query("player") player: String = "twitchweb",
        @Query("allow_source") source: Boolean = true,
        @Query("p") p: Int = 9333029
    )
}