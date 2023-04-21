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
import com.rubens.applembretemedicamento.utils.comunicacaoFragmentAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DetalhesMedicamentoAdapter(private val listaDosagemMedicamento: MedicamentoComDoses, val fecharFragment: comunicacaoFragmentAdapter): RecyclerView.Adapter<DetalhesMedicamentoAdapter.ViewHolder>() {
    private var db: AppDatabase? = null
    private lateinit var medicamentoDoseDao: MedicamentoDao





    inner class ViewHolder(val binding: ItemDetalhesMedicamentosBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ServiceCast", "SetTextI18n")
        fun bind(doses: Doses){
            db = AppDatabase.getAppDatabase(binding.root.context)
            medicamentoDoseDao = db!!.medicamentoDao

            Log.d("tentandoachardoses","tomou a dose das ${doses.horarioDose}")

            //como eu vou manter a bolinha verde se la no banco de dados consta como false?
            //vou ter que fazer outro metodo para deixar a bolinha verde só nessas "ocasioes especiais"
            if(doses.jaTomouDose){
                Log.d("controlebolinhas","tomou a dose das ${doses.horarioDose}")
                binding.ivStatusDosage.setImageResource(R.drawable.med_taken)


            }else{
                binding.ivStatusDosage.setImageResource(R.drawable.med_not_taken)

            }

            MainActivity.binding.btnDeleteMedicamento.setOnClickListener {
                val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(binding.root.context)
                alert.setTitle("${doses.nomeMedicamento}")
                alert.setMessage("Tem certeza que deseja deletar o medicamento ${doses.nomeMedicamento}?")
                Log.d("testehora", "${doses.horarioDose}")
                alert.setPositiveButton("Sim", DialogInterface.OnClickListener { dialog, which ->

                    GlobalScope.launch {
                        medicamentoDoseDao.deleteMedicamentoFromMedicamentoTratamento(doses.nomeMedicamento)
                        medicamentoDoseDao.deleteDosesDoMedicamentoFinalizado(doses.nomeMedicamento)

                    }

                    dialog.dismiss()
                    fecharFragment.mostrarToastExcluido(doses.nomeMedicamento)
                    fecharFragment.fecharFragment()


                })

                alert.setNegativeButton("Não",
                    DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss() })

                alert.show()

            }



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
            doses.horarioDose[0]
            if(doses.horarioDose[0].toString() == "2" && doses.horarioDose[1].toString() == "4"){
                binding.timeDosage.text = "00:"+doses.horarioDose[3]+doses.horarioDose[4]
            }else{
                binding.timeDosage.text = doses.horarioDose
            }





        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDetalhesMedicamentosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val medicamento = listaDosagemMedicamento.listaDoses[position]
        holder.bind(medicamento)

    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }


    override fun getItemCount(): Int = listaDosagemMedicamento.listaDoses.size

}