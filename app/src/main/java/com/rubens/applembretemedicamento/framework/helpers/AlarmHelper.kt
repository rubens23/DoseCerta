package com.rubens.applembretemedicamento.framework.helpers

import android.content.Context
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.presentation.FragmentDetalhesMedicamentos
import com.rubens.applembretemedicamento.presentation.MainActivity

interface AlarmHelper {
    fun setAlarm2(intervaloEntreDoses: Double, idMedicamento: Int, ctx: Context, listaDoses: List<Doses>, fragCtx: FragmentDetalhesMedicamentos?, mainActivity: MainActivity?, horaProxDose: String, nmMedicamento: String)

    fun cancelAlarm(context: Context, qntAlarmesTocando: Int)
    fun cancelAlarmByMedicamentoId(medicamentoId: Int, context: Context)
    fun initAlarmIntent(context: Context)
    fun initAlarmManager(applicationContext: Context)
}