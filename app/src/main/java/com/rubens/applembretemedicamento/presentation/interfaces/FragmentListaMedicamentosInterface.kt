package com.rubens.applembretemedicamento.presentation.interfaces

import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.domain.MedicamentoManager

interface FragmentListaMedicamentosInterface {
    fun initDb()
    fun getMedicamentoDao(): MedicamentoDao
    fun launchCoroutineScope(medicamento: MedicamentoComDoses)
    fun onMedicamentoClick(proxDose: String?, intervaloEntreDoses: Double, medicamento: MedicamentoComDoses, medicamentoManager: MedicamentoManager)


}