package com.android.livestreamvideoplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.livestreamvideoplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var channelName = "dakotaz"
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.motionLayout.setDefaultTransitionHandler(supportFragmentManager)
        supportFragmentManager.beginTransaction().replace(R.id.player_fragment, PlayerFragment(), "player_fragment").commit()

    }
}
