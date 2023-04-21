package com.rubens.applembretemedicamento.framework.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appmedicamentos.data.repository.MedicationRepositoryImpl
import com.rubens.applembretemedicamento.framework.data.entities.HistoricoMedicamentos
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewModelFragmentHistoricoMedicamentos @Inject constructor(
    private val medicationRepository: MedicationRepositoryImpl
): ViewModel() {

    lateinit var medicamentos: MutableLiveData<List<HistoricoMedicamentos>>

    init {
        medicamentos = MutableLiveData()
    }

    fun carregarMedicamentosFinalizados(){
        val list = medicationRepository.getMedicamentosFinalizados()

        medicamentos.postValue(list)
    }
}