package com.example.appmedicamentos.data.repository

import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import javax.inject.Inject

class AddMedicineRepositoryImpl @Inject constructor(
    private val medicamentoDao: MedicamentoDao
) {
    fun insertMedicamento(medicamento: MedicamentoTratamento): Long{
        return medicamentoDao.insertMedicamento(medicamento)

    }

    fun insertDoses(doses: Doses): Long{
        return medicamentoDao.insertDose(doses)
    }

    fun ligarAlarmeDoMedicamento(nomeMedicamento: String, ativado: Boolean) {
        medicamentoDao.ligarAlarmeDoMedicamento(nomeMedicamento, ativado)

    }


}