package com.example.appmedicamentos.utils

import android.content.Context
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.view.WindowManager


object WakeLocker {
    private var wakeLock: WakeLock? = null
    fun acquire(ctx: Context) {
        wakeLock?.release()
        val pm = ctx.getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    PowerManager.ON_AFTER_RELEASE, "myapp:com.example.appmedicamentos"
        )
        wakeLock?.acquire(10*60*1000L /*10 minutes*/)
    }

    fun release() {
        wakeLock?.release()
        wakeLock = null
    }
}