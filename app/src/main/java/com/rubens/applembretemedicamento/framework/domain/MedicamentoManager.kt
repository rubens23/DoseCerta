package com.rubens.applembretemedicamento.framework.domain

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiver
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiverInterface
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import com.rubens.applembretemedicamento.presentation.FragmentDetalhesMedicamentos
import com.rubens.applembretemedicamento.presentation.interfaces.FragmentDetalhesMedicamentosUi
import com.rubens.applembretemedicamento.utils.CalendarHelper
import java.io.Serializable

class MedicamentoManager : CalendarHelper, Serializable {
    private lateinit var context: FragmentDetalhesMedicamentos
    var nomeMedicamento = ""
    var horaProxDose: String? = null
    private lateinit var medicamento: MedicamentoTratamento
    private lateinit var receiver: AlarmReceiver
    private var _updateDataStore: MutableLiveData<String> = MutableLiveData()
    var updateDataStore = _updateDataStore
    private var _horaProximaDoseObserver: MutableLiveData<String> = MutableLiveData()
    var horaProximaDoseObserver = _horaProximaDoseObserver
    private var intervaloEntreDoses = 0.0
    private lateinit var fragmentDetalhesMedicamentosUi: FragmentDetalhesMedicamentosUi
    private lateinit var alarmReceiverInterface: AlarmReceiverInterface


    private lateinit var extra: Serializable


    private fun updateMedicamento(medicamento: MedicamentoTratamento) {
        this.medicamento = medicamento
        nomeMedicamento = medicamento.nomeMedicamento
    }

    fun getReceiver(): AlarmReceiver {
        return receiver
    }

    fun initializeExtra(extra: Serializable) {
        initExtra(extra)

    }

    fun getHrProxDose(): String? {
        return horaProxDose
    }

    private fun updateIntervaloEntreDoses(num: Double) {
        intervaloEntreDoses = num
    }

    fun startUpdateIntervaloEntreDoses(num: Double) {
        updateIntervaloEntreDoses(num)
    }

    fun checkIfReceiverIsInitialized(): Boolean {
        return this::receiver.isInitialized
    }

    fun startUpdateMedicamento(medicamento: MedicamentoTratamento) {
        updateMedicamento(medicamento)
    }

    fun getMedicamento(): MedicamentoTratamento {
        return medicamento
    }

    fun startArmarProximoAlarme() {
        armarProximoAlarme()
    }

    private fun initExtra(extra: Serializable) {
        this.extra = extra

    }

    private fun updateHoraProxDose(str: String) {
        horaProxDose = str
    }

    fun startUpdateHoraProxDose(str: String) {
        updateHoraProxDose(str)
    }

    private fun initializeAlarmManager() {
        initAlarmReceiver()
        colocaHoraProximaDoseNoObserver()

        var podeTocar = false

        horaProxDose?.let { hora ->
            Log.d("smartalarm", "hora iterada $hora")
            var hr = hora

            if (hora.length < 17) {
                hr = adicionarZeroZeroAoFinalDaHora(hora)
            }

            //converte a string para uma data, se essa data estiver no futuro, o alarme pode tocar
            //nesse horário
            convertStringToDate(hr)?.let {
                podeTocar = it.time >= System.currentTimeMillis()
            }


        }

        if (podeTocar) {
            //se pode tocar, seta o alarme
            chamarMetodoParaSetarOAlarmNoAlarmReceiver()
        } else {
            //se não pode tocar nesse horário
            //pega o outro horario, para ver
            //se dá para setar o alarme
            armarProximoAlarme()

        }


    }

    private fun chamarMetodoParaSetarOAlarmNoAlarmReceiver() {
        horaProxDose?.let {
            receiver.setAlarm2(
                intervaloEntreDoses,
                (extra as MedicamentoComDoses).medicamentoTratamento.idMedicamento,
                (extra as MedicamentoComDoses).listaDoses,
                context.requireContext(),
                it
            )
        }

        markToastAsShown()

    }

    private fun markToastAsShown() {
        _updateDataStore.value = medicamento.stringDataStore
    }

    private fun adicionarZeroZeroAoFinalDaHora(hora: String): String {
        return "$hora:00"

    }

    private fun colocaHoraProximaDoseNoObserver() {
        _horaProximaDoseObserver.value = horaProxDose
    }

    private fun armarProximoAlarme() {
        if ((extra as MedicamentoComDoses).listaDoses.size > 0) {
            val limiteFor = (extra as MedicamentoComDoses).listaDoses.size - 1
            iterarSobreDosesEAcharProxima(limiteFor)
        }

        markToastAsShown()
        initializeAlarmManager()
    }

    private fun iterarSobreDosesEAcharProxima(limiteFor: Int) {
        for (i in 0..limiteFor) {
            horaProxDose?.let { horaProxDose ->
                if (horaProxDose.length < 17) {
                    if ((extra as MedicamentoComDoses).listaDoses[i].horarioDose + ":00" == "$horaProxDose:00") {

                        if (i + 1 == limiteFor) {
                            //acabaram as doses
                        } else {
                            //pega horario da dose e concatena ":00" no final para a string ficar
                            //formato certo
                            this.horaProxDose =
                                (extra as MedicamentoComDoses).listaDoses[i + 1].horarioDose + ":00"
                            return
                        }


                    }
                } else {
                    //se hora proxDose for maior que 17 significa que já tem o sufixo no horaProxDose
                    if ((extra as MedicamentoComDoses).listaDoses[i].horarioDose + ":00" == horaProxDose) {
                        if (i + 1 == limiteFor) {
                            //doses acabaram
                        } else {
                            //coloca o sufixo pois ele não esta no valor salvo no banco
                            this.horaProxDose =
                                (extra as MedicamentoComDoses).listaDoses[i + 1].horarioDose + ":00"
                            return

                        }
                    }
                }
            }


        }


    }

    fun startAlarmManager(context: FragmentDetalhesMedicamentos) {
        this.context = context
        initFragmentDetalhesUiInterface()
        initAlarmReceiverInterface()
        initializeAlarmManager()
    }

    private fun initFragmentDetalhesUiInterface() {
        if (!this::fragmentDetalhesMedicamentosUi.isInitialized) {
            fragmentDetalhesMedicamentosUi = context as FragmentDetalhesMedicamentosUi
        }
    }

    fun startChecarSeAlarmeEstaAtivado(ctx: FragmentDetalhesMedicamentos) {
        this.context = ctx
        initFragmentDetalhesUiInterface()
        initAlarmReceiverInterface()
        //checarSeAlarmeEstaAtivado()
    }

    private fun initAlarmReceiverInterface() {
        if (!this::alarmReceiverInterface.isInitialized) {
            if (this::receiver.isInitialized) {
                alarmReceiverInterface = receiver as AlarmReceiverInterface

            } else {
                initAlarmReceiver()
                alarmReceiverInterface = receiver

            }
        }
    }

    private fun initAlarmReceiver() {
        receiver = AlarmReceiver()
    }

    private fun checarSeAlarmeEstaAtivado() {
        if (medicamento.alarmeAtivado) {
            if (alarmReceiverInterface.getMediaPlayerInstance().isPlaying) {
                alarmReceiverInterface.getListaIdMedicamentosTocandoNoMomentoFromAlarmReceiver()
                    .forEach {
                        if (it == medicamento.idMedicamento) {
                            fragmentDetalhesMedicamentosUi.showBtnPararSom()
                        }
                    }
            } else {
                Log.d("testeisplaying", "o mp nao esta tocando")
                initializeAlarmManager()
            }

        }

    }


}