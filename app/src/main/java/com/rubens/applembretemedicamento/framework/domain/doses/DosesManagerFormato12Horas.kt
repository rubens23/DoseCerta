package com.rubens.applembretemedicamento.framework.domain.doses

import androidx.lifecycle.MutableLiveData
import com.example.appmedicamentos.data.repository.AddMedicineRepositoryImpl
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento

interface DosesManagerFormato12Horas {





    fun pegarTodasAsDosesParaOMedicamento(
        nomeMedicamento: String,
        horaPrimeiraDose: String,
        qntDosesPorDia: Int,
        totalDeDiasDeTratamento: Int,
        diaInicioTratamento: String,
        defaultDateFormat: String
    )
}