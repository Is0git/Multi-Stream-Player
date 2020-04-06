package com.android.livestreamvideoplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.livestreamvideoplayer.databinding.ActivityMainBinding
import com.android.multistreamchat.chat.chat_parser.ChatParser
import com.android.multistreamchat.chat.listeners.DataListener

import com.android.multistreamplayer.MultiStreamPlayerLayout
import com.android.multistreamplayer.api.twitch.VideoService
import com.android.multistreamplayer.chat.adapters.ChatAdapter
import com.android.multistreamplayer.chat.chat_factories.ChatType

class MainActivity : AppCompatActivity() {
    var channelName = "highdistortion"
    var chatAdapter: ChatAdapter? = null
    lateinit var binding: ActivityMainBinding
    lateinit var videoService: VideoService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatAdapter = ChatAdapter(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        (binding.root as MultiStreamPlayerLayout).apply {
            chatAdapter = this@MainActivity.chatAdapter
            initAlarm(supportFragmentManager)
            val chatType = ChatType.TwitchChatType("is0xxx", "7uyg0kooxcagt096sig5f2i023mrdk", channelName, object :
                DataListener {
                override fun onReceive(message: ChatParser.Message) {
                    chatAdapter?.addLine(message)
                }

            }, null)
            init(chatType)
            registerLifeCycle(lifecycle)
        }
    }
}
