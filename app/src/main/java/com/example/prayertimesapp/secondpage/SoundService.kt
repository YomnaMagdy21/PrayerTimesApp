package com.example.prayertimesapp.secondpage.view

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import android.net.Uri

class SoundService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val soundUri = intent.getStringExtra("SOUND_URI") ?: return START_NOT_STICKY

        try {
            mediaPlayer = MediaPlayer.create(this, Uri.parse(soundUri))
            mediaPlayer?.start()
        } catch (e: Exception) {
            Log.e("SoundService", "Error playing sound: ${e.message}")
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
