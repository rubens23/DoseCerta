package com.rubens.applembretemedicamento.framework

import android.content.Context

object ApplicationContextProvider {
    private var applicationContext: Context? = null

    fun setApplicationContext(context: Context) {
        applicationContext = context.applicationContext
    }

    fun getApplicationContext(): Context {
        return applicationContext ?: throw IllegalStateException("Application context not set")
    }
}