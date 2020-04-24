package com.android.livestreamvideoplayer

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.android.multistreamplayer.MultiStreamPlayerLayout
import com.android.multistreamplayer.chat.chat_factories.PlayerType

class MainActivity : AppCompatActivity() {
    private var channelName = "nickeh30"
    lateinit var binding: ViewDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) DataBindingUtil.setContentView(this, R.layout.player_layout) else DataBindingUtil.setContentView(this, R.layout.player_layout_land)
        (binding.root as MultiStreamPlayerLayout).apply {
            val playerType = PlayerType.TwitchChatType("wicked", "7uyg0kooxcagt096sig5f2i023mrdk", channelName)
            initializePlayer(playerType)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        (binding.root as MultiStreamPlayerLayout).onRotation(newConfig)
    }

}
