package com.rubens.applembretemedicamento.framework.broadcastreceivers

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.appmedicamentos.utils.WakeLocker
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.framework.data.entities.AlarmEntity
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.managers.RoomAccess
import com.rubens.applembretemedicamento.framework.domain.eventbus.AlarmEvent
import com.rubens.applembretemedicamento.framework.domain.eventbus.AlarmeMedicamentoTocando
import com.rubens.applembretemedicamento.framework.services.ServiceMediaPlayer
import com.rubens.applembretemedicamento.presentation.MainActivity
import com.rubens.applembretemedicamento.utils.CalendarHelper2
import com.rubens.applembretemedicamento.utils.CalendarHelperImpl
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver: BroadcastReceiver()  {
    @Inject
    lateinit var roomAccess: RoomAccess
    @Inject
    lateinit var calendarHelper2: CalendarHelper2


    private var context: Context? = null






    override fun onReceive(p0: Context?, p1: Intent?) {
        context = p0
        Log.d("controllingplay", "to no inicio do onreceive")






            procedimentosAoChegarAHoraDoAlarme(p0)








    }

    private fun procedimentosAoChegarAHoraDoAlarme(p0: Context?) {
        val listaDeAlarmesTocando: ArrayList<AlarmEntity> = arrayListOf()

        val listaMedicamentos = roomAccess.getAllActiveAlarms()
        listaMedicamentos.forEach {
            Log.d("monitorandofor", "it: ${it.horaProxDose} calendar: ${CalendarHelperImpl().pegarDataHoraAtual()}")
            if(it.horaProxDose == CalendarHelperImpl().pegarDataHoraAtual()+":00"){
                Log.d("inspectinglistadd", "horario adicionado a lista de alarmes: ${it.horaProxDose} ${it.nomeMedicamento}")
                listaDeAlarmesTocando.add(it)
            }else{
                Log.d("inspectinglistadd", "horario NÃO adicionado a lista de alarmes: ${it.horaProxDose} ${it.nomeMedicamento}")

            }
        }

        Log.d("controletoast", "passei pelo metodo de procediemnto do alarme. Quer dizer que eu ja passei pelo on receive")



        var horarioDoseAnterior = ""




        Log.d("inspectinglistadd", "listaDeAlarmesTocando size ${listaDeAlarmesTocando.size}")


        listaDeAlarmesTocando.forEach {
                medicamentoNoAlarme->
            val listaDoses = arrayListOf<Doses>()
            Log.d("inspectinglistadd", "to aqui no primeiro for each")
            //extras from intent
            medicamentoNoAlarme.listaDoses.forEach {
                dose->
                Log.d("inspectinglistadd", "to aqui no segundo for each")

                val alarmeDoMedicamentoAtivado = roomAccess.verSeMedicamentoEstaComAlarmeAtivado(medicamentoNoAlarme.idMedicamento)
                Log.d("testingdose", "dose.horarioDose ${calendarHelper2.formatarDataHoraSemSegundos(dose.horarioDose)} medicamentoNoAlarme.horaProxDose ${calendarHelper2.formatarDataHoraSemSegundos(medicamentoNoAlarme.horaProxDose)}")
                if(calendarHelper2.formatarDataHoraSemSegundos(dose.horarioDose) == calendarHelper2.formatarDataHoraSemSegundos(medicamentoNoAlarme.horaProxDose)){
                    Log.d("inspectinglistadd", "as horas sao iguais: ${calendarHelper2.formatarDataHoraSemSegundos(dose.horarioDose)} ${dose.nomeMedicamento}")


                    if(!dose.jaMostrouToast){
                        Log.d("controllingplay", "ainda nao mostrou toast para esse medicamento")

                        if(alarmeDoMedicamentoAtivado && horarioDoseAnterior != dose.horarioDose){
                            var idMedicamento = medicamentoNoAlarme.idMedicamento
                            notificarOFragmentDetalhesDeQueJaPodeMostrarBotaoDePararSom(idMedicamento)
                            val horaDose = medicamentoNoAlarme.horaProxDose
                            val nomeMedicamento = medicamentoNoAlarme.nomeMedicamento

                            horarioDoseAnterior = dose.horarioDose


                            Log.d("podetocaralarm", "toast, notificação e som serão acionados para a dose: $dose ")








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

                            var doseAuxiliar = Doses(idDose = dose.idDose, nomeMedicamento = dose.nomeMedicamento, horarioDose = dose.horarioDose, intervaloEntreDoses = dose.intervaloEntreDoses, dataHora = dose.dataHora, qntDosesPorHorario = dose.qntDosesPorHorario, jaTomouDose = dose.jaTomouDose, jaMostrouToast = true)
                            listaDoses.add(doseAuxiliar)
                        }else{
                            Log.d("podetocaralarm", "essa dose era repetida, por isso ela nao tocou $dose ")

                        }

                    }else{
                        listaDoses.add(dose)
                        Log.d("controllingplay", "ja mostrou toast")


                    }
                }else{
                    listaDoses.add(dose)
                    Log.d("inspectinglistadd", "as horas sao iguais: ${calendarHelper2.formatarDataHoraSemSegundos(dose.horarioDose)} ${dose.nomeMedicamento}")



                }
            }
            roomAccess.atualizarDoseNaTabelaAlarms(listaDoses, medicamentoNoAlarme.idAlarme)


        }

    }


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



    private fun createNotificationForMedicationAlarmThatIsRinging(
        nomeMedicamento: String?,
        p0: Context?,
        horaDose: String?,
        pi: PendingIntent?,
        idMedicamento: Int?
    ) {


        var colorResource = roomAccess.getColorResource()




        val notification = p0?.let { context ->
            NotificationCompat.Builder(context, "something")
                .setContentTitle(nomeMedicamento)
                .setContentText("${context.getString(R.string.take_your_dose)} $horaDose")
                .setSmallIcon(R.drawable.ic_pills_bottle)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pi)
                .setColor(ContextCompat.getColor(context, colorResource))
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

        val tapResultIntent = Intent(p0, MainActivity::class.java)
        tapResultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        return idMedicamento?.let {
            PendingIntent.getActivity(
                p0,
                it, tapResultIntent, FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
            )
        }

    }


    private fun showToastTomeMedicamento(nomeMedicamento: String?, p0: Context?) {
        Log.d("controletoast", "mostrei o toast do medicamento $nomeMedicamento")
        if(nomeMedicamento != null && p0 != null){
                Toast.makeText(p0, "${p0.getString(R.string.take_dose_of)} $nomeMedicamento", Toast.LENGTH_LONG).show()



        }


    }

    private fun initWakeLocker(p0: Context?) {
        p0?.let {
            WakeLocker.acquire(it)
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




}
