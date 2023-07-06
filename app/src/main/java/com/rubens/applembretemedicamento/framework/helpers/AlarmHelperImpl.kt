package com.rubens.applembretemedicamento.framework.helpers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.text.format.DateFormat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.appmedicamentos.utils.WakeLocker
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiver
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.AlarmEntity
import com.rubens.applembretemedicamento.framework.data.entities.BroadcastReceiverOnReceiveData
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import com.rubens.applembretemedicamento.framework.data.managers.RoomAccess
import com.rubens.applembretemedicamento.framework.services.ServiceMediaPlayer
import com.rubens.applembretemedicamento.presentation.FragmentDetalhesMedicamentos
import com.rubens.applembretemedicamento.presentation.MainActivity
import com.rubens.applembretemedicamento.presentation.interfaces.FragmentDetalhesMedicamentosUi
import com.rubens.applembretemedicamento.presentation.interfaces.MainActivityInterface
import com.rubens.applembretemedicamento.utils.AlarmUtilsInterface
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.CalendarHelper2
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class AlarmHelperImpl @Inject constructor(
    private val roomAccess: RoomAccess,
    private val funcoesDeTempo: FuncoesDeTempo,
    private val calendarHelper2: CalendarHelper2,
    private val calendarHelper: CalendarHelper,
    private val context: Context,
    private val is24HourFormat: Boolean
): AlarmHelper, AlarmUtilsInterface {

    private var medicamento: MedicamentoComDoses? = null
    private var horaProxDose: String? = null
    private lateinit var alarmManager: AlarmManager
    private lateinit var fragmentDetalhesMedicamentosUi: FragmentDetalhesMedicamentosUi
    private var listaDoses: ArrayList<Doses> = ArrayList()
    private lateinit var alarmIntent: Intent
    private lateinit var mainActivityInterface: MainActivityInterface
    private lateinit var pendingIntent: PendingIntent
    private var nomeMedicamento = ""
    private var alarmeTocando: MutableLiveData<Boolean> = MutableLiveData()
    private var idMed = ""
    private var listaIdMedicamentosTocandoNoMomento: ArrayList<Int> = ArrayList()
    private var idMedicamentoTocandoAtualmente: MutableLiveData<List<Int>> = MutableLiveData()
    private lateinit var buttonStateLiveData: MutableLiveData<Boolean>









    override fun setAlarm2(
        intervaloEntreDoses: Double,
        idMedicamento: Int,
        ctx: Context,
        listaDoses: List<Doses>,
        fragCtx: FragmentDetalhesMedicamentos?,
        mainActivity: MainActivity?,
        horaProxDose: String,
        nmMedicamento: String
    ) {

        var horaProxDosePadronizada: String? = null

        if(is24HourFormat){
            horaProxDosePadronizada = calendarHelper2.padronizarHoraProxDose(horaProxDose)

        }else{
            horaProxDosePadronizada = horaProxDose
        }
        Log.d("testingsetalarm", "hora no começo do setAlarm: $horaProxDosePadronizada")






        preencherListaDeDoses(listaDoses)
        if (fragCtx != null) {
            showBtnCancelarAlarme(fragCtx)
        }
        if (fragCtx != null) {
            hideBtnArmarAlarme(fragCtx)
        }

        var intervaloEntreDoses = intervaloEntreDoses
        if (intervaloEntreDoses < 1) {
            intervaloEntreDoses = pegarIntervaloEmMinutos(intervaloEntreDoses)
        }


        if (mainActivity != null) {
            initAlarmManager(mainActivity)
        }
        if (mainActivity != null) {
            initAlarmIntent(mainActivity)
        }




        horaProxDosePadronizada?.let {
            Log.d("testingsetalarm2","hora padronizada dentro aqui do bloco lambda: $it")

            var localDateTimeHoraProximaDose: LocalDateTime = transformarHoraProximaDoseEmLocalDate(
                horaProxDosePadronizada!!
            )
            var horaProximaDoseInMilliseconds = transformarDateTimeEmMilliseconds(localDateTimeHoraProximaDose)

            val horaAtual = pegarDataEHoraAtualFormatada()
            val localDateHoraAtual = pegarLocalDateTimeDaHoraAtual(horaAtual)
            val horaAtualEmMillisegundos = pegarDateTimeAtualEmMillisegundos(localDateHoraAtual)

            if (horaProximaDoseInMilliseconds > horaAtualEmMillisegundos) {
                var millisegundosAteProximaDose = horaProximaDoseMenosHoraAtual(horaProximaDoseInMilliseconds, horaAtualEmMillisegundos)


                //faz configurações finais na intent e inicializa o alarme
                pendingIntent = makePendingIntent(ctx, idMedicamento, alarmIntent, 0)
                Log.d("testepattern", "setei o alarme para $horaProxDosePadronizada")

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + millisegundosAteProximaDose,
                    pendingIntent
                )
            } else {
                run lit@{
                    listaDoses.forEach { doses ->

                        if(is24HourFormat){
                            horaProxDosePadronizada = calendarHelper2.padronizarHoraProxDose(horaProxDose)

                        }else{
                            horaProxDosePadronizada = horaProxDose
                        }
                        horaProxDosePadronizada?.let{
                            Log.d("testingsetalarm2","hora padronizada dentro aqui do bloco lambda do run lit@: $it")

                            localDateTimeHoraProximaDose =
                                transformarHoraProximaDoseEmLocalDate(horaProxDosePadronizada!!)
                            horaProximaDoseInMilliseconds = transformarDateTimeEmMilliseconds(localDateTimeHoraProximaDose)
                            if (horaProximaDoseInMilliseconds > horaAtualEmMillisegundos) {
                                return@lit
                            }
                        }

                    }
                }

                pendingIntent = makePendingIntent(ctx, idMedicamento, alarmIntent, 0)
                val millisegundosAteProximaDose =
                    horaProximaDoseMenosHoraAtual(horaProximaDoseInMilliseconds, horaAtualEmMillisegundos)
                Log.d("testepattern", "setei o alarme para $horaProxDosePadronizada")

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + millisegundosAteProximaDose,
                    pendingIntent
                )

            }
        }




        addPendingIntentToIntentList(pendingIntent, mainActivity, ctx)
        if(is24HourFormat){
            roomAccess.putMedicamentoDataOnRoom(
                BroadcastReceiverOnReceiveData(idMedicamento = idMedicamento, nomeMedicamento = nomeMedicamento, horaDose = calendarHelper2.formatarDataHoraSemSegundos(horaProxDose, is24HourFormat))
            )
        }else{
            roomAccess.putMedicamentoDataOnRoom(
                BroadcastReceiverOnReceiveData(idMedicamento = idMedicamento, nomeMedicamento = nomeMedicamento, horaDose = horaProxDosePadronizada!!)
            )
        }

        roomAccess.putNewActiveAlarmOnRoom(AlarmEntity(idMedicamento = idMedicamento,horaProxDose= horaProxDosePadronizada!!,nomeMedicamento = nmMedicamento, alarmActive =  true, intervaloEntreDoses = intervaloEntreDoses, listaDoses =  listaDoses))

    }

    private fun preencherListaDeDoses(lstDoses: List<Doses>) {
        this.listaDoses.addAll(lstDoses)

    }

    private fun showBtnCancelarAlarme(context: FragmentDetalhesMedicamentos) {
        initFragmentDetalhesInterface(context)
        fragmentDetalhesMedicamentosUi.showBtnCancelarAlarme()
        Log.d("testeshowcancel", "to aqui no metodo de mostrar botao cancelar alarme")

    }

    private fun hideBtnArmarAlarme(context: FragmentDetalhesMedicamentos) {
        initFragmentDetalhesInterface(context)
        fragmentDetalhesMedicamentosUi.hideBtnArmarAlarme()
        Log.d("testeshowcancel", "eu to aqui no metodo de esconder botao de armar alarme")

    }


    private fun pegarIntervaloEmMinutos(intervaloEntreDoses: Double): Double {
        return intervaloEntreDoses * 60

    }

    override fun initAlarmManager(applicationContext: Context) {
        alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }



    private fun initFragmentDetalhesInterface(ctx: FragmentDetalhesMedicamentos) {
        if (!this::fragmentDetalhesMedicamentosUi.isInitialized) {
            fragmentDetalhesMedicamentosUi = ctx
        }
    }

    override fun initAlarmIntent(context: Context) {
        alarmIntent = Intent(context, AlarmReceiver::class.java)


    }

    private fun addPendingIntentToIntentList(pendingIntent: PendingIntent, ctxActivity: MainActivity?, ctx: Context) {
        initMainActivityInterface(ctxActivity)
        if(ctxActivity != null){
            mainActivityInterface.addPendingIntentToPendingIntentsList(pendingIntent)

        }

    }



    private fun initMainActivityInterface(ctx: MainActivity?) {
        if (ctx != null) {
            if (!this::mainActivityInterface.isInitialized) {
                mainActivityInterface = ctx as MainActivityInterface
            }
        }
    }










    private fun transformarHoraProximaDoseEmLocalDate(horaProximaDose: String): LocalDateTime {
        Log.d("testingsetalarm2","$horaProximaDose")

        var formatter: DateTimeFormatter
        if(is24HourFormat){
            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            Log.d("testingsetalarm","entrei aqui no if que indica que o formato de horas é de 24 horas")

        }else{
            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy h:mm a")
            Log.d("testingsetalarm","entrei aqui no else que indica que o formato de horas é de 12 horas")


        }
        /*
        eu tenho uma data assim: "05/07/2023 11:37 AM"

        e eu preciso transformá-la em um localDateTime aqui: LocalDateTime.parse(horaProximaDose, formatter).
        como ficara o formatter?
         */

        return LocalDateTime.parse(horaProximaDose, formatter)


    }




    private fun pegarLocalDateTimeDaHoraAtual(horaAtual: CharSequence): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

        return LocalDateTime.parse(horaAtual, formatter)


    }

    private fun pegarDataEHoraAtualFormatada(): CharSequence {
        return calendarHelper.pegarDataAtual() + " " + funcoesDeTempo.pegarHoraAtual()

    }

    private fun transformarDateTimeEmMilliseconds(localDateTimeHoraProximaDose: LocalDateTime): Long {
        return localDateTimeHoraProximaDose.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()


    }

    private fun horaProximaDoseMenosHoraAtual(horaProximaDoseInMilliseconds: Long, horaAtualEmMillisegundos: Long): Long {
        return horaProximaDoseInMilliseconds - horaAtualEmMillisegundos

    }



    private fun pegarDateTimeAtualEmMillisegundos(localDateHoraAtual: LocalDateTime): Long {
        return localDateHoraAtual.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()



    }




    private fun makePendingIntent(context: Context, medicamentoId: Int, alarmIntent: Intent, i: Int): PendingIntent {
        return PendingIntent.getBroadcast(context, medicamentoId, alarmIntent, i)


    }



    override fun cancelAlarmByMedicamentoId(medicamentoId: Int, context: Context) {
        if(this::alarmManager.isInitialized){
            alarmManager.cancel(PendingIntent.getBroadcast(context, medicamentoId, alarmIntent, 0))
            WakeLocker.release()
        }
    }


    override fun cancelAlarm(context: Context, qntAlarmesTocando: Int) {
        if(this::alarmManager.isInitialized){
            alarmManager.cancel(pendingIntent)

        }

        if(qntAlarmesTocando < 2){
            Log.d("controlcancel", "AlarmHelper a quantidade de alarmes tocando é menor do que 2 entao posso cancelar o som")
            WakeLocker.release()
            val serviceIntent = Intent(context, ServiceMediaPlayer::class.java)
            serviceIntent.action = "STOP_SERVICE"
            //ContextCompat.startForegroundService(context, serviceIntent)
            context.stopService(serviceIntent)
            alarmeTocando.postValue(false)
        }else{
            Log.d("controlcancel", "AlarmHelper a quantidade de alarmes tocando é MAIOR do que 1 entao NAO posso cancelar o som")

        }



    }

    override fun stopAlarmSound(context: Context){

        val serviceIntent = Intent(context, ServiceMediaPlayer::class.java)
        serviceIntent.action = "STOP_SERVICE"
        context.stopService(serviceIntent)
        alarmeTocando.postValue(false)
    }

    override fun getNomeMedicamentoFromAlarmReceiver(): String {
        return nomeMedicamento
    }

    override fun initButtonStateLiveData() {
        if(!this::buttonStateLiveData.isInitialized){
            buttonStateLiveData = MutableLiveData()


        }

    }

    override fun verSeMedicamentoEstaComAlarmeAtivado(medicamentoTratamento: MedicamentoTratamento): Boolean {
        return roomAccess.verSeMedicamentoEstaComAlarmeAtivado(medicamentoTratamento.idMedicamento)
    }

    override fun pegarProximaDoseESetarAlarme(medicamento: MedicamentoComDoses) {
        this.horaProxDose = null
        Log.d("setproxdose", "eu to aqui no metodo pegarProximaDoseESetarAlarme")
        iterarSobreDosesEAcharProxima(medicamento)
        initializeAlarmManager()
    }

    private fun iterarSobreDosesEAcharProxima(medicamento: MedicamentoComDoses) {
        this.medicamento = medicamento
        medicamento.listaDoses.forEach {
                dose->
            Log.d("setproxdose3", "eu to aqui no metodo iterarSobreDosesEAcharProxima. iteracao da lista de doses. dose: ${dose.horarioDose}")
            val is24HF = DateFormat.is24HourFormat(context)


            if(is24HF){
                if(calendarHelper.convertStringToDateSemSegundos(calendarHelper2.formatarDataHoraSemSegundos(dose.horarioDose, is24HF), is24HF)!!.time > System.currentTimeMillis()){
                    Log.d("setproxdose4", "eu to aqui no if formato 24 horas primeiro opardor maior que segundo. dose: ${dose.horarioDose}")

                    this.horaProxDose = dose.horarioDose

                    Log.d("setproxdose", "eu to aqui no metodo iterarSobreDosesEAcharProxima. system current time in millis ${this.horaProxDose}")

                    Log.d("setproxdose", "eu to aqui no metodo iterarSobreDosesEAcharProxima: ${this.horaProxDose}")

                    return
                }
            }else{
                if(calendarHelper.convertStringToDateSemSegundos(dose.horarioDose, is24HF)!!.time > System.currentTimeMillis()){
                    Log.d("setproxdose4", "eu to aqui no if formato 12 horas primeiro opardor maior que segundo. dose: ${dose.horarioDose}")

                            this.horaProxDose = dose.horarioDose

                    Log.d("setproxdose", "eu to aqui no metodo iterarSobreDosesEAcharProxima. system current time in millis ${this.horaProxDose}")

                    Log.d("setproxdose", "eu to aqui no metodo iterarSobreDosesEAcharProxima: ${this.horaProxDose}")

                    return
                }
            }

        }
    }

    private fun initializeAlarmManager() {
        Log.d("setproxdose", "eu to aqui no initialize alarmManager: ${this.horaProxDose}")

        if(this.horaProxDose != null){
            chamarMetodoParaSetarOAlarmNoAlarmReceiver()



        }else{
            Log.d("ahdosesacabaram","as doses acabaram")

        }


    }

    private fun chamarMetodoParaSetarOAlarmNoAlarmReceiver() {
        this.horaProxDose?.let {
            setAlarm2(
                medicamento!!.listaDoses[0].intervaloEntreDoses,
                medicamento!!.medicamentoTratamento.idMedicamento,
                context,
                medicamento!!.listaDoses,
                null,
                null,
                it,
                medicamento!!.medicamentoTratamento.nomeMedicamento
            )

        }



    }

    override fun getMediaPlayerInstance(): MediaPlayer? {
        Log.d("testeshakingclock", "peguei a instancia do media player")
        //todo a instancia do mp agora esta no service, arruma essa referencia depois

        return null
    }

    override fun getIdMedFromAlarmReceiver(): String {
        return idMed
    }

    override fun getListaIdMedicamentosTocandoNoMomentoFromAlarmReceiver(): ArrayList<Int> {
        return listaIdMedicamentosTocandoNoMomento
    }

    override fun getAlarmeTocandoLiveData(): LiveData<Boolean> {
        return alarmeTocando
    }

    override fun getListaIdMedicamentoTocandoAtualmenteNoAlarmeReceiver(): LiveData<List<Int>> {
        return idMedicamentoTocandoAtualmente
    }

    override fun removeFromListaIdMedicamentoTocandoNoMomento(id: Int) {
        listaIdMedicamentosTocandoNoMomento.remove(id)
    }

    override fun stopMediaPlayer() {
        //mp.stop()
    }

    override fun getButtonChangeLiveData(): MutableLiveData<Boolean> {
        Log.d("testebtn", "eu to aqui pegando a instancia do live data. livedata: $buttonStateLiveData")

        return buttonStateLiveData
    }



}