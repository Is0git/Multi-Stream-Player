package com.iso.player.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.content.res.ResourcesCompat
import com.iso.player.R
import net.steamcrafted.lineartimepicker.dialog.LinearTimePickerDialog
import java.util.*

class Alarm(val context: Context) {
    companion object {
        const val ALARM_ID = 101
        const val MINUTE = 60000
        const val HOUR = 60 * MINUTE
    }

    var linearTimePickerDialog: LinearTimePickerDialog =
        LinearTimePickerDialog.Builder.with(context)
            .setTextBackgroundColor(ResourcesCompat.getColor(context.resources, R.color.colorOnSecondaryVariant, null))
            .setDialogBackgroundColor(ResourcesCompat.getColor(context.resources, R.color.colorSurface, null))
            .setPickerBackgroundColor(ResourcesCompat.getColor(context.resources, R.color.chatColor, null))
            .setTextColor(Color.WHITE)
            .setButtonCallback(object : LinearTimePickerDialog.ButtonCallback {
                override fun onPositive(dialog: DialogInterface?, hour: Int, minutes: Int) {
                    setAlarm(hour, minutes)
                }

                override fun onNegative(dialog: DialogInterface?) {

                }

            })
            .build()
    var alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun showDialog() {
        linearTimePickerDialog.show()
    }

    private fun setAlarm(hours: Int, minutes: Int) {
        val timeOfAlarm = getTime(hours, minutes)
        createPendingIntent().also { pendingIntent ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,  timeOfAlarm, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeOfAlarm, pendingIntent)
            }
        }
    }

    private fun getTime(hour: Int, minutes: Int): Long {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinutes = calendar.get(Calendar.MINUTE)
        val isToday: Boolean = when  {
            currentHour == hour -> currentMinutes < minutes
            currentHour > hour -> false
            else -> true
        }
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minutes)
            if (!isToday) {
                add(Calendar.DATE, 1)
            }
        }
        return calendar.timeInMillis
    }


    private fun createPendingIntent(): PendingIntent {
        return createIntent().run {
            PendingIntent.getService(context, ALARM_ID, this, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    private fun createIntent(): Intent {
        return Intent(context, AlarmService::class.java).apply {
            putExtra("title", "Time is up")
            putExtra("description", "the time you set has expired")
        }
    }
}