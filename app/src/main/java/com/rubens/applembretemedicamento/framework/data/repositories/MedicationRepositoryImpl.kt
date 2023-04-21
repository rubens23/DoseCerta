package com.example.appmedicamentos.data.repository


import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.HistoricoMedicamentos
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import javax.inject.Inject

class MedicationRepositoryImpl @Inject constructor(
    private val medicamentoDao: MedicamentoDao,
){
    fun getMedicamentos(): List<MedicamentoComDoses>?{
        return medicamentoDao.getAllMedicamentoWithDoses()
    }

    fun insertMedicamento(medicamento: MedicamentoTratamento){
        //medicamentoDao.insertMedicamento(medicamento)
    }

    fun getMedicamentosFinalizados(): List<HistoricoMedicamentos>{
        return medicamentoDao.getTodosMedicamentosFinalizados()
    }

    // TODO: resolver o erro error: incompatible types: MedicamentoDaoTeste cannot be converted to MedicamentoDao
    //    return RoomModule_ProvideMedicamentosDaoFactory.provideMedicamentosDao(provideDBProvider.get());

}