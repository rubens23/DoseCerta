package com.rubens.applembretemedicamento.framework.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appmedicamentos.data.repository.MedicationRepositoryImpl
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewModelFragmentLista @Inject constructor(
    private val medicationRepository: MedicationRepositoryImpl
): ViewModel(){

    lateinit var medicamentos: MutableLiveData<List<MedicamentoComDoses>>

    init{
        medicamentos = MutableLiveData()
        loadMedications()
    }


    fun loadMedications(){
        val list = medicationRepository.getMedicamentos()

        medicamentos.postValue(list)
    }

    fun insertMedicamentos(medicamento: MedicamentoTratamento){
        medicationRepository.insertMedicamento(medicamento)
    }




}