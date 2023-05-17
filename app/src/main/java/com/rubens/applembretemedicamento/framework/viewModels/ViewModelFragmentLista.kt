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

    var medicamentos: MutableLiveData<List<MedicamentoComDoses>?> = MutableLiveData()
    var recyclerViewPosition = 0

    init{
        loadMedications()
    }

    fun onRecyclerViewScrolled(position: Int){
        recyclerViewPosition = position
    }


    fun loadMedications(){
        val list = medicationRepository.getMedicamentos()

        medicamentos.postValue(list)
    }

    fun insertMedicamentos(medicamento: MedicamentoTratamento){
        medicationRepository.insertMedicamento(medicamento)
    }




}