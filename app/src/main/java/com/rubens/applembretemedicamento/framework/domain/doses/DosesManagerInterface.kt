package com.rubens.applembretemedicamento.framework.domain.doses

import androidx.lifecycle.MutableLiveData
import com.example.appmedicamentos.data.repository.AddMedicineRepositoryImpl
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento

interface DosesManagerInterface {

    var insertDosesResponse: MutableLiveData<Long>


    fun gerenciarHorariosDosagem(
        medicamento: MedicamentoTratamento,
        nomeMedicamento: String,
        qntDoses: Int,
        horarioPrimeiraDose: String,
        repositoryAdicionarMedicamento: AddMedicineRepositoryImpl,
        is24HourFormat: Boolean,
        defaultDateFormat: String
    )

}