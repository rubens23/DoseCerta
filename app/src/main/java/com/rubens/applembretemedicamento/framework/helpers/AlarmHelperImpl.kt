package com.rubens.applembretemedicamento.framework.helpers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
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
    private val context: Context
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

        Log.d("testingname", "nome aqui no começo do setAlarm: $nmMedicamento")





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
        var horaProximaDose = ""

        var mudarFormatador = false
        if (horaProxDose.get(2).toString() == "/") {
            if (horaProxDose.length < 17) {
                horaProximaDose = adicionarSufixoZeroZeroDepoisDaHora(horaProxDose)
                if (horaProximaDose.length == 18) {
                    mudarFormatador = true
                }
            } else {
                if (horaProxDose.length == 18) {
                    horaProximaDose = horaProxDose
                    mudarFormatador = true

                } else {
                    if (horaProxDose.length == 19) {
                        horaProximaDose = horaProxDose
                        mudarFormatador = true

                    }

                }

            }

        } else {

            if (horaProxDose.get(1).toString() == ":") {
                if (horaProximaDose.length < 16) {
                    horaProximaDose = adicionarPrfixoZeroNaHoraESufixoZeroZeroNaHora(horaProxDose)
                    mudarFormatador = true
                } else {
                    horaProximaDose = adicionarPrefixoZeroNaHora(horaProxDose)
                    mudarFormatador = true

                }
            } else {
                if (horaProximaDose.length < 16) {
                    horaProximaDose = adicionarSufixoZeroZeroDepoisDaHora(horaProxDose)
                    mudarFormatador = true
                } else {
                    horaProximaDose = horaProxDose
                }
            }

        }

        //escolher formatador baseado no size da string.
        var dateTimeFormatter: DateTimeFormatter
        if (mudarFormatador) {
            if (horaProximaDose.length > 19) {
                horaProximaDose = horaProximaDose.subSequence(0, 19).toString()
            }
            dateTimeFormatter = getFormatadorComUmDigitoNaHora()

        } else {
            if (horaProximaDose.length > 19) {
                horaProximaDose = horaProximaDose.subSequence(0, 19).toString()
            }
            dateTimeFormatter = getFormatadorComDoisDigitosNaHora()
        }


        //pegarHoraProximaDose e horaAtualEmMillisegundos
        var localDateTimeHoraProximaDose: LocalDateTime = transformarHoraProximaDoseEmLocalDate(horaProximaDose, dateTimeFormatter)
        var horaProximaDoseInMilliseconds = transformarDateTimeEmMilliseconds(localDateTimeHoraProximaDose)

        val horaAtual = pegarDataEHoraAtualFormatada()
        val localDateHoraAtual = pegarLocalDateTimeDaHoraAtual(horaAtual, dateTimeFormatter)
        val horaAtualEmMillisegundos = pegarDateTimeAtualEmMillisegundos(localDateHoraAtual)

        //colocar o id do medicamento na alarme intent para esse alarme ser unico para esse medicamento
        putExtraNoAlarmIntent("medicamentoid", idMedicamento)






        //compara hora atual e hora proxima dose
        if (horaProximaDoseInMilliseconds > horaAtualEmMillisegundos) {
            var millisegundosAteProximaDose = horaProximaDoseMenosHoraAtual(horaProximaDoseInMilliseconds, horaAtualEmMillisegundos)


            //faz configurações finais na intent e inicializa o alarme
            putNomeMedicamentoEHoraProximaDoseNoExtraDoAlarmIntent(horaProximaDose, nmMedicamento)
            pendingIntent = makePendingIntent(ctx, idMedicamento, alarmIntent, 0)
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + millisegundosAteProximaDose,
                pendingIntent
            )
        } else {
            run lit@{
                listaDoses.forEach { doses ->

                    if (doses.horarioDose.get(12).toString() == ":") {
                        horaProximaDose = adicionarPrfixoZeroNaHoraESufixoZeroZeroNaHora(horaProxDose)
                        mudarFormatador = true
                    } else {
                        if (horaProximaDose.length < 17) {
                            horaProximaDose = adicionarSufixoZeroZeroDepoisDaHoraSeguindoFormatacaoDoCampoHorarioDoseDaTabelaDeDoses(doses.horarioDose)
                        }
                    }

                    if (mudarFormatador) {
                        dateTimeFormatter = getFormatadorComUmDigitoNaHora()
                    } else {
                        dateTimeFormatter = getFormatadorComDoisDigitosNaHora()
                    }
                    localDateTimeHoraProximaDose =
                        transformarHoraProximaDoseEmLocalDate(horaProximaDose, dateTimeFormatter)
                    horaProximaDoseInMilliseconds = transformarDateTimeEmMilliseconds(localDateTimeHoraProximaDose)
                    if (horaProximaDoseInMilliseconds > horaAtualEmMillisegundos) {
                        return@lit
                    }
                }
            }

            //faz configurações finais na alarm Intent e seta o alarme
            putNomeMedicamentoEHoraProximaDoseNoExtraDoAlarmIntent(horaProximaDose, nmMedicamento)
            pendingIntent = makePendingIntent(ctx, idMedicamento, alarmIntent, 0)
            val millisegundosAteProximaDose =
                horaProximaDoseMenosHoraAtual(horaProximaDoseInMilliseconds, horaAtualEmMillisegundos)
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + millisegundosAteProximaDose,
                pendingIntent
            )

        }
        addPendingIntentToIntentList(pendingIntent, mainActivity, ctx)
        roomAccess.putMedicamentoDataOnRoom(
            BroadcastReceiverOnReceiveData(idMedicamento = idMedicamento, nomeMedicamento = nomeMedicamento, horaDose = calendarHelper2.formatarDataHoraSemSegundos(horaProxDose))
        )
        roomAccess.putNewActiveAlarmOnRoom(AlarmEntity(idMedicamento = idMedicamento,horaProxDose= calendarHelper2.formatarDataHoraComSegundos(horaProxDose),nomeMedicamento = nmMedicamento, alarmActive =  true, intervaloEntreDoses = intervaloEntreDoses, listaDoses =  listaDoses))

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

    private fun adicionarPrfixoZeroNaHoraESufixoZeroZeroNaHora(horaProxDose: String): String {
        return horaProxDose.subSequence(0, 11).toString() + "0" + horaProxDose.subSequence(
            11,
            16
        ).toString() + ":00"



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

    private fun adicionarPrefixoZeroNaHora(horaProxDose: String): String {
        return  horaProxDose.subSequence(0, 11).toString() + "0" + horaProxDose.subSequence(
            11,
            16
        ).toString()



    }

    private fun initMainActivityInterface(ctx: MainActivity?) {
        if (ctx != null) {
            if (!this::mainActivityInterface.isInitialized) {
                mainActivityInterface = ctx as MainActivityInterface
            }
        }
    }


    private fun getFormatadorComUmDigitoNaHora(): DateTimeFormatter {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm:ss")

    }





    private fun adicionarSufixoZeroZeroDepoisDaHora(horaProxDose: String): String {
        return horaProxDose + ":00"

    }


    private fun transformarHoraProximaDoseEmLocalDate(horaProximaDose: String, dateTimeFormatter: DateTimeFormatter): LocalDateTime {
        return LocalDateTime.parse(horaProximaDose, dateTimeFormatter)


    }

    private fun getFormatadorComDoisDigitosNaHora(): DateTimeFormatter {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
    }



    private fun pegarLocalDateTimeDaHoraAtual(horaAtual: CharSequence, dateTimeFormatter: DateTimeFormatter): LocalDateTime {
        return LocalDateTime.parse(horaAtual, dateTimeFormatter)


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

    private fun putExtraNoAlarmIntent(medicamentoIdStr: String, medicamentoId: Int) {
        alarmIntent.putExtra(medicamentoIdStr, medicamentoId)

    }

    private fun pegarDateTimeAtualEmMillisegundos(localDateHoraAtual: LocalDateTime): Long {
        return localDateHoraAtual.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()



    }

    private fun putNomeMedicamentoEHoraProximaDoseNoExtraDoAlarmIntent(
        horaProximaDose: String,
        nmMedicamento: String
    ) {
        alarmIntent.data = Uri.parse(horaProximaDose)
        alarmIntent.putExtra("nomemedicamento", nmMedicamento)

    }

    private fun adicionarSufixoZeroZeroDepoisDaHoraSeguindoFormatacaoDoCampoHorarioDoseDaTabelaDeDoses(
        horarioDose: String
    ): String {
        return horarioDose + ":00"

    }

    private fun makePendingIntent(context: Context, medicamentoId: Int, alarmIntent: Intent, i: Int): PendingIntent {
        return PendingIntent.getBroadcast(context, medicamentoId, alarmIntent, i)


    }


    private fun cancelAlarmByPendingIntent(pendingIntent: PendingIntent) {
        alarmManager.cancel(pendingIntent)

    }

    override fun cancelAlarmByMedicamentoId(medicamentoId: Int, context: Context) {
        if(this::alarmManager.isInitialized){
            alarmManager.cancel(PendingIntent.getBroadcast(context, medicamentoId, alarmIntent, 0))
            WakeLocker.release()
        }
    }

    private fun clearPendingIntentList() {
        mainActivityInterface.clearPendingIntentsList()
    }

    override fun cancelAlarm(context: Context) {
        if(this::alarmManager.isInitialized){
            alarmManager.cancel(pendingIntent)

        }

        WakeLocker.release()
        val serviceIntent = Intent(context, ServiceMediaPlayer::class.java)
        serviceIntent.action = "STOP_SERVICE"
        //ContextCompat.startForegroundService(context, serviceIntent)
        context.stopService(serviceIntent)
        alarmeTocando.postValue(false)

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
        iterarSobreDosesEAcharProxima(medicamento)
        initializeAlarmManager()
    }

    private fun iterarSobreDosesEAcharProxima(medicamento: MedicamentoComDoses) {
        this.medicamento = medicamento
        medicamento.listaDoses.forEach {
                dose->
            if(calendarHelper.convertStringToDateSemSegundos(calendarHelper2.formatarDataHoraSemSegundos(dose.horarioDose))!!.time > System.currentTimeMillis()){
                this.horaProxDose = dose.horarioDose
                return
            }
        }
    }

    private fun initializeAlarmManager() {
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