package com.rubens.applembretemedicamento.presentation.interfaces

import com.rubens.applembretemedicamento.databinding.ItemDetalhesMedicamentosBinding
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.DetalhesMedicamentoAdapter

interface AccessAdapterMethodsInterface {
    fun getViewHolderBinding(): ItemDetalhesMedicamentosBinding?

    fun getViewHolderInstance(): DetalhesMedicamentoAdapter.ViewHolder?

    fun updateRecyclerViewOnDateChange(diaAtualSelecionado: String)
    fun atualizarDose(doses: Doses)


}