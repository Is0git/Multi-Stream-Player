package com.android.livestreamvideoplayer

import android.app.AlarmManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.android.livestreamvideoplayer.databinding.ActivityMainBinding
import com.android.livestreamvideoplayer.retrofit.VideoService
import com.android.multistreamchat.chat.Chat
import com.android.multistreamchat.chat.chat_parser.ChatParser
import com.android.multistreamchat.chat.listeners.DataListener
import com.android.multistreamchat.chat.listeners.EmoteStateListener
import com.android.multistreamchat.twitch_chat.chat_emotes.TwitchEmotesManager
import com.android.multistreamchat.twitch_chat.chat_parser.TwitchChatParser
import com.android.multistreamplayer.MultiStreamPlayerLayout
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.http.Url

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var videoService: VideoService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initService()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        (binding.root as MultiStreamPlayerLayout).apply {
            initAlarm(supportFragmentManager)
            registerLifeCycle(lifecycle)
        }
        getUrl("greekgodx")

    }

    private fun initService() {
        videoService = com.android.livestreamvideoplayer.retrofit.Retrofit.getRetrofit("https://api.twitch.tv").create(VideoService::class.java)
    }

    private fun getUrl(channelName: String)  {
        lifecycleScope.launch {
            val response = videoService.getAccessToken("https://api.twitch.tv/api/channels/$channelName/access_token")
            response.body()?.token.let { it?.replace("%", "") }
            val uri = Uri.Builder()
                .scheme("http")
                .authority("usher.twitch.tv")
                .encodedPath("api/channel/hls/$channelName.m3u8")
                .encodedQuery("token=${response.body()?.token}" )
                .appendQueryParameter("sig", response.body()?.sig)
                .appendQueryParameter("player", "twitchweb")
                .appendQueryParameter("allow_audio_only", "true")
                .appendQueryParameter("allow_source", "true")
                .appendQueryParameter("type", "any")
                .appendQueryParameter("p", "9333029")
                .build()
            (binding.root as MultiStreamPlayerLayout).play(uri.toString())
        }
    }

}
