package com.rubens.applembretemedicamento.framework.domain

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.rubens.applembretemedicamento.framework.ApplicationContextProvider
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiver
import com.rubens.applembretemedicamento.utils.AlarmUtilsInterface
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import com.rubens.applembretemedicamento.framework.helpers.AlarmHelper
import com.rubens.applembretemedicamento.framework.helpers.AlarmHelperImpl
import com.rubens.applembretemedicamento.presentation.FragmentDetalhesMedicamentos
import com.rubens.applembretemedicamento.presentation.MainActivity
import com.rubens.applembretemedicamento.presentation.interfaces.FragmentDetalhesMedicamentosUi
import com.rubens.applembretemedicamento.utils.CalendarHelper
import java.io.Serializable
import javax.inject.Inject


class MedicamentoManager @Inject constructor(
    val context: Context,
    private val alarmHelper: AlarmHelper,
    private val calendarHelper: CalendarHelper
) : Parcelable {
    lateinit var fragCtx: FragmentDetalhesMedicamentos
    var nomeMedicamento = ""
    var horaProxDose: String? = null
    private lateinit var medicamento: MedicamentoTratamento
    //private lateinit var receiver: AlarmReceiver
    private var _updateDataStore: MutableLiveData<String> = MutableLiveData()
    var updateDataStore = _updateDataStore
    private var _horaProximaDoseObserver: MutableLiveData<String> = MutableLiveData()
    var horaProximaDoseObserver = _horaProximaDoseObserver
    private var intervaloEntreDoses = 0.0
    private lateinit var fragmentDetalhesMedicamentosUi: FragmentDetalhesMedicamentosUi
    private lateinit var alarmUtilsInterface: AlarmUtilsInterface


    private lateinit var extra: Serializable

    constructor(parcel: Parcel, alarmReceiver: AlarmReceiver, context: Context, alarmHelper: AlarmHelper, calendarHelper: CalendarHelper) : this(context, alarmHelper, calendarHelper) {
        nomeMedicamento = parcel.readString().toString()
        horaProxDose = parcel.readString()
        intervaloEntreDoses = parcel.readDouble()
    }

    /*
    constructor(parcel: Parcel) : this() {
        nomeMedicamento = parcel.readString()!!
        horaProxDose = parcel.readString()
        intervaloEntreDoses = parcel.readDouble()
    }

     */


    private fun updateMedicamento(medicamento: MedicamentoTratamento) {
        this.medicamento = medicamento
        nomeMedicamento = medicamento.nomeMedicamento
    }

    fun getReceiver(): AlarmHelper {
        return alarmHelper
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
        return true
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
        //initAlarmReceiver()
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
            calendarHelper.convertStringToDate(hr)?.let {
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
            alarmHelper.setAlarm2(
                intervaloEntreDoses,
                (extra as MedicamentoComDoses).medicamentoTratamento.idMedicamento,
                context,
                (extra as MedicamentoComDoses).listaDoses,
                fragCtx,
                fragCtx.requireActivity() as MainActivity,
                it,
                (extra as MedicamentoComDoses).medicamentoTratamento.nomeMedicamento
            )

            Log.d("toastaparece", "to bem aqui antes de mostrar o toast de confirmação do alarme")
            fragmentDetalhesMedicamentosUi.showAlarmConfirmationToast(it, extra as MedicamentoComDoses)
            Log.d("testeshowcancel", "toast ja apareceu, portnato eu ja posso alterar os botoes")
            fragmentDetalhesMedicamentosUi.showBtnCancelarAlarme()
            fragmentDetalhesMedicamentosUi.hideBtnArmarAlarme()
            //fragmentDetalhesMedicamentosUi.showBtnArmarAlarme()
        }



        //markToastAsShown()

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
            if(limiteFor != 0){
                iterarSobreDosesEAcharProxima(limiteFor)
            }
        }

        //markToastAsShown()
        if((extra as MedicamentoComDoses).listaDoses.size - 1 != 0){
            initializeAlarmManager()
        }
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
        this.fragCtx = context
        initFragmentDetalhesUiInterface()
        //initAlarmReceiverInterface()
        initializeAlarmManager()
    }

    private fun initFragmentDetalhesUiInterface() {
        if (!this::fragmentDetalhesMedicamentosUi.isInitialized) {
            fragmentDetalhesMedicamentosUi = fragCtx as FragmentDetalhesMedicamentosUi
        }
    }

    fun startChecarSeAlarmeEstaAtivado(ctx: FragmentDetalhesMedicamentos) {
        this.fragCtx = ctx
        initFragmentDetalhesUiInterface()
        //initAlarmReceiverInterface()
        //checarSeAlarmeEstaAtivado()
    }

    /*
    private fun initAlarmReceiverInterface() {
        if (!this::alarmUtilsInterface.isInitialized) {
            alarmUtilsInterface = alarmHelper
        }
    }

     */

    private fun initAlarmReceiver() {
        //receiver = AlarmReceiverSingleton.getInstance()
    }
    /*

    private fun checarSeAlarmeEstaAtivado() {
        if (medicamento.alarmeAtivado) {
            if(alarmUtilsInterface.getMediaPlayerInstance() != null){
                if (alarmUtilsInterface.getMediaPlayerInstance()!!.isPlaying) {
                    alarmUtilsInterface.getListaIdMedicamentosTocandoNoMomentoFromAlarmReceiver()
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

     */

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nomeMedicamento)
        parcel.writeString(horaProxDose)
        parcel.writeDouble(intervaloEntreDoses)
    }

    override fun describeContents(): Int {
        return 0
    }

    /*
    companion object CREATOR : Parcelable.Creator<MedicamentoManager> {
        override fun createFromParcel(parcel: Parcel): MedicamentoManager {
            return MedicamentoManager(parcel)
        }

        override fun newArray(size: Int): Array<MedicamentoManager?> {
            return arrayOfNulls(size)
        }
    }

     */
    companion object CREATOR : Parcelable.Creator<MedicamentoManager> {
        override fun createFromParcel(parcel: Parcel): MedicamentoManager {
            val ar = MedicamentoManager::class.java.classLoader?.loadClass(AlarmReceiver::class.java.name)?.newInstance() as AlarmReceiver
            val context = ApplicationContextProvider.getApplicationContext()
            val alarmHelper = (parcel.readSerializable() as? AlarmHelper) ?: error("Failed to read AlarmHelper from Parcel.")
            val calendarHelper = (parcel.readSerializable() as? CalendarHelper) ?: error("Failed to read CalendarHelper from Parcel.")
            return MedicamentoManager(parcel, ar, context, alarmHelper, calendarHelper)
        }

        override fun newArray(size: Int): Array<MedicamentoManager?> {
            return arrayOfNulls(size)
        }
    }


}