package com.rubens.applembretemedicamento.presentation.recyclerviewadapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.databinding.ItemDetalhesMedicamentosBinding
import com.rubens.applembretemedicamento.framework.data.AppDatabase
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.presentation.FragmentDetalhesMedicamentos
import com.rubens.applembretemedicamento.presentation.interfaces.ConexaoBindingAdapterDetalhesMedicamentos
import com.rubens.applembretemedicamento.presentation.interfaces.DetalhesMedicamentosAdapterInterface
import com.rubens.applembretemedicamento.utils.CalendarHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DetalhesMedicamentoAdapter(listaDosagemMedicamento: MedicamentoComDoses, val context: Context, val fragmentDetalhes: FragmentDetalhesMedicamentos): RecyclerView.Adapter<DetalhesMedicamentoAdapter.ViewHolder>(), CalendarHelper{
    private var listaDoses: ArrayList<Doses> = ArrayList()
    private lateinit var detalhesMedicamentosAdapterInterface: DetalhesMedicamentosAdapterInterface



    init {
        populateListaDoses(listaDosagemMedicamento)
        configureList()
        initDetalhesMedicaemtosAdapterInterface(fragmentDetalhes)
    }

    private fun initDetalhesMedicaemtosAdapterInterface(context: FragmentDetalhesMedicamentos) {
        detalhesMedicamentosAdapterInterface = context
    }

    private fun populateListaDoses(listaDosagemMedicamento: MedicamentoComDoses) {
        listaDoses.addAll(listaDosagemMedicamento.listaDoses)

    }

    private fun configureList() {
        var listaAuxiliar = ArrayList<Doses>()
        listaAuxiliar.addAll(listaDoses)
        listaDoses.clear()
        for (i in 0..listaAuxiliar.size - 1){
            if(pegarDataAtual() == listaAuxiliar[i].horarioDose.subSequence(0,10)){
                listaDoses.add(listaAuxiliar[i])
            }
        }

    }


    inner class ViewHolder(val binding: ItemDetalhesMedicamentosBinding): RecyclerView.ViewHolder(binding.root), ConexaoBindingAdapterDetalhesMedicamentos  {
        fun bind(doses: Doses){
            setDosageTakenOrNotTaken(doses)


            binding.itemMedicamento.setOnClickListener {
                detalhesMedicamentosAdapterInterface.onDoseClick(doses)
            }
            binding.ivStatusDosage.setOnClickListener {
              detalhesMedicamentosAdapterInterface.onDoseImageViewClick(doses)
            }

            setNomeMedicamentoOnItemTextView(doses)

            formatarHorarioDoseSeHorarioDoseSize15(doses)
            formatarHorarioDoseSeHorarioDoseSize16(doses)









        }

        private fun formatarHorarioDoseSeHorarioDoseSize16(doses: Doses) {
            if(doses.horarioDose.length == 16){

                binding.timeDosage.text = doses.horarioDose
            }

        }

        private fun formatarHorarioDoseSeHorarioDoseSize15(doses: Doses) {
            if(doses.horarioDose.length == 15){
                binding.timeDosage.text = doses.horarioDose
            }

        }

        private fun setNomeMedicamentoOnItemTextView(doses: Doses) {
            binding.tvDetailMedicineName.text = doses.nomeMedicamento

        }


        private fun setDosageTakenOrNotTaken(doses: Doses) {
            if(doses.jaTomouDose){
                binding.ivStatusDosage.setImageResource(R.drawable.med_taken)
            }else{
                binding.ivStatusDosage.setImageResource(R.drawable.med_not_taken)
            }

        }

        override fun getBindingFromAdapterDetalhes(): ItemDetalhesMedicamentosBinding {
            return binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDetalhesMedicamentosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listaDoses[position])

    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }


    override fun getItemCount(): Int = listaDoses.size


}