package com.rubens.applembretemedicamento.presentation.interfaces

import com.rubens.applembretemedicamento.databinding.ItemDetalhesMedicamentosBinding
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.DetalhesMedicamentoAdapter

interface AccessAdapterMethodsInterface {
    fun getViewHolderBinding(): ItemDetalhesMedicamentosBinding?

    fun getViewHolderInstance(): DetalhesMedicamentoAdapter.ViewHolder?


}