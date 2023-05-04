package com.rubens.applembretemedicamento.presentation.recyclerviewadapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.databinding.MedicamentoBinding
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiver
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiverInterface
import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.domain.MedicamentoManager
import com.rubens.applembretemedicamento.presentation.FragmentListaMedicamentosDirections
import com.rubens.applembretemedicamento.presentation.interfaces.AdapterListaMedicamentosInterface
import com.rubens.applembretemedicamento.presentation.interfaces.FragmentListaMedicamentosInterface
import kotlin.collections.ArrayList

class AdapterListaMedicamentos(
    private val list: ArrayList<MedicamentoComDoses>,
    val context: Context
) : RecyclerView.Adapter<AdapterListaMedicamentos.ViewHolder>(), AdapterListaMedicamentosInterface {
    private lateinit var alarmReceiverInterface: AlarmReceiverInterface
    private var alarmReceiver: AlarmReceiver = AlarmReceiver()
    private var listaIdMedicamentos: ArrayList<Int> = ArrayList()
    private lateinit var fragmentListaMedicamentosInterface: FragmentListaMedicamentosInterface


    private lateinit var medicamentoDoseDao: MedicamentoDao
    var listaComDosesToast: MutableLiveData<List<Doses>> = MutableLiveData()

    private val medicamentoManager: MedicamentoManager = MedicamentoManager()

    init {
        initFragmentListaInterface(context)
        fragmentListaMedicamentosInterface.initDb()
        initAlarmReceiverInterface()
        idMedicamentoTocandoObserver()
    }

    private fun initFragmentListaInterface(context: Context) {
        fragmentListaMedicamentosInterface = context as FragmentListaMedicamentosInterface

    }

    private fun idMedicamentoTocandoObserver() {

        alarmReceiverInterface.getAlarmeTocandoLiveData().observe(context as LifecycleOwner) {

        }
    }

    private fun initAlarmReceiverInterface() {
        alarmReceiverInterface = alarmReceiver
    }


    inner class ViewHolder(val binding: MedicamentoBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(medicamento: MedicamentoComDoses) {
            //pega o horario da proxima dose para mostrar no item
            pegarHorarioProximaDose(medicamento)


        }

        private fun pegarHorarioProximaDose(medicamento: MedicamentoComDoses) {
            var proxDose: String? = null
            var intervaloEntreDoses = 0.0
            var definiuProxDose = false
            for (i in 0..medicamento.listaDoses.size - 1) {
                //pega primeira dose da lista e seleciona o intervalo
                intervaloEntreDoses = medicamento.listaDoses.get(i).intervaloEntreDoses
                val dose = medicamento.listaDoses.get(i)
                if (!dose.jaTomouDose && !definiuProxDose) {
                    proxDose = dose.horarioDose
                    definiuProxDose = true
                }
                checarSeAlarmeDoMedicamentoEstaAtivado(medicamento)

                if (dose.jaTomouDose && i != medicamento.listaDoses.size - 1) {
                    definiuProxDose = false
                } else if (i == medicamento.listaDoses.size - 1 && dose.jaTomouDose) {
                    proxDose = medicamento.medicamentoTratamento.horaPrimeiraDose
                    fragmentListaMedicamentosInterface.launchCoroutineScope(medicamento)


                }

            }

            formataStringProximaDoseIfStringSize15(proxDose)
            formataStringProximaDoseIfStringSize16(proxDose)
            initDb()
            initClickListeners(proxDose, intervaloEntreDoses, medicamento)
            setMedNameOnItem(medicamento)

        }

        private fun setMedNameOnItem(medicamento: MedicamentoComDoses) {
            binding.medName.text = medicamento.medicamentoTratamento.nomeMedicamento


        }

        private fun initClickListeners(
            proxDose: String?,
            intervaloEntreDoses: Double,
            medicamento: MedicamentoComDoses
        ) {
            binding.medicamento.setOnClickListener {
                fragmentListaMedicamentosInterface.onMedicamentoClick(
                    proxDose,
                    intervaloEntreDoses,
                    medicamento,
                    medicamentoManager
                )
            }

        }

        private fun initDb() {
            fragmentListaMedicamentosInterface.initDb()
            medicamentoDoseDao = fragmentListaMedicamentosInterface.getMedicamentoDao()
        }



        private fun formataStringProximaDoseIfStringSize16(proxDose: String?) {
            if (proxDose != null) {
                Log.d("testeformatadapter", "to no if ${proxDose.length}")

                if (proxDose.length == 16) {
                    Log.d("testeformatadapter", "to no if length == 18")

                    binding.horaProximaDose.text = proxDose.subSequence(11, 16)
                }
            }

        }

        private fun formataStringProximaDoseIfStringSize15(proxDose: String?) {
            if (proxDose != null) {
                Log.d("testeformatadapter", "to no if ${proxDose.length}")
                if (proxDose.length == 15) {
                    Log.d("testeformatadapter", "to no if length é 17")

                    binding.horaProximaDose.text = proxDose.subSequence(11, 15)
                }
            }

        }


        private fun checarSeAlarmeDoMedicamentoEstaAtivado(medicamento: MedicamentoComDoses) {
            if (medicamento.medicamentoTratamento.alarmeAtivado) {
                showReloginhoDeAlarmeAtivado()
                checkIfMediaPlayerIsPlaying(medicamento)

            }

        }

        private fun checkIfMediaPlayerIsPlaying(medicamento: MedicamentoComDoses) {
            if (alarmReceiverInterface.getMediaPlayerInstance().isPlaying) {
                listaIdMedicamentos.forEach { id ->
                    if (medicamento.medicamentoTratamento.idMedicamento == id && id > -1) {
                        initShakingClockAnimation()

                    }

                }

            }


        }

        private fun initShakingClockAnimation() {
            val shake = AnimationUtils.loadAnimation(binding.root.context, R.anim.shake)
            binding.alarmeAtivado.startAnimation(shake)

        }

        private fun showReloginhoDeAlarmeAtivado() {
            binding.alarmeAtivado.visibility = View.VISIBLE

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MedicamentoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val medicamento = list.get(position)
        holder.bind(medicamento)

    }


    override fun getItemCount(): Int {
        Log.d("testehoranot", "list size: ${list.size}")
        return list.size
    }

    override fun getListaIdMedicamentosFromAdapterListaMedicamentos(): ArrayList<Int> {
        return listaIdMedicamentos
    }

    override fun removeFromListaIdMedicamentosFromListaAdapter(id: Int) {
        listaIdMedicamentos.remove(id)
    }


}

