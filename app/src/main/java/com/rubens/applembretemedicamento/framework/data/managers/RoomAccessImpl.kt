package com.rubens.applembretemedicamento.framework.data.managers

import android.content.Context
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.rubens.applembretemedicamento.framework.data.AppDatabase
import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.data.entities.AlarmEntity
import com.rubens.applembretemedicamento.framework.data.entities.BroadcastReceiverOnReceiveData
import com.rubens.applembretemedicamento.framework.data.entities.ConfiguracoesEntity
import dagger.hilt.android.AndroidEntryPoint
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

    override fun podeTocarDepoisQueReiniciar(): Boolean {
        return dao.podeTocarDepoisDeReiniciar().podeTocarDepoisDeReiniciar
    }

    override fun podeTocarComOAppFechado(): Boolean{
        return dao.podeTocarDepoisDeReiniciar().podeTocarQuandoFechado
    }

    override fun colocarConfiguracoesAtualizadas(configuracoesEntity: ConfiguracoesEntity){
        dao.inserirConfiguracoes(configuracoesEntity)
    }

    override fun pegarConfiguracoes(): ConfiguracoesEntity {
        return dao.pegarConfiguracoes()
    }


}