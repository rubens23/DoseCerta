package com.rubens.applembretemedicamento.framework.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmedicamentos.data.repository.MedicationRepositoryImpl
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelFragmentLista @Inject constructor(
    private val medicationRepository: MedicationRepositoryImpl
): ViewModel(){

    var medicamentos: MutableLiveData<List<MedicamentoComDoses>?> = MutableLiveData()
    var recyclerViewPosition = 0

    private val _pegouConfiguracoesDeAvaliacao: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 0)
    val pegouConfiguracoesDeAvaliacao: SharedFlow<Boolean> = _pegouConfiguracoesDeAvaliacao

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

    fun podeMostrarDialogDeAvaliacao() {
        val configuracoes = medicationRepository.pegarConfiguracoes()
        viewModelScope.launch {
            _pegouConfiguracoesDeAvaliacao.emit(configuracoes.podeMostrarDialogAvaliacao)
        }

    }

    fun nuncaMaisMostrarDialogAvaliacao() {
        medicationRepository.nuncaMaisMostrarDialogDeAvaliacao()
    }


}