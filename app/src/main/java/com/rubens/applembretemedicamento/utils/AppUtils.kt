package com.rubens.applembretemedicamento.utils

import android.app.ActivityManager
import android.content.Context

object AppUtils {
    fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        if (activityManager != null) {
            val packageName = context.packageName
            // Obtém a lista de processos em execução no momento
            val appProcesses = activityManager.runningAppProcesses
            if (appProcesses != null) {
                // Verifica se algum dos processos em execução pertence ao seu pacote
                for (processInfo in appProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        && processInfo.processName == packageName) {
                        return true
                    }
                }
            }
        }
        return false
    }
}




