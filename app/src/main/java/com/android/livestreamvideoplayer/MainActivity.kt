package com.android.livestreamvideoplayer

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.livestreamvideoplayer.databinding.ActivityMainBinding
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsDataSourceFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
            .also {
                binding.player.apply {
                    player = it
                    player.playWhenReady = true

                }
            }
        player.prepare(buildMediaSource())


    }
    private fun buildMediaSource(): HlsMediaSource {
        val dataSourceFactory =
            DefaultDataSourceFactory(this, "app_name")
        return HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(RawResourceDataSource.buildRawResourceUri(R.raw.trainwreckstv))
    }
}
