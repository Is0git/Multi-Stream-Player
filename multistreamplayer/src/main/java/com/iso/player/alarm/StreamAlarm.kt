package com.iso.player.alarm

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.iso.player.R

class AlarmService : Service() {
    companion object {
        const val ALARM_CHANNEL_ID = "100"
    }
    override fun onBind(intent: Intent?): IBinder? {
       return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val title = intent?.extras?.getString("title", "valuie")
        val description = intent?.extras?.getString("description", "sd")
        Toast.makeText(applicationContext, "SUP", Toast.LENGTH_LONG).show()
        val notification = NotificationCompat.Builder(this, ALARM_CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.alarm_icon)
            .setContentText(description).setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(this).notify(101, notification)
        return super.onStartCommand(intent, flags, startId)
    }
}