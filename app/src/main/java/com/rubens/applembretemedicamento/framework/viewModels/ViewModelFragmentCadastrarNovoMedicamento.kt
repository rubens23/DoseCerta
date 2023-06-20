package com.rubens.applembretemedicamento.framework.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmedicamentos.data.repository.AddMedicineRepositoryImpl
import com.example.appmedicamentos.data.repository.MedicationRepositoryImpl
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import com.rubens.applembretemedicamento.framework.domain.doses.DosesManagerInterface
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ViewModelFragmentCadastrarNovoMedicamento @Inject constructor(
    private val repositoryAdicionarMedicamento: AddMedicineRepositoryImpl,
    private val medicationRepository: MedicationRepositoryImpl,
    private val dosesManagerInterface: DosesManagerInterface,
    private val funcoesDeTempo: FuncoesDeTempo,
    private val calendarHelper: CalendarHelper
): ViewModel() {


    var insertResponse: MutableLiveData<Long> = MutableLiveData()
    var medicamentos: MutableLiveData<List<MedicamentoComDoses>?> = MutableLiveData()




    fun gerenciarHorariosDosagem(
        medicamento: MedicamentoTratamento,
        nomeMedicamento: String,
        qntDoses: Int,
        horarioPrimeiraDose: String
    ) {

        dosesManagerInterface.gerenciarHorariosDosagem(medicamento, nomeMedicamento, qntDoses, horarioPrimeiraDose, repositoryAdicionarMedicamento)

        //initInsertDosesListener()


    }

    private fun initInsertDosesListener() {
        /*
        viewModelScope.launch {
            dosesManagerInterface.insertDosesResponse.asFlow().collect{
                insertResponse.postValue(it)
            }
        }

         */





    }


    fun loadMedications(){
        val list = medicationRepository.getMedicamentos()

        medicamentos.postValue(list)
    }



    fun insertMedicamento(medicamento: MedicamentoTratamento){
        val insertResponseLong = repositoryAdicionarMedicamento.insertMedicamento(medicamento)

        insertResponse.postValue(insertResponseLong)
    }


    fun ligarAlarmeDoMedicamento(nomeMedicamento: String, ativado: Boolean){
        repositoryAdicionarMedicamento.ligarAlarmeDoMedicamento(nomeMedicamento, ativado)
    }


























}