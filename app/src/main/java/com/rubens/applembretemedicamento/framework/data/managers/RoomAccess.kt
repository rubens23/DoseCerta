package com.rubens.applembretemedicamento.framework.data.managers

import com.rubens.applembretemedicamento.framework.data.entities.AlarmEntity
import com.rubens.applembretemedicamento.framework.data.entities.BroadcastReceiverOnReceiveData
import com.rubens.applembretemedicamento.framework.data.entities.ConfiguracoesEntity

interface RoomAccess {
    fun putMedicamentoDataOnRoom(broadcastReceiverOnReceiveData: BroadcastReceiverOnReceiveData)
    fun getMedicamentosDataOnRoom() : List<BroadcastReceiverOnReceiveData>
    fun putNewActiveAlarmOnRoom(alarmEntity: AlarmEntity)
    fun getAllActiveAlarms(): List<AlarmEntity>
    fun podeTocarDepoisQueReiniciar(): Boolean
    fun podeTocarComOAppFechado(): Boolean
    fun colocarConfiguracoesAtualizadas(configuracoesEntity: ConfiguracoesEntity)
    fun pegarConfiguracoes(): ConfiguracoesEntity?
}