package com.rubens.applembretemedicamento.framework.data.managers

import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.AlarmEntity
import com.rubens.applembretemedicamento.framework.data.entities.BroadcastReceiverOnReceiveData
import com.rubens.applembretemedicamento.framework.data.entities.ConfiguracoesEntity
import com.rubens.applembretemedicamento.framework.data.entities.Doses

interface RoomAccess {
    fun putMedicamentoDataOnRoom(broadcastReceiverOnReceiveData: BroadcastReceiverOnReceiveData)
    fun getMedicamentosDataOnRoom() : List<BroadcastReceiverOnReceiveData>
    fun putNewActiveAlarmOnRoom(alarmEntity: AlarmEntity)
    fun getAllActiveAlarms(): List<AlarmEntity>
    fun podeTocarDepoisQueReiniciar(): Boolean?
    fun colocarConfiguracoesAtualizadas(configuracoesEntity: ConfiguracoesEntity)
    fun pegarConfiguracoes(): ConfiguracoesEntity?
    fun atualizarDoseNaTabelaAlarms(listaDoses: ArrayList<Doses>, idAlarme: Int)
    fun verSeMedicamentoEstaComAlarmeAtivado(idMedicamento: Int): Boolean
    fun getColorResource(): Int

    fun getMedicamentoComDoses(idMedicamento: Int): MedicamentoComDoses
}