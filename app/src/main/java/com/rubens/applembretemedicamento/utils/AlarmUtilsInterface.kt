package com.rubens.applembretemedicamento.utils

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

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



}