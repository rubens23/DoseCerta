package com.rubens.applembretemedicamento.framework.services

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import com.rubens.applembretemedicamento.framework.data.AppDatabase
import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.domain.eventbus.AlarmEvent
import com.rubens.applembretemedicamento.framework.domain.eventbus.MediaPlayerTocando
import org.greenrobot.eventbus.EventBus


class ServiceMediaPlayer: Service() {
    private lateinit var mp: MediaPlayer
    private var db: AppDatabase? = null
    private lateinit var medicamentoDoseDao: MedicamentoDao
    private var mediaPlayerJaCriado = false



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


            mp = MediaPlayer.create(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            mediaPlayerJaCriado = true

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initDb(applicationContext)
        initDao()

        if(intent?.action == "STOP_SERVICE"){
            var algumMedicamentoTocando = false

            val listaMedicamentosTocando = arrayListOf<MedicamentoComDoses>()

            medicamentoDoseDao.getAllMedicamentoWithDoses().forEach {
                if(it.medicamentoTratamento.alarmeTocando){
                    algumMedicamentoTocando = true
                    listaMedicamentosTocando.add(it)
                }
            }

            Log.d("stoptag", "entrei aqui na tag de stop service")


            if(!algumMedicamentoTocando && listaMedicamentosTocando.isEmpty()){
                Log.d("controlcancel", "nao tem nenhum medicamento tocando e a lista de medicamentos tocando esta vazia")

                    stopForeground(true)
                    stopSelf()
                    Log.d("stoptag", "nao tem nenhum medicamento tocando")



            }else{
                Log.d("controlcancel", "temAlgumMedicamentoTocando $algumMedicamentoTocando lista.size ${listaMedicamentosTocando.isEmpty()}")

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
        return if(mediaPlayerJaCriado){
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


            Log.d("fluxo31", "media player começou a tocar, instancia mp ${mp}")


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
            //startForeground(1, null)
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
        if(this::mp.isInitialized){
            mp.stop()

        }

        //mp.release()

    }

    private fun enviarInstanciaAtualDoMediaPlayerParaListFragment(mediaPlayer: MediaPlayer) {
        EventBus.getDefault().postSticky(MediaPlayerTocando(mediaPlayer))
    }


    private fun showBtnPararSomEventBus(medicationId: Int) {
        Log.d("entendendoshowstop", "to aqui no metodo que enviara o eventbus")

        val data = medicationId
        EventBus.getDefault().postSticky(AlarmEvent(data))
    }
}