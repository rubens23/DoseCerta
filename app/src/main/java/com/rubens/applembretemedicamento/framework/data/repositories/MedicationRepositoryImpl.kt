package com.example.appmedicamentos.data.repository


import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.ConfiguracoesEntity
import com.rubens.applembretemedicamento.framework.data.entities.HistoricoMedicamentos
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import javax.inject.Inject

class MedicationRepositoryImpl @Inject constructor(
    private val medicamentoDao: MedicamentoDao,
){
    fun getMedicamentos(): List<MedicamentoComDoses>?{
        return medicamentoDao.getAllMedicamentoWithDoses()
    }

    fun getAllMedicamentosComAlarmeTocando(): List<MedicamentoTratamento>?{
        return medicamentoDao.getAllMedicamentosComAlarmeTocando()
    }

    fun alarmeMedicamentoTocando(id: Int, tocando: Boolean){
        return medicamentoDao.alarmeMedicamentoTocando(id, tocando)
    }

    fun insertMedicamento(medicamento: MedicamentoTratamento){
        //medicamentoDao.insertMedicamento(medicamento)
    }

    fun getMedicamentosFinalizados(): List<HistoricoMedicamentos>{
        return medicamentoDao.getTodosMedicamentosFinalizados()
    }



    fun desativarTodosOsAlarmes(){
        medicamentoDao.desativarTodosOsAlarmes()
    }

    fun pegarConfiguracoes(): ConfiguracoesEntity {
        return medicamentoDao.pegarConfiguracoes()
    }

    fun nuncaMaisMostrarDialogDeAvaliacao() {
        medicamentoDao.nuncaMaisMostrarDialogDeAvaliacao(false)
    }

}