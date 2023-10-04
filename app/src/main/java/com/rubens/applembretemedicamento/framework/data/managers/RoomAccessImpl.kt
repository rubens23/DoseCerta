package com.rubens.applembretemedicamento.framework.data.managers

import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.AlarmEntity
import com.rubens.applembretemedicamento.framework.data.entities.BroadcastReceiverOnReceiveData
import com.rubens.applembretemedicamento.framework.data.entities.ConfiguracoesEntity
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import javax.inject.Inject

class RoomAccessImpl @Inject constructor(
    private val dao: MedicamentoDao
): RoomAccess {



    override fun putMedicamentoDataOnRoom(broadcastReceiverOnReceiveData: BroadcastReceiverOnReceiveData) {
        dao.insertMedicamentoBroadcastReceiver(broadcastReceiverOnReceiveData)
    }

    override fun getMedicamentosDataOnRoom(): List<BroadcastReceiverOnReceiveData> {
        return dao.getMedicamentosDataForBroadcastReceiver()
    }

    override fun putNewActiveAlarmOnRoom(alarmEntity: AlarmEntity) {
        dao.putNewActiveAlarmInRoom(alarmEntity)
    }

    override fun getAllActiveAlarms(): List<AlarmEntity> {
        return dao.getAllActiveAlarms()
    }

    override fun podeTocarDepoisQueReiniciar(): Boolean? {
        return dao.podeTocarDepoisDeReiniciar()?.podeTocarDepoisDeReiniciar
    }



    override fun colocarConfiguracoesAtualizadas(configuracoesEntity: ConfiguracoesEntity){
        dao.inserirConfiguracoes(configuracoesEntity)
    }

    override fun pegarConfiguracoes(): ConfiguracoesEntity {
        return dao.pegarConfiguracoes()
    }

    override fun atualizarDoseNaTabelaAlarms(listaDoses: ArrayList<Doses>, idAlarme: Int) {
        dao.atualizarDoseMudandoJaMostrouToast(listaDoses, idAlarme)
    }

    override fun verSeMedicamentoEstaComAlarmeAtivado(idMedicamento: Int): Boolean {
        return dao.verSeMedicamentoEstaComAlarmeAtivado(idMedicamento)
    }

    override fun getColorResource(): Int {
        return dao.pegarConfiguracoes().colorResource
    }

    override fun getMedicamentoComDoses(idMedicamento: Int): MedicamentoComDoses {
        return dao.getMedicamentoDosesById(idMedicamento)
    }


}