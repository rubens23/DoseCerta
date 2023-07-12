package com.rubens.applembretemedicamento.framework.domain

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.format.DateFormat
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
import com.rubens.applembretemedicamento.utils.CalendarHelper2
import java.io.Serializable
import javax.inject.Inject


class MedicamentoManager @Inject constructor(
    val context: Context,
    private val alarmHelper: AlarmHelper,
    private val calendarHelper: CalendarHelper,
    private val calendarHelper2: CalendarHelper2,
    private val is24HourFormat: Boolean,
    private val defaultDeviceDateFormat: String
) : Parcelable {
    lateinit var fragCtx: FragmentDetalhesMedicamentos
    var nomeMedicamento = ""
    var horaProxDose: String? = null
    private lateinit var medicamento: MedicamentoTratamento
    private var _updateDataStore: MutableLiveData<String> = MutableLiveData()
    var updateDataStore = _updateDataStore
    private var _horaProximaDoseObserver: MutableLiveData<String> = MutableLiveData()
    var horaProximaDoseObserver = _horaProximaDoseObserver
    private var intervaloEntreDoses = 0.0
    private lateinit var fragmentDetalhesMedicamentosUi: FragmentDetalhesMedicamentosUi


    private lateinit var extra: Serializable

    constructor(parcel: Parcel, alarmReceiver: AlarmReceiver, context: Context, alarmHelper: AlarmHelper, calendarHelper: CalendarHelper, calendarHelper2: CalendarHelper2, is24HourFormat: Boolean, defaultDeviceDateFormat: String) : this(context, alarmHelper, calendarHelper, calendarHelper2, is24HourFormat, defaultDeviceDateFormat) {
        nomeMedicamento = parcel.readString().toString()
        horaProxDose = parcel.readString()
        intervaloEntreDoses = parcel.readDouble()
    }


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
        Log.d("achandoatrihora", "eu fiz a atribuicao da hora aqui no updateHoraProxDose $horaProxDose")

    }

    fun startUpdateHoraProxDose(str: String) {
        updateHoraProxDose(str)
        Log.d("achandoatrihora", "o startUpdateHoraProxDose foi chamado")

    }


    private fun initializeAlarmManager() {
        if(horaProxDose != null){

            if(is24HourFormat){
                if(!calendarHelper.verificarSeDataHoraJaPassou(horaProxDose!!, true, calendarHelper.pegarFormatoDeDataPadraoDoDispositivoDoUsuario(context))){
                    //seta o alarme com essa hora mesmo
                    Log.d("fluxo0907", "chamei o metodo para armar o broadcastreceiver aqui no medicamento manager ")

                    chamarMetodoParaSetarOAlarmNoAlarmReceiver()
                    Log.d("testingsetalarm3", "o formato é 24 horas e o horario dessa dose ainda nao passou.")


                }else{
                    //pega a proxima dose mais proxima
                    armarProximoAlarme()
                    Log.d("testingsetalarm3", "o formato é 24 horas e o horario dessa dose ja passou.")

                }
            }else{
                if(!calendarHelper.verificarSeDataHoraJaPassou(horaProxDose!!, false, calendarHelper.pegarFormatoDeDataPadraoDoDispositivoDoUsuario(context))){
                    //seta o alarme com essa hora mesmo
                    Log.d("fluxo0907", "chamei o metodo para armar o broadcastreceiver aqui no medicamento manager ")

                    chamarMetodoParaSetarOAlarmNoAlarmReceiver()
                    Log.d("testingsetalarm3", "o formato é 12 horas e o horario dessa dose ainda nao passou.")


                }else{
                    //pega a proxima dose mais proxima
                    armarProximoAlarme()
                    Log.d("testingsetalarm3", "o formato é 12 horas e o horario dessa dose ja passou.")

                }


            }


            Log.d("testingsetalarm3", "hora do proximo alarme nao é nula, proximo alarme pode ser setado.")



        }else{
            Log.d("dosesacabaram","as doses acabaram")

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

            fragmentDetalhesMedicamentosUi.showAlarmConfirmationToast(it, extra as MedicamentoComDoses)
            fragmentDetalhesMedicamentosUi.showBtnCancelarAlarme()
            fragmentDetalhesMedicamentosUi.hideBtnArmarAlarme()
        }



    }





    fun armarProximoAlarme() {
        Log.d("testingsetalarm3", "eu passei aqui pelo metodo armarProximoAlarmea")

        iterarSobreDosesEAcharProxima()
        initializeAlarmManager()

    }



    private fun iterarSobreDosesEAcharProxima(){
        Log.d("testingsetalarm3", "eu passei aqui pelo metodo iterarSobreDosesEAcharProxima")
        (extra as MedicamentoComDoses).listaDoses.forEach {
                dose->
            val dataHoraSemSegundos = calendarHelper2.formatarDataHoraSemSegundos(dose.horarioDose, is24HourFormat, defaultDeviceDateFormat)

            if(calendarHelper.convertStringToDateSemSegundos(dataHoraSemSegundos, is24HourFormat, defaultDeviceDateFormat)!!.time > System.currentTimeMillis()){
                horaProxDose = dose.horarioDose
                Log.d("achandoatrihora", "eu fiz a atribuicao da hora aqui no iterarSobreDosesEAcharProxima $horaProxDose")
                return
            }
        }
    }


    fun startAlarmManager(context: FragmentDetalhesMedicamentos) {
        this.fragCtx = context
        initFragmentDetalhesUiInterface()
        initializeAlarmManager()
    }

    private fun initFragmentDetalhesUiInterface() {
        if (!this::fragmentDetalhesMedicamentosUi.isInitialized) {
            fragmentDetalhesMedicamentosUi = fragCtx
        }
    }

    fun startChecarSeAlarmeEstaAtivado(ctx: FragmentDetalhesMedicamentos) {
        this.fragCtx = ctx
        initFragmentDetalhesUiInterface()

    }








    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nomeMedicamento)
        parcel.writeString(horaProxDose)
        parcel.writeDouble(intervaloEntreDoses)
    }

    override fun describeContents(): Int {
        return 0
    }


    companion object CREATOR : Parcelable.Creator<MedicamentoManager> {
        override fun createFromParcel(parcel: Parcel): MedicamentoManager {
            val ar = MedicamentoManager::class.java.classLoader?.loadClass(AlarmReceiver::class.java.name)?.newInstance() as AlarmReceiver
            val context = ApplicationContextProvider.getApplicationContext()
            val alarmHelper = (parcel.readSerializable() as? AlarmHelper) ?: error("Failed to read AlarmHelper from Parcel.")
            val calendarHelper = (parcel.readSerializable() as? CalendarHelper) ?: error("Failed to read CalendarHelper from Parcel.")
            val calendarHelper2 = (parcel.readSerializable() as? CalendarHelper2) ?: error("Failed to read CalendarHelper from Parcel.")
            val is24HourFormat = (parcel.readSerializable() as? Boolean) ?: error("Failed to read CalendarHelper from Parcel.")
            val defaultDeviceDateFormat = (parcel.readSerializable() as? String) ?: error("Failed to read CalendarHelper from Parcel.")
            return MedicamentoManager(parcel, ar, context, alarmHelper, calendarHelper, calendarHelper2, is24HourFormat, defaultDeviceDateFormat)
        }

        override fun newArray(size: Int): Array<MedicamentoManager?> {
            return arrayOfNulls(size)
        }
    }


}