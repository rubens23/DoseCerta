package com.rubens.applembretemedicamento.framework.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appmedicamentos.data.repository.AddMedicineRepositoryImpl
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddMedicineViewModel @Inject constructor(
    private val addMedicineRepository: AddMedicineRepositoryImpl
): ViewModel(){

    //qual que vai ser a resposta do metodo que eu vou utilizar no repository?
    //eu quero pegar a lista ou só o resposta de se o insert foi bem sucedido
    //ou não?
    var insertResponse: MutableLiveData<Long> = MutableLiveData()
    var insertDosesResponse: MutableLiveData<Long> = MutableLiveData()

    fun insertMedicamento(medicamento: MedicamentoTratamento){
        val insertResponseLong = addMedicineRepository.insertMedicamento(medicamento)

        insertResponse.postValue(insertResponseLong)
    }
    fun insertDose(doses: Doses){
        val insertDosesResponseLong = addMedicineRepository.insertDoses(doses)

        insertDosesResponse.postValue(insertDosesResponseLong)
    }

    fun ligarAlarmeDoMedicamento(nomeMedicamento: String, ativado: Boolean){
        addMedicineRepository.ligarAlarmeDoMedicamento(nomeMedicamento, ativado)
    }

    //todo colocar um observer no insertResponse

}