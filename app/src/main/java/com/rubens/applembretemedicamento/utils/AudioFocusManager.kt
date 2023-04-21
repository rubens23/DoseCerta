package com.example.appmedicamentos.utils

import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build


class AudioFocusManager: AudioManager.OnAudioFocusChangeListener {

    var context: Context? = null
    private var audioFocusRequest: AudioFocusRequest? = null

    fun AudioFocusManager(context: Context?) {
        this.context = context
    }

    fun requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && audioFocusRequest != null) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                .build()
            getAudioManager().requestAudioFocus(audioFocusRequest!!)
        } else {
            getAudioManager().requestAudioFocus(
                null,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
            )
        }
    }

    fun releaseAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (audioFocusRequest != null) {
                getAudioManager().abandonAudioFocusRequest(audioFocusRequest!!)
                audioFocusRequest = null
            }
        } else {
            getAudioManager().abandonAudioFocus(null)
        }
    }

    private fun getAudioManager(): AudioManager {
        return context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun onAudioFocusChange(p0: Int) {
        TODO("Not yet implemented")
    }
}