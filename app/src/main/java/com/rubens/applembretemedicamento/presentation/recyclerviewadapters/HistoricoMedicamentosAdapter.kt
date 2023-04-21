package com.rubens.applembretemedicamento.presentation.recyclerviewadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rubens.applembretemedicamento.databinding.ItemDetalhesMedicamentosBinding
import com.rubens.applembretemedicamento.databinding.ItemHistoricoMedicamentoBinding
import com.rubens.applembretemedicamento.framework.data.entities.HistoricoMedicamentos

class HistoricoMedicamentosAdapter(private val listaHistoricoMedicamentos: List<HistoricoMedicamentos>): RecyclerView.Adapter<HistoricoMedicamentosAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemHistoricoMedicamentoBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(medicamentos: HistoricoMedicamentos){
            binding.tvDetailMedicineName.text = medicamentos.nomeMedicamento
            binding.dataFinalizacao.text = medicamentos.dataFinalizacao

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoricoMedicamentoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val medicamento = listaHistoricoMedicamentos[position]
        holder.bind(medicamento)
    }

    override fun getItemCount(): Int = listaHistoricoMedicamentos.size
}