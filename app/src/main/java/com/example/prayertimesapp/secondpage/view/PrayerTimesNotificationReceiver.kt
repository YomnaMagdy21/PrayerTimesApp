package com.example.prayertimesapp.secondpage.view

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PixelFormat
import android.media.RingtoneManager
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.prayertimesapp.R
import com.example.prayertimesapp.utility.SharedPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PrayerTimesNotificationReceiver : BroadcastReceiver() {

    lateinit var   btnCancel:Button
    lateinit var alarmWay :String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra("PRAYER_NAME") ?: return

        alarmWay= SharedPreference.getAlarm(context)


        // Call the reminderWay method in a coroutine
        GlobalScope.launch {
            if(alarmWay == "n"){
                createNotification(context,prayerName)
            }
            else{
                showReminder(context,prayerName)
            }
        }

        }



    // Start the SoundService
    private fun startSoundService(context: Context, soundUri: String) {
        val serviceIntent = Intent(context, SoundService::class.java).apply {
            putExtra("SOUND_URI", soundUri)
        }
        context.startService(serviceIntent)
    }

    // Create a pending intent to dismiss the notification
    private fun getDismissPendingIntent(context: Context, notificationId: Int): PendingIntent {
        val cancelIntent = Intent(context, NotificationCancelReceiver::class.java)
        cancelIntent.putExtra("notification_id", notificationId) // Pass the notification ID
        return PendingIntent.getBroadcast(
            context,
            notificationId,
            cancelIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }


@RequiresApi(Build.VERSION_CODES.O)
    suspend fun showReminder(context:Context,prayerName:String){

        // Check for the permission and request it if not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.packageName))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Add this flag
            context.startActivity(intent)
            return  // Return if the permission is not granted yet
        }

        val LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE
        val view = LayoutInflater.from(context).inflate(R.layout.reminder, null, false)
        var alarmMsg=view.findViewById(R.id.text_alarm_time)  as TextView
        alarmMsg.text="حان الان موعد اذان"+" "+prayerName
        btnCancel=view.findViewById(R.id.button_dismiss)  as Button
        btnCancel.setBackgroundColor(Color.rgb(138,199,219))
        btnCancel.shadowRadius



        val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
        val layoutParams =
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )

        setAlarmUI(layoutParams)

        val ringtoneUri = Uri.parse("android.resource://${context.packageName}/raw/adahan")
        val ringtone = RingtoneManager.getRingtone(context.applicationContext, ringtoneUri)

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val originalIntent = Intent(context, PrayerTimesNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, originalIntent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager?.cancel(pendingIntent)

        withContext(Dispatchers.Main) {
            windowManager.addView(view, layoutParams)
            view.visibility = View.VISIBLE
            ringtone.play()
            // messageTextView.text = message
        }
      //  SharedPreference.saveCancel(context,"not")
        btnCancel.setBackgroundResource(R.drawable.rounded_button)
        btnCancel.setOnClickListener {
            view.visibility=View.GONE
            ringtone.stop()
//            SharedPreference.saveCancel(context,"cancel")


        }


    }


    fun setAlarmUI(layoutParams:WindowManager.LayoutParams){
        layoutParams.alpha=0.9f
        layoutParams.horizontalWeight=50f

        layoutParams.windowAnimations=20

        layoutParams.width=750
        layoutParams.buttonBrightness=20f
        layoutParams.gravity= Gravity.CENTER_HORIZONTAL
        layoutParams.gravity = Gravity.TOP
    }

    fun createNotification(context: Context,prayerName: String){

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Define the sound URI
        val soundUri = "android.resource://${context.packageName}/raw/adahan"
        Log.d("NotificationSound", "Sound URI: $soundUri")

        // Start the SoundService to play the sound
        startSoundService(context, soundUri)

        // Create Notification Channel (for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val channel = NotificationChannel(
                "prayer_channel",
                "Prayer Times",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for prayer time notifications"
                setSound(Uri.parse(soundUri), audioAttributes) // Set custom sound for the channel
            }

            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = prayerName.hashCode()

        // Build the notification
        val notification = NotificationCompat.Builder(context, "prayer_channel")
            .setContentTitle("مواقيت الصلاه")
            .setContentText("حان الان موعد اذان $prayerName")
            .setSmallIcon(R.drawable.bell)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(Uri.parse(soundUri)) // Set custom sound for the notification
            .setDefaults(0) // Disable default system sounds
            .addAction(
                R.drawable.bell, // Custom icon for the action
                "ايقاف الاذان", // Action text
                getDismissPendingIntent(context, notificationId) // Dismiss action
            )
            .build()

        // Check for POST_NOTIFICATIONS permission (Android 13+)
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(
                "PrayerTimesNotificationReceiver",
                "Permission not granted for posting notifications."
            )
            return
        }

        // Show the notification
        NotificationManagerCompat.from(context).notify(notificationId, notification)

    }
}



class NotificationCancelReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("notification_id", 0) // Get notification ID
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.d("NotificationCancelReceiver", "Cancelling notification with ID: $notificationId")
        notificationManager.cancel(notificationId) // Cancel specific notification

        // Stop the SoundService
        stopSoundService(context)
    }

    // Stop the sound by stopping the SoundService
    private fun stopSoundService(context: Context) {
        val serviceIntent = Intent(context, SoundService::class.java)
        context.stopService(serviceIntent)
    }
}