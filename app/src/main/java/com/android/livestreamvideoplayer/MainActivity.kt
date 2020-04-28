package com.android.livestreamvideoplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding

class MainActivity : AppCompatActivity() {
    private var channelName = "dakotaz"
    lateinit var binding: ViewDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.player_fragment, PlayerFragment()).commit()
    }



}
