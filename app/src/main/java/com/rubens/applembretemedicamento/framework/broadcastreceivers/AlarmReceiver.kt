package com.rubens.applembretemedicamento.framework.broadcastreceivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.appmedicamentos.utils.WakeLocker
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.domain.eventbus.AlarmEvent
import com.rubens.applembretemedicamento.framework.domain.eventbus.AlarmeMedicamentoTocando
import com.rubens.applembretemedicamento.framework.services.ServiceMediaPlayer
import com.rubens.applembretemedicamento.presentation.FragmentDetalhesMedicamentos
import com.rubens.applembretemedicamento.presentation.MainActivity
import com.rubens.applembretemedicamento.presentation.interfaces.FragmentDetalhesMedicamentosUi
import com.rubens.applembretemedicamento.presentation.interfaces.MainActivityInterface
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import org.greenrobot.eventbus.EventBus
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


class AlarmReceiver : BroadcastReceiver(), CalendarHelper, FuncoesDeTempo, AlarmReceiverInterface {

    private lateinit var alarmIntent: Intent
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private var listaDoses: ArrayList<Doses> = ArrayList()
    private lateinit var fragmentDetalhesMedicamentosUi: FragmentDetalhesMedicamentosUi
    private lateinit var mainActivityInterface: MainActivityInterface
    private var nomeMedicamento = ""
    private var idMed = ""
    private var listaIdMedicamentosTocandoNoMomento: ArrayList<Int> = ArrayList()
    private var alarmeTocando: MutableLiveData<Boolean> = MutableLiveData()
    private var idMedicamentoTocandoAtualmente: MutableLiveData<List<Int>> = MutableLiveData()
    private var context: Context? = null

    private lateinit var buttonStateLiveData: MutableLiveData<Boolean>




    override fun onReceive(p0: Context?, p1: Intent?) {
        context = p0



        Log.d("acompanhandoinstancia", "to aqui no onReceive do broadcast receiver")



        //extras from intent
        var idMedicamento = p1?.getIntExtra("medicamentoid", -1)
        notificarOFragmentDetalhesDeQueJaPodeMostrarBotaoDePararSom(idMedicamento)
        val horaDose = p1?.data
        val nomeMedicamento = p1?.getStringExtra("nomemedicamento")

        initWakeLocker(p0)
        showToastTomeMedicamento(nomeMedicamento, p0)
        initOnAudioFocusChangeListener(p0)




        notificarOFragmentListaDeQueOAlarmeDoMedicamentoEstaTocando(idMedicamento)


        val pendingIntent = criarPendingIntentComIdDoMedicamento(p0, idMedicamento)

        createNotificationForMedicationAlarmThatIsRinging(
            nomeMedicamento,
            p0,
            horaDose,
            pendingIntent,
            idMedicamento
        )


    }



    /*

    private fun initMediaPlayerService(context: Context?) {
        if(context != null){
            val serviceIntent = Intent(context, ServiceMediaPlayer::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)

        }
    }

     */



    private fun notificarOFragmentListaDeQueOAlarmeDoMedicamentoEstaTocando(idMedicamento: Int?) {
        val id = idMedicamento
        EventBus.getDefault().post(AlarmeMedicamentoTocando(id))

    }

    private fun notificarOFragmentDetalhesDeQueJaPodeMostrarBotaoDePararSom(idMedicamento: Int?) {
        val data = idMedicamento
        data?.let {
            EventBus.getDefault().postSticky(AlarmEvent(it))

        }
    }

    override fun initButtonStateLiveData() {
        if(!this::buttonStateLiveData.isInitialized){
            buttonStateLiveData = MutableLiveData()

            Log.d("testebtn", "eu to aqui iniciando o livedata $buttonStateLiveData")
            /*
            -inicia livedata no onReceive
            -fragment recebe a informação de que o livedata foi iniciado
            - observer inicia outro observer?
             */


        }

    }

    private fun createNotificationForMedicationAlarmThatIsRinging(
        nomeMedicamento: String?,
        p0: Context?,
        horaDose: Uri?,
        pi: PendingIntent?,
        idMedicamento: Int?
    ) {
//        val typedValue = TypedValue()
//        p0?.theme?.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
//        val colorPrimary= typedValue.data

        val notification = p0?.let { context ->
            NotificationCompat.Builder(context, "something")
                .setContentTitle(nomeMedicamento)
                .setContentText("Tome sua dose das " + horaDose + " o id do medicamento é $idMedicamento")
                .setSmallIcon(R.drawable.ic_pills_bottle)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pi)
                .setColor(ContextCompat.getColor(context, R.color.rosa_salmao))
                .setColorized(true)
                .build()
        }

        if(p0 != null){
            val foregroundIntent = Intent(p0, ServiceMediaPlayer::class.java)
            foregroundIntent.putExtra("notification", notification)
            foregroundIntent.putExtra("medicamentoId", idMedicamento)
            foregroundIntent.action = "PLAY_ALARM"

            p0.startForegroundService(foregroundIntent)
        }



    }

    private fun criarPendingIntentComIdDoMedicamento(
        p0: Context?,
        idMedicamento: Int?
    ): PendingIntent? {
        Log.d("checkingthings", "id passado na criacao da pending intent ${idMedicamento}")

        val tapResultIntent = Intent(p0, MainActivity::class.java)
        tapResultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        return idMedicamento?.let {
            PendingIntent.getActivity(
                p0,
                it, tapResultIntent, FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
            )
        }

    }


    private fun adicionarIdDoMedicamentoAListaDeMedicamentosTocandoNoMomento(idMedicamento: Int?) {
        val listaNumeroInteiro: ArrayList<Int> = ArrayList()
        if (idMedicamento != null) {
            idMed = idMedicamento.toString()
            listaIdMedicamentosTocandoNoMomento.add(idMed.toInt())
            listaNumeroInteiro.add(idMedicamento)
            //todo eu tenho que consertar essa referencia pois pode ser que o adapter nao esteja instanciado quando eu chamar essa linha abaixo
            //AdapterListaMedicamentos.listaIdMedicamentos.add(idMed.toInt())

        }

    }





    private fun showToastTomeMedicamento(nomeMedicamento: String?, p0: Context?) {
        Toast.makeText(p0, "Tome o medicamento $nomeMedicamento", Toast.LENGTH_LONG).show()

    }

    private fun initWakeLocker(p0: Context?) {
        p0?.let {
            WakeLocker.acquire(it)
        }

    }

    private fun initMainActivityInterface(ctx: MainActivity?) {
        if (ctx != null) {
            if (!this::mainActivityInterface.isInitialized) {
                mainActivityInterface = ctx as MainActivityInterface
            }
        }
    }

    private fun initFragmentDetalhesInterface(ctx: FragmentDetalhesMedicamentos) {
        if (!this::fragmentDetalhesMedicamentosUi.isInitialized) {
            fragmentDetalhesMedicamentosUi = ctx
        }
    }

    private fun initOnAudioFocusChangeListener(context: Context?) {
        Log.d("testeaudiofocus", "to no init audio focus")
        val afChangeListener: AudioManager.OnAudioFocusChangeListener =
            AudioManager.OnAudioFocusChangeListener { focusChange ->
                when (focusChange) {
                    AudioManager.AUDIOFOCUS_GAIN -> {
                        Log.d("testeaudiofocus", "foco mudou")

                        //viewModel.isAppLostAudioFocusLiveData.value = false
                    }

                    else -> {
                        //viewModel.isAppLostAudioFocusLiveData.value = true
                    }
                }
            }

        val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            audioManager.requestAudioFocus(
                AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setOnAudioFocusChangeListener(afChangeListener).build()
            )
        } else {
            audioManager.requestAudioFocus(
                afChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }


    fun cancelAlarm(context: Context) {
        if(this::alarmManager.isInitialized){
            alarmManager.cancel(pendingIntent)

        }

        WakeLocker.release()
        val serviceIntent = Intent(context, ServiceMediaPlayer::class.java)
        serviceIntent.action = "STOP_SERVICE"
        ContextCompat.startForegroundService(context, serviceIntent)
        alarmeTocando.postValue(false)
        Log.d("fluxo31", "entrou aqui no if do cancel, context nao é nulo")

    }

    override fun stopAlarmSound(context: Context){
        Log.d("testeplay", "eu to dentro do metodo que vai parar o serviço")

        val serviceIntent = Intent(context, ServiceMediaPlayer::class.java)
        serviceIntent.action = "STOP_SERVICE"
        ContextCompat.startForegroundService(context!!, serviceIntent)
        alarmeTocando.postValue(false)
    }

    fun cancelAllAlarms(applicationContext: Context) {

        if (this::mainActivityInterface.isInitialized) {
            if (mainActivityInterface.getPendingIntentsList().size > 0) {
                for (pendingIntent in mainActivityInterface.getPendingIntentsList()) {
                    if (this::alarmManager.isInitialized) {
                        cancelAlarmByPendingIntent(pendingIntent)
                    } else {
                        initAlarmManager(applicationContext)
                       cancelAlarmByPendingIntent(pendingIntent)
                    }
                }
            } else {
                if (this::alarmManager.isInitialized) {
                    cancelAlarm(applicationContext)
                } else {
                    initAlarmManager(applicationContext)
                    cancelAlarm(applicationContext)
                }

            }
            clearPendingIntentList()
        }
    }

    private fun clearPendingIntentList() {
        mainActivityInterface.clearPendingIntentsList()
    }

    private fun initAlarmManager(applicationContext: Context) {
        alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private fun cancelAlarmByPendingIntent(pendingIntent: PendingIntent) {
        alarmManager.cancel(pendingIntent)

    }

    fun cancelAlarmByMedicamentoId(medicamentoId: Int, context: Context) {
        if(this::alarmManager.isInitialized){
            alarmManager.cancel(PendingIntent.getBroadcast(context, medicamentoId, alarmIntent, 0))
            WakeLocker.release()
        }
    }


    fun setAlarm2(
        interEntreDoses: Double,
        medicamentoId: Int,
        listaDoses: List<Doses>,
        context: FragmentDetalhesMedicamentos,
        ctxActivity: MainActivity,
        horaProxDose: String,
        nmMedicamento: String
    ) {

        preencherListaDeDoses(listaDoses)
        showBtnCancelarAlarme(context)
        hideBtnArmarAlarme(context)

        var intervaloEntreDoses = interEntreDoses
        if (intervaloEntreDoses < 1) {
            intervaloEntreDoses = pegarIntervaloEmMinutos(intervaloEntreDoses)
        }


        initAlarmManager(ctxActivity)
        initAlarmIntent(ctxActivity)
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
        putExtraNoAlarmIntent("medicamentoid", medicamentoId)




        //compara hora atual e hora proxima dose
        if (horaProximaDoseInMilliseconds > horaAtualEmMillisegundos) {
            var millisegundosAteProximaDose = horaProximaDoseMenosHoraAtual(horaProximaDoseInMilliseconds, horaAtualEmMillisegundos)


            //faz configurações finais na intent e inicializa o alarme
            putNomeMedicamentoEHoraProximaDoseNoExtraDoAlarmIntent(horaProximaDose, nmMedicamento)
            pendingIntent = makePendingIntent(ctxActivity, medicamentoId, alarmIntent, 0)
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
            pendingIntent = makePendingIntent(ctxActivity, medicamentoId, alarmIntent, 0)
            val millisegundosAteProximaDose =
                horaProximaDoseMenosHoraAtual(horaProximaDoseInMilliseconds, horaAtualEmMillisegundos)
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + millisegundosAteProximaDose,
                pendingIntent
            )

        }
        addPendingIntentToIntentList(pendingIntent, ctxActivity)

    }

    private fun addPendingIntentToIntentList(pendingIntent: PendingIntent, ctxActivity: MainActivity) {
        initMainActivityInterface(ctxActivity)
        mainActivityInterface.addPendingIntentToPendingIntentsList(pendingIntent)

    }

    private fun adicionarSufixoZeroZeroDepoisDaHoraSeguindoFormatacaoDoCampoHorarioDoseDaTabelaDeDoses(
        horarioDose: String
    ): String {
        return horarioDose + ":00"

    }

    private fun makePendingIntent(context: Context, medicamentoId: Int, alarmIntent: Intent, i: Int): PendingIntent {
        return PendingIntent.getBroadcast(context, medicamentoId, alarmIntent, i)


    }

    private fun putNomeMedicamentoEHoraProximaDoseNoExtraDoAlarmIntent(
        horaProximaDose: String,
        nmMedicamento: String
    ) {
        alarmIntent.data = Uri.parse(horaProximaDose)
        alarmIntent.putExtra("nomemedicamento", nmMedicamento)

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

    private fun pegarLocalDateTimeDaHoraAtual(horaAtual: CharSequence, dateTimeFormatter: DateTimeFormatter): LocalDateTime {
        return LocalDateTime.parse(horaAtual, dateTimeFormatter)


    }

    private fun pegarDataEHoraAtualFormatada(): CharSequence {
        return pegarDataAtual() + " " + pegarHoraAtual()

    }

    private fun transformarDateTimeEmMilliseconds(localDateTimeHoraProximaDose: LocalDateTime): Long {
        return localDateTimeHoraProximaDose.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()


    }

    private fun transformarHoraProximaDoseEmLocalDate(horaProximaDose: String, dateTimeFormatter: DateTimeFormatter): LocalDateTime {
        return LocalDateTime.parse(horaProximaDose, dateTimeFormatter)


    }

    private fun getFormatadorComDoisDigitosNaHora(): DateTimeFormatter {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
    }

    private fun getFormatadorComUmDigitoNaHora(): DateTimeFormatter {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm:ss")

    }

    private fun adicionarPrefixoZeroNaHora(horaProxDose: String): String {
        return  horaProxDose.subSequence(0, 11).toString() + "0" + horaProxDose.subSequence(
            11,
            16
        ).toString()



    }

    private fun adicionarPrfixoZeroNaHoraESufixoZeroZeroNaHora(horaProxDose: String): String {
        return horaProxDose.subSequence(0, 11).toString() + "0" + horaProxDose.subSequence(
            11,
            16
        ).toString() + ":00"



    }

    private fun adicionarSufixoZeroZeroDepoisDaHora(horaProxDose: String): String {
        return horaProxDose + ":00"

    }

    private fun initAlarmIntent(context: Context) {
        alarmIntent = Intent(context, AlarmReceiver::class.java)


    }

    private fun pegarIntervaloEmMinutos(intervaloEntreDoses: Double): Double {
        return intervaloEntreDoses * 60

    }

    private fun hideBtnArmarAlarme(context: FragmentDetalhesMedicamentos) {
        initFragmentDetalhesInterface(context)
        fragmentDetalhesMedicamentosUi.hideBtnArmarAlarme()
        Log.d("testeshowcancel", "eu to aqui no metodo de esconder botao de armar alarme")

    }

    private fun showBtnCancelarAlarme(context: FragmentDetalhesMedicamentos) {
        initFragmentDetalhesInterface(context)
        fragmentDetalhesMedicamentosUi.showBtnCancelarAlarme()
        Log.d("testeshowcancel", "to aqui no metodo de mostrar botao cancelar alarme")

    }



    private fun preencherListaDeDoses(lstDoses: List<Doses>) {
        this.listaDoses.addAll(lstDoses)

    }

    override fun getMediaPlayerInstance(): MediaPlayer? {
        Log.d("testeshakingclock", "peguei a instancia do media player")
        //todo a instancia do mp agora esta no service, arruma essa referencia depois

        return null
    }

    override fun getNomeMedicamentoFromAlarmReceiver(): String {
        return nomeMedicamento
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
