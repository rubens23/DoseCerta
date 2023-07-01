package com.rubens.applembretemedicamento.presentation.recyclerviewadapters

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
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
import com.rubens.applembretemedicamento.utils.CalendarHelper2


class DetalhesMedicamentoAdapter(var listaDosagemMedicamento: MedicamentoComDoses, val context: FragmentDetalhesMedicamentos, private val dataAtual: String, private val calendarHelper2: CalendarHelper2): RecyclerView.Adapter<DetalhesMedicamentoAdapter.ViewHolder>(),
    AccessAdapterMethodsInterface {
    private var listaDoses: ArrayList<Doses> = ArrayList()
    private lateinit var detalhesMedicamentosAdapterInterface: DetalhesMedicamentosAdapterInterface
    private var viewHolder: DetalhesMedicamentoAdapter.ViewHolder? = null
    private var dataAtualSelecionada = dataAtual


    init {
        Log.d("atualizandorv", "entrei no init")

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

    override fun updateList(medComDoses: MedicamentoComDoses){
        listaDosagemMedicamento = medComDoses
    }

    override fun getViewHolderInstance(): DetalhesMedicamentoAdapter.ViewHolder? {
        return viewHolder
    }

    override fun updateRecyclerViewOnDateChange(diaAtualSelecionado: String) {
        Log.d("testelistadedoses", "eu to aqui no metodo de update $diaAtualSelecionado")

        dataAtualSelecionada = diaAtualSelecionado
        limparListaDoses()
        populateListaDoses(listaDosagemMedicamento)
        configureList()
        notifyDataSetChanged()
    }

    override fun atualizarDose(doses: Doses) {
        if (listaDoses.isNotEmpty()) {
            if (listaDoses.size == 1) {
                //lista só tem uma dose
                if (listaDoses[0].idDose == doses.idDose) {
                    //index 0 precisa receber a dose atualizada
                    //preciso atualizar o item da recycler view e notificar para a lista ser atualizada
                    listaDoses[0] = doses
                    notifyItemChanged(0)
                }
            } else {
                //lista tem mais de uma dose
                for (i in listaDoses.indices) {
                    if (listaDoses[i].idDose == doses.idDose) {
                        // Preciso pegar o index do item
                        val index = i
                        // Preciso colocar a dose recebida no parâmetro no index da lista
                        listaDoses[index] = doses
                        // Preciso atualizar o item da RecyclerView e notificar para a lista ser atualizada
                        notifyItemChanged(index)
                        break
                    }
                }
            }

        }
    }

    private fun limparListaDoses() {
        listaDoses.clear()
    }


    private fun populateListaDoses(listaDosagemMedicamento: MedicamentoComDoses) {
        listaDoses.addAll(listaDosagemMedicamento.listaDoses)


    }

    private fun configureList() {

        var listaAuxiliar = ArrayList<Doses>()
        listaAuxiliar.addAll(listaDoses)
        listaDoses.clear()
        for (i in 0..listaAuxiliar.size - 1){
            if(dataAtualSelecionada == listaAuxiliar[i].horarioDose.subSequence(0,10)){
                listaDoses.add(listaAuxiliar[i])
            }
        }

        listaDoses.forEach {
            Log.d("testelistadedoses", "eu to no configure list: ${it.nomeMedicamento} ${it.horarioDose}")

        }

    }


    inner class ViewHolder(val binding: ItemDetalhesMedicamentosBinding): RecyclerView.ViewHolder(binding.root), ConexaoBindingAdapterDetalhesMedicamentos  {
        fun bind(doses: Doses){
            Log.d("atualizandorv", "eu entrei aqui no bind onde o item será atualizado ${doses.horarioDose} ${doses.jaTomouDose}")
            setDosageTakenOrNotTaken(doses)
            setColors()


            binding.itemMedicamento.setOnClickListener {
                detalhesMedicamentosAdapterInterface.onDoseClick(doses)
            }
            binding.ivStatusDosage.setOnClickListener {
              detalhesMedicamentosAdapterInterface.onDoseImageViewClick(doses)
            }

            setNomeMedicamentoOnItemTextView(doses)

            binding.timeDosage.text = calendarHelper2.formatarDataHoraSemSegundos2(doses.horarioDose)
            //formatarHorarioDoseSeHorarioDoseSize15(doses)
            //formatarHorarioDoseSeHorarioDoseSize16(doses)









        }

        private fun setColors() {
            val attribute = android.R.attr.textColorPrimary
            val typedValue = TypedValue()
            context.requireContext().theme.resolveAttribute(attribute, typedValue, true)
            val textColorPrimary = ContextCompat.getColor(context.requireContext(), typedValue.resourceId)

            binding.ivStatusDosage.borderColor = textColorPrimary
        }

        private fun formatarHorarioDoseSeHorarioDoseSize16(doses: Doses) {
            if(doses.horarioDose.length == 16){

                binding.timeDosage.text = doses.horarioDose
                Log.d("testdosestr", "${doses.nomeMedicamento} ${doses.horarioDose}")


            }

        }

        private fun formatarHorarioDoseSeHorarioDoseSize15(doses: Doses) {
            if(doses.horarioDose.length == 15){
                binding.timeDosage.text = doses.horarioDose
                Log.d("testdosestr", "${doses.nomeMedicamento} ${doses.horarioDose}")

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
        detalhesMedicamentosAdapterInterface.setViewHolderLiveDataValue(viewHolder!!)
        return viewHolder as ViewHolder

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("testelistadedoses", "to aqui no onBindViewHolder")

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