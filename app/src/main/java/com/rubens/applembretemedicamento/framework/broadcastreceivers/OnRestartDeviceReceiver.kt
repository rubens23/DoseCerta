package com.rubens.applembretemedicamento.framework.broadcastreceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.rubens.applembretemedicamento.framework.data.managers.RoomAccess
import com.rubens.applembretemedicamento.framework.helpers.AlarmHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnRestartDeviceReceiver: BroadcastReceiver() {

    @Inject
    lateinit var roomAccess: RoomAccess
    @Inject
    lateinit var alarmHelper: AlarmHelper

    override fun onReceive(context: Context?, intent: Intent?) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent?.action)){
            Log.d("reinicio", "o dispositivo foi reiniciado")
            val listaMedicamentosComAlarmeAtivo = roomAccess.getAllActiveAlarms()
            val podeTocarDepoisQueReiniciar = roomAccess.podeTocarDepoisQueReiniciar()
            if(podeTocarDepoisQueReiniciar){
                listaMedicamentosComAlarmeAtivo.forEach {
                    Log.d("reinicio", "alarme ativo para ${it.nomeMedicamento} proxima dose as ${it.horaProxDose} size da lista: ${listaMedicamentosComAlarmeAtivo.size}")
                    alarmHelper.initAlarmManager(context!!)
                    alarmHelper.initAlarmIntent(context)
                    alarmHelper.setAlarm2(it.intervaloEntreDoses, it.idMedicamento, context, it.listaDoses, null, null, it.horaProxDose, it.nomeMedicamento)

                }
            }

        }
    }
}