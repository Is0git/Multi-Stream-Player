package com.android.multistreamplayer.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.fragment.app.FragmentManager
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class Alarm(val context: Context, val supportFragmentManager: FragmentManager) {
    companion object {
        const val ALARM_ID = 101

        fun calculateAlarmTime(hours: Int, minutes: Int, seconds: Int): Long {

            return (hours * 3600000L) + (minutes * 60000L) + (seconds * 1000L) + System.currentTimeMillis()
        }
    }

    private val timeDialog: TimePickerDialog by lazy {
        TimePickerDialog.newInstance({ view, hourOfDay, minute, second ->
            calculateAlarmTime(
                hourOfDay,
                minute,
                second
            ).also { setAlarm(it) }
        }, true).apply {
            version = TimePickerDialog.Version.VERSION_2
        }
    }

    fun showDialog() {
        timeDialog.show(supportFragmentManager, "101")
    }

    var alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun setAlarm(time: Long) {
        createPendingIntent().also { pendingIntent ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
            }
        }

    }

    private fun createPendingIntent(): PendingIntent {
        return createIntent().run {
            PendingIntent.getService(context, ALARM_ID, this, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    private fun createIntent(): Intent {
        return Intent(context, AlarmService::class.java).apply {
            putExtra("title", "FINISHED")
            putExtra("description", "STOP WATHCING ALREADY, ITS BAD FOR YOU")
        }
    }
}