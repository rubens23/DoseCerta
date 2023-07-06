package com.rubens.applembretemedicamento.framework.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmedicamentos.data.repository.AddMedicineRepositoryImpl
import com.example.appmedicamentos.data.repository.MedicationRepositoryImpl
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import com.rubens.applembretemedicamento.framework.domain.doses.DosesManagerFormato12Horas
import com.rubens.applembretemedicamento.framework.domain.doses.DosesManagerInterface
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelFragmentCadastrarNovoMedicamento @Inject constructor(
    private val repositoryAdicionarMedicamento: AddMedicineRepositoryImpl,
    private val medicationRepository: MedicationRepositoryImpl,
    private val dosesManagerInterface: DosesManagerInterface,
    private val dosesManagerFormato12Horas: DosesManagerFormato12Horas,
    private val calendarHelper: CalendarHelper,
    private val funcoesDeTempo: FuncoesDeTempo
): ViewModel() {


    var insertResponse: MutableLiveData<Long> = MutableLiveData()
    var medicamentos: MutableLiveData<List<MedicamentoComDoses>?> = MutableLiveData()

    private val _mostrarToastMedicamentoInseridoComSucesso: MutableSharedFlow<String> = MutableSharedFlow(replay = 0)
    val mostrarToastMedicamentoInseridoComSucesso: SharedFlow<String> = _mostrarToastMedicamentoInseridoComSucesso

    private val _podeFecharFragmento: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 0)
    val podeFecharFragmento: SharedFlow<Boolean> = _podeFecharFragmento

    private val _mostrarToastFalhaNaInsercaoDoMedicamento: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 0)
    val mostrarToastFalhaNaInsercaoDoMedicamento: SharedFlow<Boolean> = _mostrarToastFalhaNaInsercaoDoMedicamento

    private val _mostrarToastHoraJaPassou: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 0)
    val mostrarToastHoraJaPassou: SharedFlow<Boolean> = _mostrarToastHoraJaPassou

    private val _mostrarToastErroNoCadastroDoMedicamento: MutableSharedFlow<Boolean> = MutableSharedFlow(replay = 0)
    val mostrarToastErroNoCadastroDoMedicamento: SharedFlow<Boolean> = _mostrarToastErroNoCadastroDoMedicamento




    private lateinit var medicamento: MedicamentoTratamento
    lateinit var nomeRemedio: String
    var qntDoses: Int = 0
    lateinit var horarioPrimeiraDose: String
    var is24HourFormat: Boolean = false
    lateinit var qntDosesStr: String










    fun gerenciarHorariosDosagem(
        medicamento: MedicamentoTratamento,
        nomeMedicamento: String,
        qntDoses: Int,
        horarioPrimeiraDose: String,
        is24HourFormat: Boolean
    ) {

        if(is24HourFormat){
            dosesManagerInterface.gerenciarHorariosDosagem(medicamento, nomeMedicamento, qntDoses, horarioPrimeiraDose, repositoryAdicionarMedicamento, is24HourFormat)

        }else{
            dosesManagerFormato12Horas.pegarTodasAsDosesParaOMedicamento(medicamento.nomeMedicamento, medicamento.horaPrimeiraDose, medicamento.qntDoses, medicamento.totalDiasTratamento, medicamento.dataInicioTratamento)
        }

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

    fun seInsertBemSucedidoProsseguirComCadastroDasDoses(insertResponse: Long?) {
        if (insertResponse != null) {
            if (insertResponse > -1) {
                if(this::medicamento.isInitialized){
                    startMakingDosageTimes()
                    showToastMedicamentoInseridoComSucesso()
                    fecharFragmentAtual()
                }
            } else {
                showFailedInsertToast()

            }
        }

    }

    private fun fecharFragmentAtual() {
        viewModelScope.launch {
            _podeFecharFragmento.emit(true)
        }
    }

    private fun showFailedInsertToast() {
        viewModelScope.launch {
            _mostrarToastFalhaNaInsercaoDoMedicamento.emit(true)
        }
    }

    private fun showToastMedicamentoInseridoComSucesso() {
        viewModelScope.launch {
            _mostrarToastMedicamentoInseridoComSucesso.emit(nomeRemedio)

        }
    }

    private fun startMakingDosageTimes() {
        gerenciarHorariosDosagem(medicamento, nomeRemedio, qntDoses, horarioPrimeiraDose, is24HourFormat)
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

    fun pegarTodosOsMedicamentosComAlarmeTocando(): List<MedicamentoTratamento>? {
        return medicationRepository.getAllMedicamentosComAlarmeTocando()

    }

    fun seeIfMedicamentoHasValidInfo(
        nomeRemedio: String,
        qntDoses: Int,
        horarioPrimeiraDose: String,
        qntDiasTrat: Int?,
        diaInicioTratamento: String,
        inputDataInicioTratamentoIsDone: Boolean,
        dataInicioTratamentoMasked: String
    ) {
        var horaPrimeiraDoseLength = 0

        if(is24HourFormat){
            horaPrimeiraDoseLength = 5
        }else{
            horaPrimeiraDoseLength = 8
        }


        Log.d("colocandosuf", "${horarioPrimeiraDose.length}")
        if (diaInicioTratamento.length == 10 && diaInicioTratamento.isNotEmpty() && qntDoses > 0 && nomeRemedio.isNotEmpty() && horarioPrimeiraDose.isNotEmpty() && horarioPrimeiraDose.length == horaPrimeiraDoseLength && horarioPrimeiraDose[2].toString() == ":" && inputDataInicioTratamentoIsDone && qntDiasTrat != null
            && horarioPrimeiraDose.isNotEmpty() && horarioPrimeiraDose[2].toString() == ":") {
            if(!calendarHelper.verificarSeDataHoraJaPassou("$diaInicioTratamento $horarioPrimeiraDose", is24HourFormat)){



                saveNewMedication(nomeRemedio, qntDoses, horarioPrimeiraDose, qntDiasTrat, dataInicioTratamentoMasked)
            }else{
                mostraToastJaPassouHora()

            }



        } else {
            showErrorCadastratingNewMedicationToast()

        }

    }

    private fun showErrorCadastratingNewMedicationToast() {
        viewModelScope.launch {
            _mostrarToastErroNoCadastroDoMedicamento.emit(true)
        }
    }

    private fun mostraToastJaPassouHora() {
        viewModelScope.launch {
            _mostrarToastFalhaNaInsercaoDoMedicamento.emit(true)

        }
    }

    private fun saveNewMedication(
        nomeRemedio: String,
        qntDoses: Int,
        horarioPrimeiraDose: String,
        qntDiasTrat: Int,
        dataInicioTratamentoMasked: String
    ) {
        medicamento = MedicamentoTratamento(
            nomeMedicamento = nomeRemedio,
            totalDiasTratamento = qntDiasTrat,
            horaPrimeiraDose = horarioPrimeiraDose,
            qntDoses = qntDoses,
            tratamentoFinalizado = false,
            diasRestantesDeTratamento = qntDiasTrat,
            dataInicioTratamento = dataInicioTratamentoMasked,
            dataTerminoTratamento = funcoesDeTempo.pegarDataDeTermino(
                dataInicioTratamentoMasked,
                qntDiasTrat
            ),
            stringDataStore = "toast_already_shown"+"_$nomeRemedio"
        )
        insertMedicamento(
            medicamento
        )

    }


}