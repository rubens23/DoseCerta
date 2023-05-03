package com.rubens.applembretemedicamento.framework.domain

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiver
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import com.rubens.applembretemedicamento.presentation.interfaces.FragmentDetalhesMedicamentosUi
import com.rubens.applembretemedicamento.utils.CalendarHelper
import java.io.Serializable

class MedicamentoManager: CalendarHelper, Serializable {
    private lateinit var context: Context
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


    private lateinit var extra: Serializable


    private fun updateMedicamento(medicamento: MedicamentoTratamento){
        this.medicamento = medicamento
        nomeMedicamento = medicamento.nomeMedicamento
    }

    fun getReceiver(): AlarmReceiver{
        return receiver
    }

    fun initializeExtra(extra: Serializable){
        initExtra(extra)

    }

    fun getHrProxDose(): String?{
        return horaProxDose
    }

    private fun updateIntervaloEntreDoses(num: Double){
        intervaloEntreDoses = num
    }

    fun startUpdateIntervaloEntreDoses(num: Double){
        updateIntervaloEntreDoses(num)
    }

    fun checkIfReceiverIsInitialized(): Boolean{
        return this::receiver.isInitialized
    }

    fun startUpdateMedicamento(medicamento: MedicamentoTratamento){
        updateMedicamento(medicamento)
    }

    fun getMedicamento(): MedicamentoTratamento{
        return medicamento
    }

    fun startArmarProximoAlarme(){
        armarProximoAlarme()
    }

    private fun initExtra(extra: Serializable) {
        this.extra = extra

    }

    private fun updateHoraProxDose(str: String){
        horaProxDose = str
    }

    fun startUpdateHoraProxDose(str: String){
        updateHoraProxDose(str)
    }

    private fun initializeAlarmManager() {



        if(AlarmReceiver.nomeMedicamento != ""){
            Log.d("alarmessimulta", "ja armei um alarme anteriormente")
        }else{
            Log.d("alarmessimulta", "ainda não armei nenhum alarme")
        }



        receiver = AlarmReceiver()


        _horaProximaDoseObserver.value = horaProxDose



        var podeTocar = false

        horaProxDose?.let {
                hora->
            Log.d("smartalarm", "hora iterada $hora")
            var hr = hora

            //todo consertar isso. A proxima dose esta sendo as 5:10 ao inves das 23:10
            if(hora.length < 17){
                hr = hora+":00"
            }

            convertStringToDate(hr)?.let {
                Log.d("smartalarm", "data em millisegundos: $hora                       ${it.time}")
                Log.d("smartalarm", "current time em millis: ${System.currentTimeMillis()}")
                if(it.time >= System.currentTimeMillis()){
                    Log.d("smartalarm2", "essa dose ainda nao passou entao pode tocar: $hr")
                    podeTocar = true
                }else{
                    Log.d("smartalarm2", "essa dose ja passou $hr")
                    podeTocar = false

                }
            }


        }

        if(podeTocar){


            horaProxDose?.let { receiver.setAlarm2(intervaloEntreDoses, (extra as MedicamentoComDoses).medicamentoTratamento.idMedicamento, (extra as MedicamentoComDoses).listaDoses, context, it) }

            _updateDataStore.value = medicamento.stringDataStore



        }else{
            armarProximoAlarme()

        }



    }

    private fun armarProximoAlarme() {
        if((extra as MedicamentoComDoses).listaDoses.size > 0){


            val limiteFor = (extra as MedicamentoComDoses).listaDoses.size - 1

            iterarSobreDosesEAcharProxima(limiteFor)


        }

        _updateDataStore.value = medicamento.stringDataStore

        initializeAlarmManager()
    }

    private fun iterarSobreDosesEAcharProxima(limiteFor: Int) {
        for(i in 0..limiteFor){
            horaProxDose?.let {
                    horaProxDose->
                if(horaProxDose.length < 17){
                    if ((extra as MedicamentoComDoses).listaDoses[i].horarioDose + ":00" == horaProxDose +":00"){
                        Log.d("armarproximo7", "if 1 foram encontrados valores iguais!! horarioDose ${(extra as MedicamentoComDoses).listaDoses[i].horarioDose + ":00"} == horarioProxDose ${horaProxDose}:00")




                        if(i+1 == limiteFor){
                            //acabaram as doses

                        }else{
                            this.horaProxDose = (extra as MedicamentoComDoses).listaDoses[i+1].horarioDose + ":00"
                            return
                        }

                        /**
                         * dois medicamentos com o alarme tocando ao mesmo tempo...o botão de parar som some...o ideal
                         * era identificar que o alarme ja esta tocando e fazer uma lista ao inves de uma variavel com um espaço só.
                         */
                    }
                }else{

                    if ((extra as MedicamentoComDoses).listaDoses[i].horarioDose + ":00" == horaProxDose){
                        Log.d("armarproximo7", "if 2 foram encontrados valores iguais!! horarioDose ${(extra as MedicamentoComDoses).listaDoses[i].horarioDose + ":00"} == horarioProxDose $horaProxDose")

                        if(i+1 == limiteFor){
                            //acabaram as doses

                        }else{
                            this.horaProxDose = (extra as MedicamentoComDoses).listaDoses[i+1].horarioDose + ":00"
                            return

                        }
                    }
                }
            }


        }


    }

    fun startAlarmManager(context: Context){
        this.context = context
        initFragmentDetalhesUiInterface()
        initializeAlarmManager()
    }

    private fun initFragmentDetalhesUiInterface() {
        if(!this::fragmentDetalhesMedicamentosUi.isInitialized){
            fragmentDetalhesMedicamentosUi = context as FragmentDetalhesMedicamentosUi
        }
    }

    fun startChecarSeAlarmeEstaAtivado(ctx: Context) {
        this.context = ctx
        initFragmentDetalhesUiInterface()
        checarSeAlarmeEstaAtivado()
    }

    private fun checarSeAlarmeEstaAtivado() {
        if(medicamento.alarmeAtivado){
            if(AlarmReceiver.mp.isPlaying){
                Log.d("testeisplaying", "o mp esta tocando")

                AlarmReceiver.listaIdMedicamentosTocandoNoMomento.forEach {
                    if(it == medicamento.idMedicamento){
                        fragmentDetalhesMedicamentosUi.showBtnPararSom()
                    }
                }

            }else{
                Log.d("testeisplaying", "o mp nao esta tocando")

                initializeAlarmManager()
            }

        }

    }



}