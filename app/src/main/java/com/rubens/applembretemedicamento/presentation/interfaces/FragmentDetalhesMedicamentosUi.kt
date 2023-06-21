package com.rubens.applembretemedicamento.presentation.interfaces

import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses

interface FragmentDetalhesMedicamentosUi {

    fun showBtnCancelarAlarme()

    fun hideBtnArmarAlarme()

    fun hideBtnCancelarAlarme()

    fun showBtnArmarAlarme()

    fun showBtnPararSom()

    fun showAlarmConfirmationToast(horaProxDose: String, medicamentoComDoses: MedicamentoComDoses)
    fun showToastDosesAcabaram()


}