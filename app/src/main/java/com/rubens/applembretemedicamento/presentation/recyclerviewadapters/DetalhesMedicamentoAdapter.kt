package com.rubens.applembretemedicamento.presentation.recyclerviewadapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.databinding.ItemDetalhesMedicamentosBinding
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.presentation.FragmentDetalhesMedicamentos
import com.rubens.applembretemedicamento.presentation.interfaces.AccessAdapterMethodsInterface
import com.rubens.applembretemedicamento.presentation.interfaces.ConexaoBindingAdapterDetalhesMedicamentos
import com.rubens.applembretemedicamento.presentation.interfaces.DetalhesMedicamentosAdapterInterface
import com.rubens.applembretemedicamento.utils.CalendarHelper


class DetalhesMedicamentoAdapter(listaDosagemMedicamento: MedicamentoComDoses, val context: FragmentDetalhesMedicamentos): RecyclerView.Adapter<DetalhesMedicamentoAdapter.ViewHolder>(), CalendarHelper,
    AccessAdapterMethodsInterface {
    private var listaDoses: ArrayList<Doses> = ArrayList()
    private lateinit var detalhesMedicamentosAdapterInterface: DetalhesMedicamentosAdapterInterface
    private var viewHolder: DetalhesMedicamentoAdapter.ViewHolder? = null


    init {
        populateListaDoses(listaDosagemMedicamento)
        configureList()
        initDetalhesMedicaemtosAdapterInterface(context)
    }

    private fun initDetalhesMedicaemtosAdapterInterface(context: FragmentDetalhesMedicamentos) {
        detalhesMedicamentosAdapterInterface = context
    }

    override fun getViewHolderBinding(): ItemDetalhesMedicamentosBinding? {
        return viewHolder?.getItemDetalhesMedicamentosBinding()
    }

    override fun getViewHolderInstance(): ViewHolder? {
        return viewHolder
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

        override fun getItemDetalhesMedicamentosBinding(): ItemDetalhesMedicamentosBinding {
            return binding
        }

        override fun getViewHolderInstance(): DetalhesMedicamentoAdapter.ViewHolder {
            return ViewHolder(binding)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDetalhesMedicamentosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        viewHolder = ViewHolder(binding)
        return viewHolder as ViewHolder

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