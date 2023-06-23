package com.rubens.applembretemedicamento.utils

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento

interface AlarmUtilsInterface {

    fun getMediaPlayerInstance(): MediaPlayer?

    fun stopAlarmSound(context: Context)


    fun getNomeMedicamentoFromAlarmReceiver(): String

    fun getIdMedFromAlarmReceiver(): String

    fun getListaIdMedicamentosTocandoNoMomentoFromAlarmReceiver(): ArrayList<Int>

    fun getAlarmeTocandoLiveData(): LiveData<Boolean>

    fun getListaIdMedicamentoTocandoAtualmenteNoAlarmeReceiver(): LiveData<List<Int>>

    fun removeFromListaIdMedicamentoTocandoNoMomento(id: Int)

    fun stopMediaPlayer()

    fun getButtonChangeLiveData(): MutableLiveData<Boolean>

    fun initButtonStateLiveData()
    fun verSeMedicamentoEstaComAlarmeAtivado(medicamentoTratamento: MedicamentoTratamento): Boolean
    fun pegarProximaDoseESetarAlarme(medicamento: MedicamentoComDoses)
    fun initAlarmManager(context: Context)
    fun initAlarmIntent(context: Context)


}