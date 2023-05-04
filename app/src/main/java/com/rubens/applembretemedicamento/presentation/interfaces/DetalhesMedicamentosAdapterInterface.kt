package com.rubens.applembretemedicamento.presentation.interfaces

import com.rubens.applembretemedicamento.framework.data.entities.Doses

interface DetalhesMedicamentosAdapterInterface {
    fun initDao()

    fun initDataBase()

    fun onDoseClick(doses: Doses)

    fun onDoseImageViewClick(doses: Doses)
}