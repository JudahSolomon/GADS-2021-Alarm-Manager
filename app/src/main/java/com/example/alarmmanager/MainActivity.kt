package com.example.alarmmanager

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.alarmmanager.databinding.ActivityMainBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.sql.Time
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var picker: MaterialTimePicker
    private lateinit var calender: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        calender = Calendar.getInstance()

        createNotificationChannel()

        binding.btnSelectTime.setOnClickListener {
            showTimePicker()
        }

        binding.btnSetAlarm.setOnClickListener {
            setAlarm()
        }

        binding.btnCancelAlarm.setOnClickListener {
            cancelAlarm()
        }

    }

    private fun cancelAlarm() {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        alarmManager.cancel(pendingIntent)
        Toast.makeText(this, "Alarm cancelled", Toast.LENGTH_LONG).show()
    }

    private fun setAlarm() {

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        alarmManager.setExact(
            //real time clock and should wakeup the device
            AlarmManager.RTC_WAKEUP,
            calender.timeInMillis,
            //repeat interval for the setRepeating
//            AlarmManager.INTERVAL_DAY,
            pendingIntent)

        Toast.makeText(this, "Alarm set successfully", Toast.LENGTH_SHORT).show()
        
    }

    private fun showTimePicker() {

        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Alarm Time")
            .build()
        picker.show(supportFragmentManager, CHANNEL_ID)

        picker.addOnPositiveButtonClickListener {

            binding.tVTime.text = when {
                picker.hour > 12 -> getString(R.string.time_format, (picker.hour - 12), picker.minute, " PM")
                //                    String.format("%2d", picker.hour - 12) + ":" + String.format("%2d", picker.minute) + " PM"
                picker.hour == 12 -> getString(R.string.time_format, (picker.hour), picker.minute, " PM")
                else -> getString(R.string.time_format, picker.hour, picker.minute, " AM")
            }

            calender = Calendar.getInstance().also {
                it[Calendar.HOUR_OF_DAY] = picker.hour
                it[Calendar.MINUTE] = picker.minute
                it[Calendar.SECOND] = 0
                it[Calendar.MILLISECOND] = 0
            }

        }

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alarms"
            val descriptionText = "Set alarms and get notified on important things..."
            val importance = NotificationManager.IMPORTANCE_HIGH

            val notificationChannel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                shouldVibrate()
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}