package com.rubens.applembretemedicamento.framework.singletons

import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiver

object AlarmReceiverSingleton {
    private lateinit var receiver: AlarmReceiver

    fun getInstance(): AlarmReceiver{
        if(!::receiver.isInitialized){
            receiver = AlarmReceiver()
        }
        return receiver
    }
}