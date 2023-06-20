package com.rubens.applembretemedicamento.framework.singletons

import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiver
import com.rubens.applembretemedicamento.framework.data.managers.RoomAccessImpl


object AlarmReceiverSingleton {
    //todo entender e arrumar esse memory leak
    private lateinit var receiver: AlarmReceiver

/*
    fun getInstance(): AlarmReceiver{
        if(!::receiver.isInitialized){
            receiver = AlarmReceiver(RoomAccessImpl())
        }
        return receiver
    }

 */
}