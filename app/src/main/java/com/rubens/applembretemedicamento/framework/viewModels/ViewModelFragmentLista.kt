package com.rubens.applembretemedicamento.framework.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appmedicamentos.data.repository.MedicationRepositoryImpl
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import com.rubens.applembretemedicamento.framework.data.roomdatasourcemanager.DataSourceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewModelFragmentLista @Inject constructor(
    private val medicationRepository: MedicationRepositoryImpl,
    private val dataSourceManager: DataSourceManager
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
        //descomente a linha abaixo para testar a recycler view com dados fake na build variant debug
        //val list = dataSourceManager.getDataSource(medicationRepository)

        val list = medicationRepository.getMedicamentos()

        medicamentos.postValue(list)
    }

    fun alarmeMedicamentoTocando(id: Int, tocando: Boolean){
        medicationRepository.alarmeMedicamentoTocando(id, tocando)
    }

    fun insertMedicamentos(medicamento: MedicamentoTratamento){
        medicationRepository.insertMedicamento(medicamento)
    }




}