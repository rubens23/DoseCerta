package com.rubens.applembretemedicamento.presentation.interfaces

import com.rubens.applembretemedicamento.databinding.ItemDetalhesMedicamentosBinding
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.DetalhesMedicamentoAdapter

interface ConexaoBindingAdapterDetalhesMedicamentos {
    fun getItemDetalhesMedicamentosBinding(): ItemDetalhesMedicamentosBinding
    fun getViewHolderInstance(): DetalhesMedicamentoAdapter.ViewHolder
}