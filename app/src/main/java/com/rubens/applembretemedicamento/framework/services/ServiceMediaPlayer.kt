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
import com.rubens.applembretemedicamento.framework.domain.AlarmEvent
import com.rubens.applembretemedicamento.framework.domain.MediaPlayerTocando
import org.greenrobot.eventbus.EventBus

class ServiceMediaPlayer: Service() {
    private var mp: MediaPlayer = MediaPlayer()
    private var db: AppDatabase? = null
    private lateinit var medicamentoDoseDao: MedicamentoDao



    companion object{
        private var NOTIFICATION_ID = 1
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    private fun instatiateMediaPlayer() {
        Log.d("testeplay", "passei no instantiate media player ")

        mp = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if(intent?.action == "STOP_SERVICE"){

            stopSelf()
            return START_NOT_STICKY
        }

        if (intent?.action == "PLAY_ALARM"){


            Log.d("testeplay", "eu to aqui dentro do bloco play alarm")

            val medicationId = intent?.getIntExtra("medicamentoId", -1)
            setNotificationId(medicationId)
            initNotification(intent)
            instatiateMediaPlayer()
            playMediaPlayer()
            showBtnPararSomEventBus()
            enviarInstanciaAtualDoMediaPlayerParaListFragment(mp)
            initDb(applicationContext)
            initDao()

            setAlarmeTocandoForMedicamento(medicationId, true)
        }







        if (intent?.action == "GET_PLAYER_INSTANCE"){




        }







        return START_STICKY
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
            Log.d("testeplay", "to no mp is not playing, vai começar a tocar mp: $mp ")


            mp.start()

            Log.d("testeplay", "mp is playing? ${mp.isPlaying} ")


        }
    }



    private fun initNotification(intent: Intent?) {

        val notification = intent?.getParcelableExtra<Notification>("notification")
        if (notification != null) {
            // Iniciar o serviço em primeiro plano com a notificação existente
            Log.d("testeplay", "notificação nao é nula")


            startForeground(NOTIFICATION_ID, notification)
        } else {
            Log.d("testeplay", "notificação é nula")

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
        Log.d("testeplay", "vim aqui para parar o mp e o serviço")

    }

    private fun enviarInstanciaAtualDoMediaPlayerParaListFragment(mp: MediaPlayer) {
        EventBus.getDefault().postSticky(MediaPlayerTocando(mp))
    }


    private fun showBtnPararSomEventBus() {
        val data = "pode mostrar o botão parar som!"
        EventBus.getDefault().postSticky(AlarmEvent(data))
    }
}