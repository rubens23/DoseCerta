package com.rubens.applembretemedicamento.presentation.recyclerviewadapters

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.databinding.ItemDetalhesMedicamentosBinding
import com.rubens.applembretemedicamento.framework.data.AppDatabase
import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.presentation.MainActivity
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.comunicacaoFragmentAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DetalhesMedicamentoAdapter(private val listaDosagemMedicamento: MedicamentoComDoses, val fecharFragment: comunicacaoFragmentAdapter): RecyclerView.Adapter<DetalhesMedicamentoAdapter.ViewHolder>(), CalendarHelper {
    private var db: AppDatabase? = null
    private lateinit var medicamentoDoseDao: MedicamentoDao
    private var listaDoses: ArrayList<Doses> = ArrayList()



    init {
        listaDoses.addAll(listaDosagemMedicamento.listaDoses)
        configureList()


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


    inner class ViewHolder(val binding: ItemDetalhesMedicamentosBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ServiceCast", "SetTextI18n")
        fun bind(doses: Doses){
            db = AppDatabase.getAppDatabase(binding.root.context)
            medicamentoDoseDao = db!!.medicamentoDao





            if(doses.jaTomouDose){
                Log.d("controlebolinhas","tomou a dose das ${doses.horarioDose}")
                binding.ivStatusDosage.setImageResource(R.drawable.med_taken)


            }else{
                binding.ivStatusDosage.setImageResource(R.drawable.med_not_taken)

            }

            /*
            MainActivity.binding.btnDeleteMedicamento.setOnClickListener {
                Log.d("testecliquedelete", "eu cliquei no botão de excluir medicamento")
                val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(binding.root.context)
                alert.setTitle("${doses.nomeMedicamento}")
                alert.setMessage("Tem certeza que deseja deletar o medicamento ${doses.nomeMedicamento}?")
                Log.d("testehora", "${doses.horarioDose}")
                alert.setPositiveButton("Sim", DialogInterface.OnClickListener { dialog, which ->

                    GlobalScope.launch {
                        medicamentoDoseDao.deleteMedicamentoFromMedicamentoTratamento(doses.nomeMedicamento)
                        medicamentoDoseDao.deleteDosesDoMedicamentoFinalizado(doses.nomeMedicamento)
                        fecharFragment.markToastAsNotShown()
                        fecharFragment.deleteDataStoreByKey()


                    }

                    dialog.dismiss()
                    fecharFragment.cancelarBroadcastReceiver()
                    fecharFragment.mostrarToastExcluido(doses.nomeMedicamento)
                    fecharFragment.fecharFragment()


                })

                alert.setNegativeButton("Não",
                    DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                        fecharFragment.initIntertitialAd()



                    })

                alert.show()



            }

             */



            binding.itemMedicamento.setOnClickListener {
                if(!doses.jaTomouDose){
                    //mostrar o dialog confirmando a dose a ser tomada
                    val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(binding.root.context)
                    alert.setTitle("Tomar ${doses.nomeMedicamento}")
                    alert.setMessage("Você quer tomar a dose de ${doses.horarioDose} agora?")
                    Log.d("testehora", "${doses.horarioDose}")
                    alert.setPositiveButton("Sim", DialogInterface.OnClickListener { dialog, which ->

                        GlobalScope.launch {
                            medicamentoDoseDao.tomarDoseMedicamento(true, doses.idDose)
                        }
                        binding.ivStatusDosage.setImageResource(R.drawable.med_taken)

                        dialog.dismiss()
                    })

                    alert.setNegativeButton("Não",
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss() })

                    alert.show()
                }else{
                    //mostrar o dialog confirmando a dose a ser tomada
                    val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(binding.root.context)
                    alert.setTitle("Dose Tomada!")
                    alert.setMessage("Você já tomou a dose das ${doses.horarioDose}!")
                    alert.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->


                        dialog.dismiss()
                    })


                    alert.show()

                }

            }
            binding.ivStatusDosage.setOnClickListener {
                Log.d("testecliquedose", "${doses.horarioDose} ${doses.jaTomouDose}")
                if(!doses.jaTomouDose){
                    //mostrar o dialog confirmando a dose a ser tomada
                    val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(binding.root.context)
                    alert.setTitle("Tomar ${doses.nomeMedicamento}")
                    alert.setMessage("Você quer tomar a dose de ${doses.horarioDose} agora?")
                    alert.setPositiveButton("Sim", DialogInterface.OnClickListener { dialog, which ->

                        GlobalScope.launch {
                            medicamentoDoseDao.tomarDoseMedicamento(true, doses.idDose)
                        }
                        binding.ivStatusDosage.setImageResource(R.drawable.med_taken)

                        dialog.dismiss()
                    })

                    alert.setNegativeButton("Não",
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss() })

                    alert.show()
                }else{
                    //mostrar o dialog confirmando a dose a ser tomada
                    val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(binding.root.context)
                    alert.setTitle("Dose Tomada!")
                    alert.setMessage("Você já tomou a dose das ${doses.horarioDose}!")
                    alert.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->


                        dialog.dismiss()
                    })


                    alert.show()

                }


            }





            binding.tvDetailMedicineName.text = doses.nomeMedicamento
            /*
            if(doses.horarioDose[0].toString() == "2" && doses.horarioDose[1].toString() == "4"){
                binding.timeDosage.text = "00:"+doses.horarioDose[3]+doses.horarioDose[4]

            }else{
                binding.timeDosage.text = doses.horarioDose
            }

             */
            if(doses.horarioDose.length == 15){
                Log.d("testeformatadapter", "to no if length é 17")


                //binding.timeDosage.text = doses.horarioDose.subSequence(11,15)
                binding.timeDosage.text = doses.horarioDose
            }

            if(doses.horarioDose.length == 16){
                Log.d("testeformatadapter", "to no if length é 17")

                //binding.timeDosage.text = doses.horarioDose.subSequence(11,16)
                binding.timeDosage.text = doses.horarioDose
            }






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