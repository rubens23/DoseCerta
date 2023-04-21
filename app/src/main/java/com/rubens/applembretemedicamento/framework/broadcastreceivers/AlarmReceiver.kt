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
import com.rubens.applembretemedicamento.presentation.FragmentDetalhesMedicamentos
import com.rubens.applembretemedicamento.presentation.MainActivity
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


class AlarmReceiver: BroadcastReceiver(), CalendarHelper, FuncoesDeTempo {

    private lateinit var audioFocusChangeListener: AudioManager.OnAudioFocusChangeListener
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    val horaProximaDoseObserver: MutableLiveData<String> = MutableLiveData()
    private var listaDoses: ArrayList<Doses> = ArrayList()
    private var horaDoseAtual = ""


    companion object{
        var mp: MediaPlayer = MediaPlayer()
        var nomeMedicamento = ""
        var alarmeTocando: MutableLiveData<Boolean> = MutableLiveData()
        var idMedicamentoTocandoAtualmente: MutableLiveData<List<Int>> = MutableLiveData()

    }






    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.d("testeaudiofocus", "onReceive")

        val idMedicamento = p1?.getIntExtra("medicamentoid", -1)
        val horaDose = p1?.data
        val nomeMedicamento = p1?.getStringExtra("nomemedicamento")

        Log.d("testereceiveextra", "id do medicamento: $idMedicamento hora da dose: $horaDose nome medicamento $nomeMedicamento")

        //todo eu preciso do nome correto do medicamento de acordo com o id que foi passado no alarme
        //todo eu preciso da hora correta da dose
        //todo quando eu tiver essas duas coisas eu vou conseguir passar certinho para a notification

        /*
        pega a lista de medicamentos, ve qual é o proximo que vai tocar, pega o nome e o horario
        da proxima dose que vai tocar
         */







        p0?.let {
           WakeLocker.acquire(it)
        }

        //val result: Int = audioManger.requestAudioFocus(focusRequest)





        Toast.makeText(p0, "Tome o medicamento $nomeMedicamento", Toast.LENGTH_LONG).show()
        initOnAudioFocusChangeListener(p0)
        mp = MediaPlayer.create(p0, Settings.System.DEFAULT_RINGTONE_URI)
        mp.start()
        alarmeTocando.postValue(true)
        val listaNumeroInteiro: ArrayList<Int> = ArrayList()
        if (idMedicamento != null) {
            listaNumeroInteiro.add(idMedicamento)
            idMedicamentoTocandoAtualmente.postValue(listaNumeroInteiro)

        }




        FragmentDetalhesMedicamentos.binding.btnPararSom.visibility = View.VISIBLE

        pegarProximaHorarioProximaDose()
        showNotification(p0, p1)

        val mainActivityIntent = Intent(p0, MainActivity::class.java)
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
                .setSmallIcon(R.drawable.ic_launcher_foreground)
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


            //22:46
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

    private fun showNotification(context: Context?, intent: Intent?) {

        if (context != null && intent != null){
            Log.d("testecontext", "o context não é nulo")




        }else{
            Log.d("testecontext", "o context é nulo")
        }

    }

    private fun pegarProximaHorarioProximaDose() {


    }

    fun cancelAlarm() {
        if(alarmManager != null){
            alarmManager.cancel(pendingIntent)
            FragmentDetalhesMedicamentos.binding.btnCancelarAlarme.visibility = View.GONE
            FragmentDetalhesMedicamentos.binding.btnArmarAlarme.visibility = View.VISIBLE
            FragmentDetalhesMedicamentos.binding.btnArmarAlarme.isClickable = true
            WakeLocker.release()
        }
        if (mp.isPlaying){
            mp.stop()
            alarmeTocando.postValue(false)



            //esse if só vai ser disparado quando o alarme ja tiver tocado e o usuario tiver parado o alarme
            //
        }
    }



    fun setAlarm2(
        interEntreDoses: Double,
        medicamentoId: Int,
        medicamento: List<Doses>,
        context: Context,
        horaProxDose: String
    ){
        /*
        dataclass(nomeMedicamento, idMedicamento, horaProxDose) aí adiciona esse objeto para uma lista desses objetos
                no onreceive eu acesso essa lista de objetos e como eu vou saber qual medicamento eu tenho que usar?
        eu preciso de uma marca do da pendingIntent, de acordo com qual pendingIntent que esta tocando o alarme
        eu pego o objeto e seto a notification, e depois eu apago o objeto da lista.

         */



        listaDoses.addAll(medicamento)

        FragmentDetalhesMedicamentos.binding.btnCancelarAlarme.visibility = View.VISIBLE
        FragmentDetalhesMedicamentos.binding.btnArmarAlarme.visibility = View.INVISIBLE
        FragmentDetalhesMedicamentos.binding.btnArmarAlarme.isClickable = false



        //21:45 teste 26
        var intervaloEntreDoses = interEntreDoses
        var intervaloEmMillisegundos = 0.0
        if(intervaloEntreDoses < 1){
            intervaloEntreDoses *= 60
            Log.d("testeinter", "$intervaloEntreDoses")
            //vai calcular o intervalo de uma forma, considerando minutos
            intervaloEmMillisegundos = minutosParaMillisegundos(intervaloEntreDoses.toLong().toString()).toDouble()

        }else{
            Log.d("testeinter", "intervalo não é menor ou igual a 0.. $intervaloEntreDoses")
            //vai calcular o intervalo de outra forma, considerando horas
            intervaloEmMillisegundos = horasParaMillisegundos(intervaloEntreDoses.toLong().toString()).toDouble()

        }

        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        var horaProximaDose = ""
        var mudarFormatador = false
        if(horaProxDose.get(2).toString() == "/"){
            horaProximaDose = horaProxDose
        }else{

            if (horaProxDose.get(1).toString() == ":"){
                //horaProximaDose = pegarDataAtual()+" 0"+horaProxDose+":00"
                horaProximaDose = pegarDataAtual()+" "+horaProxDose+":00"
                mudarFormatador = true


            }else{


                horaProximaDose = pegarDataAtual()+" "+horaProxDose+":00"
            }

        }
        /*
        a hora pode estar desse jeito
         */
        horaProximaDoseObserver.postValue(horaProximaDose)
        Log.d("testeparse", "hora proxima dose que vai ser usada no parse: ${horaProximaDose}")

        val dateTimeFormatter: DateTimeFormatter
        if(mudarFormatador){
            dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm:ss")

        }else{
            dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        }
        var localDateHoraProximaDose = LocalDateTime.parse(horaProximaDose, dateTimeFormatter)
        var horaProximaDoseInMilliseconds = localDateHoraProximaDose.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
        val horaAtual = pegarDataAtual() + " " + pegarHoraAtual()
        val localDateHoraAtual = LocalDateTime.parse(horaAtual, dateTimeFormatter)
        val horaAtualEmMillisegundos = localDateHoraAtual.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
        Log.d("testetimeinmilli", "hora atual em millisegundos $horaAtualEmMillisegundos hora proxima dose em millisegundos $horaProximaDoseInMilliseconds")
        alarmIntent.putExtra("medicamentoid", medicamentoId)

        if(horaProximaDoseInMilliseconds > horaAtualEmMillisegundos){
            var millisegundosAteProximaDose = horaProximaDoseInMilliseconds - horaAtualEmMillisegundos
            Log.d("testetimeinmilli", "millisegundos ate proxima dose: $millisegundosAteProximaDose")
            Log.d("testeifelse", "to no if")

            /*
            teste 3

            hora proxima dose que vai ser usada no parse: 18/04/2023 23:06:00
             */
            //Log.d("testereceiveextra", "id do medicamento: $idMedicamento hora da dose: $horaDose")

            alarmIntent.data = Uri.parse(horaProximaDose)
            alarmIntent.putExtra("nomemedicamento", medicamento.get(0).nomeMedicamento)

            pendingIntent = PendingIntent.getBroadcast(context, medicamentoId, alarmIntent, 0)

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + millisegundosAteProximaDose, pendingIntent)
            Log.d("testetimeinmilli", "intervalo em millisegundos: ${intervaloEmMillisegundos.toLong()}")



        }else{


            run lit@{
                medicamento.forEach {
                        doses ->

                    horaProximaDose = pegarDataAtual()+" "+doses.horarioDose+":00"

                    localDateHoraProximaDose = LocalDateTime.parse(horaProximaDose, dateTimeFormatter)
                    horaProximaDoseInMilliseconds = localDateHoraProximaDose.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()



                    if(horaProximaDoseInMilliseconds > horaAtualEmMillisegundos){
                        Log.d("ifdentrodoforeach", "proxima dose esta no futuro $horaProximaDose")
                        horaProximaDoseObserver.postValue(horaProximaDose)
                        return@lit

                    }else{
                        Log.d("ifdentrodoforeach", "essa dose já passou $horaProximaDose")

                    }
                }




            }

            alarmIntent.data = Uri.parse(horaProximaDose)
            alarmIntent.putExtra("nomemedicamento", medicamento.get(0).nomeMedicamento)

            pendingIntent = PendingIntent.getBroadcast(context, medicamentoId, alarmIntent, 0)

            var millisegundosAteProximaDose = horaProximaDoseInMilliseconds - horaAtualEmMillisegundos
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + millisegundosAteProximaDose, pendingIntent)

            /*
            quando o alarme toca ele tem que ir para o onReceive
             */




        }


    }

    private fun getSegundosAteProximaDose(hr: String): Long? {
        Log.d("testeonreceive", "${hr}")

        val stringConvertidaParaData = convertStringToDate(hr)
        if(stringConvertidaParaData != null){
            val segundosAteProximaDose = calculateHoursDifference(stringConvertidaParaData)
            return segundosAteProximaDose
        }
        return null

    }


}