package com.android.livestreamvideoplayer

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.android.livestreamvideoplayer.databinding.ActivityMainBinding
import com.android.livestreamvideoplayer.retrofit.VideoService
import com.android.multistreamplayer.MultiStreamPlayerLayout
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var videoService: VideoService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initService()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        (binding.root as MultiStreamPlayerLayout)

        getUrl()

    }

    fun initService() {
        videoService = com.android.livestreamvideoplayer.retrofit.Retrofit.getRetrofit("https://api.twitch.tv").create(VideoService::class.java)
    }

    fun getUrl()  {
        lifecycleScope.launch {
            val response = videoService.getAccessToken()
            Log.d("PALYER", "${response.body()?.token}")
            response.body()?.token.let { it?.replace("%", "") }
            val uri = Uri.Builder()
                .scheme("http")
                .authority("usher.twitch.tv")
                .encodedPath("api/channel/hls/nickeh30.m3u8")
                .encodedQuery("token=${response.body()?.token}" )
                .appendQueryParameter("sig", response.body()?.sig)
                .appendQueryParameter("player", "twitchweb")
                .appendQueryParameter("allow_audio_only", "true")
                .appendQueryParameter("allow_source", "true")
                .appendQueryParameter("type", "any")
                .appendQueryParameter("p", "9333029")
                .build()
            Log.d("PALYER", "$uri")
            (binding.root as MultiStreamPlayerLayout).play(uri.toString())
        }
    }
}
