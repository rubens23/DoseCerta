package com.rubens.applembretemedicamento.framework.broadcastreceivers

import ButtonStateLiveData
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
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.appmedicamentos.utils.WakeLocker
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.domain.AlarmEvent
import com.rubens.applembretemedicamento.framework.domain.AlarmeMedicamentoTocando
import com.rubens.applembretemedicamento.framework.domain.MediaPlayerTocando
import com.rubens.applembretemedicamento.framework.singletons.AlarmReceiverSingleton
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
    private var mp: MediaPlayer = MediaPlayer()
    private var nomeMedicamento = ""
    private var idMed = ""
    private var listaIdMedicamentosTocandoNoMomento: ArrayList<Int> = ArrayList()
    private var alarmeTocando: MutableLiveData<Boolean> = MutableLiveData()
    private var idMedicamentoTocandoAtualmente: MutableLiveData<List<Int>> = MutableLiveData()

    private lateinit var buttonStateLiveData: MutableLiveData<Boolean>

    init {
        Log.d("testebtn", "iniciei o alarm receiver")
        //initButtonStateLiveData()

        /*
        o alarmReceiver é criado no list fragment
        o alarmreceiver é criado tambem ao apertar no botao do alarme

        - o observer pega a instancia do livedata antes do livedata iniciar novamente

         */

    }


    override fun onReceive(p0: Context?, p1: Intent?) {

        notificarOFragmentDetalhesDeQueJaPodeMostrarBotaoDePararSom()

        /*
        - tenho que pegar o medicamento o qual o alarme esta tocando
        - e pegar o id desse medicamento
         */






        //extras from intent
        var idMedicamento = p1?.getIntExtra("medicamentoid", -1)
        val horaDose = p1?.data
        val nomeMedicamento = p1?.getStringExtra("nomemedicamento")

        initWakeLocker(p0)
        showToastTomeMedicamento(nomeMedicamento, p0)
        initOnAudioFocusChangeListener(p0)
        initMediaPlayer(p0)
        startMediaPlayer()
        enviarInstanciaAtualDoMediaPlayerParaListFragment(mp)

        Log.d("testebusdetalhes", "instancia do mp aqui no onReceive: $mp")


        val data = "pode mostrar o botão parar som!"
        EventBus.getDefault().post(AlarmEvent(data))

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

    private fun enviarInstanciaAtualDoMediaPlayerParaListFragment(mp: MediaPlayer) {
        EventBus.getDefault().post(MediaPlayerTocando(mp))
    }

    private fun notificarOFragmentListaDeQueOAlarmeDoMedicamentoEstaTocando(idMedicamento: Int?) {
        val id = idMedicamento
        EventBus.getDefault().post(AlarmeMedicamentoTocando(id))

    }

    private fun notificarOFragmentDetalhesDeQueJaPodeMostrarBotaoDePararSom() {
        val data = "pode mostrar o botão parar som!"
        EventBus.getDefault().post(AlarmEvent(data))
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
        val manager = p0?.let {
            NotificationManagerCompat.from(it)
        }

        notification?.let {
            if (idMedicamento != null) {
                manager?.notify(idMedicamento, notification)
            }


        }

    }

    private fun criarPendingIntentComIdDoMedicamento(
        p0: Context?,
        idMedicamento: Int?
    ): PendingIntent? {
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

    private fun startMediaPlayer() {
        mp.start()
        alarmeTocando.postValue(true)
        Log.d("testeshakingclock", "media player começou a tocar agora")



    }

    private fun initMediaPlayer(p0: Context?) {
        mp = MediaPlayer.create(p0, Settings.System.DEFAULT_RINGTONE_URI)
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


    fun cancelAlarm() {
        alarmManager.cancel(pendingIntent)
        Log.d("testeshowcancel", "show button armar alarme foi chamado pelo metodo cancelAlarm do AlarmReceiver")

        WakeLocker.release()
        if (mp.isPlaying) {
            mp.stop()
            alarmeTocando.postValue(false)
            Log.d("testealarme", "alarme tava tocqando e parou depois de passar por aqui")



        }
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
                    cancelAlarm()
                } else {
                    initAlarmManager(applicationContext)
                    cancelAlarm()
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
        horaProxDose: String
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
            putNomeMedicamentoEHoraProximaDoseNoExtraDoAlarmIntent(horaProximaDose)
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
            putNomeMedicamentoEHoraProximaDoseNoExtraDoAlarmIntent(horaProximaDose)
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

    private fun putNomeMedicamentoEHoraProximaDoseNoExtraDoAlarmIntent(horaProximaDose: String) {
        alarmIntent.data = Uri.parse(horaProximaDose)
        alarmIntent.putExtra("nomemedicamento", listaDoses.get(0).nomeMedicamento)

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

    override fun getMediaPlayerInstance(): MediaPlayer {
        Log.d("testeshakingclock", "peguei a instancia do media player")

        return mp
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
        mp.stop()
    }

    override fun getButtonChangeLiveData(): MutableLiveData<Boolean> {
        Log.d("testebtn", "eu to aqui pegando a instancia do live data. livedata: $buttonStateLiveData")

        return buttonStateLiveData
    }


}
