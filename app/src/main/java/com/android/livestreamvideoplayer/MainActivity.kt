package com.android.livestreamvideoplayer

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.android.livestreamvideoplayer.databinding.ActivityMainBinding
import com.android.multistreamchat.chat.chat_parser.ChatParser
import com.android.multistreamchat.chat.listeners.DataListener

import com.android.multistreamplayer.MultiStreamPlayerLayout
import com.android.multistreamplayer.api.twitch.VideoService
import com.android.multistreamplayer.chat.adapters.ChatAdapter
import com.android.multistreamplayer.chat.chat_factories.ChatType
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    var channelName = "cdnthe3rd"
    var chatAdapter: ChatAdapter? = null
    lateinit var binding: ViewDataBinding
    lateinit var videoService: VideoService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatAdapter = ChatAdapter(this)
        binding = if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) DataBindingUtil.setContentView(this, R.layout.activity_main) else DataBindingUtil.setContentView(this, R.layout.activity_main2)
        (binding.root as MultiStreamPlayerLayout).apply {
            chatAdapter = this@MainActivity.chatAdapter
            initAlarm(supportFragmentManager)
            val chatType = ChatType.TwitchChatType("is0xxx", "7uyg0kooxcagt096sig5f2i023mrdk", channelName, object :
                DataListener {
                override fun onReceive(message: ChatParser.Message) {
                    chatAdapter?.addLine(message)
                    chatList?.smoothScrollToPosition(chatAdapter?.itemCount?.minus(1) ?: 0)
                }

            }, null)
            init(chatType)
            registerLifeCycle(lifecycle)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
      binding =  if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)  DataBindingUtil.setContentView(this, R.layout.activity_main) else  DataBindingUtil.setContentView(this, R.layout.activity_main2)
    }
}
