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
            setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.player_fragment_container, PlayerFragment()).commit()
    }



}
