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
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.appmedicamentos.utils.WakeLocker
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.presentation.MainActivity
import com.rubens.applembretemedicamento.presentation.interfaces.FragmentDetalhesMedicamentosUi
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.AdapterListaMedicamentos
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


class AlarmReceiver: BroadcastReceiver(), CalendarHelper, FuncoesDeTempo {

    private lateinit var alarmIntent: Intent
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private var listaDoses: ArrayList<Doses> = ArrayList()
    private lateinit var fragmentDetalhesMedicamentosUi: FragmentDetalhesMedicamentosUi





    companion object{
        var mp: MediaPlayer = MediaPlayer()
        var nomeMedicamento = ""
        var idMed = ""
        var alarmeTocando: MutableLiveData<Boolean> = MutableLiveData()
        var idMedicamentoTocandoAtualmente: MutableLiveData<List<Int>> = MutableLiveData()
        var listaIdMedicamentosTocandoNoMomento: ArrayList<Int> = ArrayList()

    }



    override fun onReceive(p0: Context?, p1: Intent?) {

        if(!this::fragmentDetalhesMedicamentosUi.isInitialized){
            fragmentDetalhesMedicamentosUi = p0 as FragmentDetalhesMedicamentosUi
        }

        var idMedicamento = p1?.getIntExtra("medicamentoid", -1)
        val horaDose = p1?.data
        val nomeMedicamento = p1?.getStringExtra("nomemedicamento")



        p0?.let {
           WakeLocker.acquire(it)
        }


        Toast.makeText(p0, "Tome o medicamento $nomeMedicamento", Toast.LENGTH_LONG).show()
        initOnAudioFocusChangeListener(p0)
        mp = MediaPlayer.create(p0, Settings.System.DEFAULT_RINGTONE_URI)
        mp.start()
        alarmeTocando.postValue(true)
        val listaNumeroInteiro: ArrayList<Int> = ArrayList()
        if (idMedicamento != null) {
            idMed = idMedicamento.toString()
            listaIdMedicamentosTocandoNoMomento.add(idMed.toInt())
            listaNumeroInteiro.add(idMedicamento)
            //idMedicamentoTocandoAtualmente.postValue(listaNumeroInteiro)
            AdapterListaMedicamentos.listaIdMedicamentos.add(idMed.toInt())

        }




        fragmentDetalhesMedicamentosUi.showBtnPararSom()



        val tapResultIntent = Intent(p0, MainActivity::class.java)
        tapResultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = idMedicamento?.let {
            PendingIntent.getActivity(p0,
                it, tapResultIntent, FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)
        }


        val notification = p0?.let {
                context->
            NotificationCompat.Builder(context, "something")
                .setContentTitle(nomeMedicamento)
                .setContentText("Tome sua dose das "+ horaDose + " o id do medicamento é $idMedicamento")
                .setSmallIcon(R.drawable.ic_pills_bottle)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, R.color.rosa_salmao))
                .setColorized(true)
                .build()
        }
        val manager = p0?.let{
            NotificationManagerCompat.from(it)}

        notification?.let {
            if (idMedicamento != null) {
                manager?.notify(idMedicamento, notification )
            }


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
        if(alarmManager != null){
            alarmManager.cancel(pendingIntent)
            fragmentDetalhesMedicamentosUi.hideBtnCancelarAlarme()
            fragmentDetalhesMedicamentosUi.showBtnArmarAlarme()
            WakeLocker.release()
        }
        if (mp.isPlaying){
            mp.stop()
            alarmeTocando.postValue(false)



        }
    }

    fun cancelAllAlarms(applicationContext: Context) {

        if(MainActivity.pendingIntentsList.size > 0){
            Log.d("testedeletepi", "é maior do que 0 ${MainActivity.pendingIntentsList.size}")
            for(pendingIntent in MainActivity.pendingIntentsList){
                if(this::alarmManager.isInitialized){
                    Log.d("testedeletepi", "entrei aqui no if que vai cancelar o alarmManager")

                    alarmManager.cancel(pendingIntent)

                }else{
                    alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmManager.cancel(pendingIntent)

                    Log.d("testedeletepi", "alarm manager ainda nao foi iniciallizado")

                }
            }

        }else{
            Log.d("testedeletepi", "é menor ou igual a 0 ${MainActivity.pendingIntentsList.size}")

            if(this::alarmManager.isInitialized){
                Log.d("testedeletepi", "entrei aqui no if 2 que vai cancelar o alarmManager")

                alarmManager.cancel(pendingIntent)

            }else{
                alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(pendingIntent)
                Log.d("testedeletepi", "else 2: alarm manager ainda não foi inicializado")

            }

        }


        MainActivity.pendingIntentsList.clear()
    }

    fun cancelAlarmByMedicamentoId(medicamentoId: Int, context: Context){
        if (alarmManager != null){
            alarmManager.cancel(PendingIntent.getBroadcast(context, medicamentoId, alarmIntent, 0))
            WakeLocker.release()
        }
    }



    fun setAlarm2(
        interEntreDoses: Double,
        medicamentoId: Int,
        medicamento: List<Doses>,
        context: Context,
        horaProxDose: String
    ){

        Log.d("smartalarm2", "eu to aqui no metodo que vai setar o alarme para a hora: $horaProxDose")

        listaDoses.addAll(medicamento)

        fragmentDetalhesMedicamentosUi.showBtnCancelarAlarme()
        fragmentDetalhesMedicamentosUi.hideBtnArmarAlarme()



        var intervaloEntreDoses = interEntreDoses
        if(intervaloEntreDoses < 1){
            intervaloEntreDoses *= 60

        }


        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(context, AlarmReceiver::class.java)
        var horaProximaDose = ""

        var mudarFormatador = false
        if(horaProxDose.get(2).toString() == "/"){
            if(horaProxDose.length < 17){
                Log.d("testeformat2", "antes $horaProxDose horaProximaDose length ${horaProximaDose.length}")
                horaProximaDose = horaProxDose+":00"
                Log.d("testeformat2", "depois $horaProximaDose")

                if (horaProximaDose.length == 18){

                    mudarFormatador = true
                }
            }else{
                Log.d("testeformat5", horaProxDose)
                if(horaProxDose.length == 18){
                    horaProximaDose = horaProxDose
                    mudarFormatador = true

                }else{

                    if(horaProxDose.length == 19){
                        horaProximaDose = horaProxDose
                        mudarFormatador = true

                    }

                    Log.d("testeformat5", "vc pegou a length errada...${horaProxDose.length}")

                }

            }

        }else{

            if (horaProxDose.get(1).toString() == ":"){
                if(horaProximaDose.length < 16){
                    Log.d("testeformat2", "antes 2 $horaProximaDose")

                    horaProximaDose = horaProxDose.subSequence(0, 11).toString() +"0"+horaProxDose.subSequence(11,16).toString()+":00"

                    Log.d("testeformat2", "antes 2 $horaProximaDose")



                    mudarFormatador = true


                }else{
                    horaProximaDose = horaProxDose.subSequence(0, 11).toString() +"0"+horaProxDose.subSequence(11,16).toString()
                    mudarFormatador = true

                }


            }else{
                if(horaProximaDose.length < 16){
                    Log.d("testeformat2", "antes 3 $horaProximaDose")

                    horaProximaDose = horaProxDose+":00"

                    Log.d("testeformat2", "antes 3 $horaProximaDose")

                    mudarFormatador = true
                    Log.d("testeformatador", "mudar formatador é tru ${horaProximaDose}")


                }else{
                    horaProximaDose = horaProxDose
                    Log.d("testedte", "eu to aqui e devo colocar true na variavel formatadora $horaProximaDose")

                }


            }

        }
        //horaProximaDoseObserver.postValue(horaProximaDose)

        var dateTimeFormatter: DateTimeFormatter
        if(mudarFormatador){
            if(horaProximaDose.length > 19){
                horaProximaDose = horaProximaDose.subSequence(0, 19).toString()
            }
            dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm:ss")

        }else{
            if(horaProximaDose.length > 19){
                horaProximaDose = horaProximaDose.subSequence(0, 19).toString()
            }
            dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        }
        var localDateHoraProximaDose = LocalDateTime.parse(horaProximaDose, dateTimeFormatter)

        var horaProximaDoseInMilliseconds = localDateHoraProximaDose.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
        val horaAtual = pegarDataAtual() + " " + pegarHoraAtual()
        val localDateHoraAtual = LocalDateTime.parse(horaAtual, dateTimeFormatter)
        val horaAtualEmMillisegundos = localDateHoraAtual.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
        alarmIntent.putExtra("medicamentoid", medicamentoId)

        if(horaProximaDoseInMilliseconds > horaAtualEmMillisegundos){
            var millisegundosAteProximaDose = horaProximaDoseInMilliseconds - horaAtualEmMillisegundos

            alarmIntent.data = Uri.parse(horaProximaDose)
            alarmIntent.putExtra("nomemedicamento", medicamento.get(0).nomeMedicamento)

            pendingIntent = PendingIntent.getBroadcast(context, medicamentoId, alarmIntent, 0)

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + millisegundosAteProximaDose, pendingIntent)



        }else{


            run lit@{
                medicamento.forEach {
                        doses ->

                    if (doses.horarioDose.get(12).toString() == ":"){
                        horaProximaDose = horaProxDose.subSequence(0, 11).toString() +"0"+horaProxDose.subSequence(11,16).toString()+":00"
                        mudarFormatador = true



                    }else{
                        if(horaProximaDose.length < 17){
                            Log.d("naopode", "eu entrei aqui e coloquei mais 2 zeros ${horaProximaDose.length}")
                            horaProximaDose =  doses.horarioDose+":00"
                        }
                        //horaProximaDose = doses.horarioDose
                    }

                    if(mudarFormatador){
                        dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm:ss")
                        Log.d("changeformatter", "eu mudei o formatador aqui no if")

                    }else{
                        Log.d("changeformatter", "eu mudei o formatador aqui no else")

                        dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                    }


                    localDateHoraProximaDose = LocalDateTime.parse(horaProximaDose, dateTimeFormatter)
                    horaProximaDoseInMilliseconds = localDateHoraProximaDose.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()



                    if(horaProximaDoseInMilliseconds > horaAtualEmMillisegundos){
                        Log.d("ifdentrodoforeach", "proxima dose esta no futuro $horaProximaDose")
                        //horaProximaDoseObserver.postValue(horaProximaDose)
                        return@lit

                    }else{
                        Log.d("ifdentrodoforeach", "essa dose já passou $horaProximaDose")

                    }
                }




            }

            alarmIntent.data = Uri.parse(horaProximaDose)
            alarmIntent.putExtra("nomemedicamento", medicamento.get(0).nomeMedicamento)

            pendingIntent = PendingIntent.getBroadcast(context, medicamentoId, alarmIntent, 0)

            val millisegundosAteProximaDose = horaProximaDoseInMilliseconds - horaAtualEmMillisegundos
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + millisegundosAteProximaDose, pendingIntent)

        }



        MainActivity.pendingIntentsList.add(pendingIntent)
        Log.d("testedeletepi", "acabei de adicionar algo na lista de pendingIntents ${MainActivity.pendingIntentsList.size}")


    }



}