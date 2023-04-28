package com.rubens.applembretemedicamento.framework.services

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.rubens.applembretemedicamento.presentation.MainActivity

class ClosingAppServiceService: Service() {
    private var myActivity: MainActivity? = null


    override fun onBind(intent: Intent?): IBinder? {
        return MyBinder()
    }

    override fun onCreate() {
        super.onCreate()

        val intent = Intent(this, MainActivity::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        myActivity?.codigoASerExecutadoAoFecharOApp()

        Log.d("testeonclose", "eu to aqui no onTaskRemoved")
    }

    override fun onDestroy() {
        super.onDestroy()

        unbindService(connection)
    }

    inner class MyBinder: Binder(){
        fun getService(): ClosingAppServiceService = this@ClosingAppServiceService
    }

    private val connection = object :ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MainActivity.MyBinder
            myActivity = binder.getActivity()
            myActivity?.codigoASerExecutadoAoFecharOApp()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            myActivity = null
        }

    }



}