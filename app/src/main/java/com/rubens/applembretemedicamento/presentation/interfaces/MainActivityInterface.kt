package com.rubens.applembretemedicamento.presentation.interfaces

import android.app.PendingIntent

/**
 * essa interface serve como intermediario para
 * classes que querem acessar metodos ou variaveis
 * que pertencem a main activity
 */
interface MainActivityInterface {

    fun showToolbar()
    fun hideToolbarTitle()
    fun showBtnDeleteMedicamento()

    fun getPendingIntentsList(): ArrayList<PendingIntent>

    fun clearPendingIntentsList()

    fun addPendingIntentToPendingIntentsList(pi: PendingIntent)

    fun hideToolbar()
}