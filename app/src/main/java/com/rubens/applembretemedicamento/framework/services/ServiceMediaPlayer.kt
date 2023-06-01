package com.rubens.applembretemedicamento.framework.services

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import com.rubens.applembretemedicamento.framework.data.AppDatabase
import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.domain.eventbus.AlarmEvent
import com.rubens.applembretemedicamento.framework.domain.eventbus.MediaPlayerTocando
import org.greenrobot.eventbus.EventBus


class ServiceMediaPlayer: Service() {
    private var mp: MediaPlayer = MediaPlayer()
    private var db: AppDatabase? = null
    private lateinit var medicamentoDoseDao: MedicamentoDao
    private var mpJaCriado = false



    companion object{
        private var NOTIFICATION_ID = -1
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    private fun instatiateMediaPlayer() {

        mp = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI)
        Log.d("checkingthings", "instancia media player ${mp}")
        mpJaCriado = true

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initDb(applicationContext)
        initDao()
        var algumMedicamentoTocando = false

        if(intent?.action == "STOP_SERVICE"){

            medicamentoDoseDao.getAllMedicamentoWithDoses().forEach {
                if(it.medicamentoTratamento.alarmeTocando){
                    algumMedicamentoTocando = true
                    return@forEach
                }
            }


            if(!algumMedicamentoTocando){
                stopSelf()
            }
            return START_NOT_STICKY
        }

        if (intent?.action == "PLAY_ALARM"){


            val mediaPlayerJaEstaTocando = checkIfMediaPlayerIsInitiatedAndPlaying()
            val medicationId = intent.getIntExtra("medicamentoId", -1)



            if (mediaPlayerJaEstaTocando){
                Log.d("entendendoshowstop", "to aqui dentro do media player ja esta tocando")
                setNotificationId(medicationId)
                initNotification(intent)
                showBtnPararSomEventBus(medicationId)
                enviarInstanciaAtualDoMediaPlayerParaListFragment(mp)

            }else{
                Log.d("entendendoshowstop", "to aqui dentro do else no service")

                setNotificationId(medicationId)
                initNotification(intent)
                instatiateMediaPlayer()
                playMediaPlayer()
                showBtnPararSomEventBus(medicationId)
                enviarInstanciaAtualDoMediaPlayerParaListFragment(mp)

            }

            setAlarmeTocandoForMedicamento(medicationId, true)

        }







        return START_STICKY
    }

    private fun checkIfMediaPlayerIsInitiatedAndPlaying(): Boolean {
        return if(mpJaCriado){
            mp.isPlaying


        }else{
            false

        }
    }



    private fun setAlarmeTocandoForMedicamento(medicationId: Int?, b: Boolean) {
        if (medicationId != null){
            medicamentoDoseDao.alarmeMedicamentoTocando(medicationId, b)

        }
    }

    private fun initDao() {
        medicamentoDoseDao = db!!.medicamentoDao
    }

    private fun initDb(applicationContext: Context) {
        db = AppDatabase.getAppDatabase(applicationContext)
    }

    private fun playMediaPlayer() {
        if(!mp.isPlaying){


            Log.d("checkingthings", "media player começou a tocar")


            mp.start()



        }
    }



    private fun initNotification(intent: Intent?) {

        val notification = intent?.getParcelableExtra<Notification>("notification")
        if (notification != null) {
            // Iniciar o serviço em primeiro plano com a notificação existente
            Log.d("checkingthings", "instancia notificação ${notification.contentIntent}")
            Log.d("checkingthings", "notification id ${NOTIFICATION_ID}")


            //ative para fazer as notificações anteriores persistirem
            //stopForeground(STOP_FOREGROUND_DETACH)

            startForeground(NOTIFICATION_ID, notification)
        } else {

            //todo que essa é realmente a melhor maneira para pegar a instancia do mp? desse jeito eu vou ter que mostrar outra notificacao
            // Se a notificação não estiver presente, crie uma nova notificação aqui
            //val newNotification = criarNotificacao()
            //startForeground(NOTIFICATION_ID, newNotification)
        }
    }



    private fun setNotificationId(medicamentoId: Int?) {
        if(medicamentoId != -1){
            NOTIFICATION_ID = medicamentoId!!
        }
    }

    override fun onDestroy() {

        stopMediaPlayer()

        super.onDestroy()
    }

    private fun stopMediaPlayer() {
        mp.stop()
        //mp.release()

    }

    private fun enviarInstanciaAtualDoMediaPlayerParaListFragment(mp: MediaPlayer) {
        EventBus.getDefault().postSticky(MediaPlayerTocando(mp))
    }


    private fun showBtnPararSomEventBus(medicationId: Int) {
        Log.d("entendendoshowstop", "to aqui no metodo que enviara o eventbus")

        val data = medicationId
        EventBus.getDefault().postSticky(AlarmEvent(data))
    }
}