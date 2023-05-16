package com.rubens.applembretemedicamento.presentation.interfaces

import com.rubens.applembretemedicamento.databinding.ItemDetalhesMedicamentosBinding
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.DetalhesMedicamentoAdapter

interface DetalhesMedicamentosAdapterInterface {
    fun initDao()

    fun initDataBase()

    fun onDoseClick(doses: Doses)

    fun onDoseImageViewClick(doses: Doses)

    fun setViewHolderLiveDataValue(vh: DetalhesMedicamentoAdapter.ViewHolder)



}