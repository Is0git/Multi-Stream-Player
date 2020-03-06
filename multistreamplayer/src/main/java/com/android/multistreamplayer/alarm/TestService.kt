package com.android.multistreamplayer.alarm

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.android.multistreamplayer.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AlarmService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
       return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val title = intent?.extras?.getString("title")
        val description = intent?.extras?.getString("description")
        Toast.makeText(applicationContext, "SUP", Toast.LENGTH_LONG).show()
        Log.d("TEST", "SSDS")
        return super.onStartCommand(intent, flags, startId)
    }

}