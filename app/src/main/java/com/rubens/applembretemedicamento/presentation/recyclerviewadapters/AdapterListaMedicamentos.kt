package com.rubens.applembretemedicamento.presentation.recyclerviewadapters

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.databinding.MedicamentoBinding
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiverInterface
import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.domain.MedicamentoManager
import com.rubens.applembretemedicamento.framework.singletons.AlarmReceiverSingleton
import com.rubens.applembretemedicamento.presentation.FragmentListaMedicamentos
import com.rubens.applembretemedicamento.presentation.interfaces.AdapterListaMedicamentosInterface
import com.rubens.applembretemedicamento.presentation.interfaces.FragmentListaMedicamentosInterface
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.collections.ArrayList

class AdapterListaMedicamentos(
    private val list: ArrayList<MedicamentoComDoses>,
    val context: FragmentListaMedicamentos
) : RecyclerView.Adapter<AdapterListaMedicamentos.ViewHolder>(), AdapterListaMedicamentosInterface {
    private lateinit var alarmReceiverInterface: AlarmReceiverInterface
    //private var alarmReceiver: AlarmReceiver = AlarmReceiverSingleton.getInstance()
    private var listaIdMedicamentosTocandoNoMomento: ArrayList<Int> = ArrayList()
    private lateinit var fragmentListaMedicamentosInterface: FragmentListaMedicamentosInterface
    private var mediaPlayer = MediaPlayer.create(context.requireContext(), Settings.System.DEFAULT_RINGTONE_URI)


    private lateinit var medicamentoDoseDao: MedicamentoDao
    var listaComDosesToast: MutableLiveData<List<Doses>> = MutableLiveData()

    private val medicamentoManager: MedicamentoManager = MedicamentoManager()

    init {
        initFragmentListaInterface(context)
        fragmentListaMedicamentosInterface.initDb()
        initAlarmReceiverInterface()
        idMedicamentoTocandoObserver()
    }

    private fun initFragmentListaInterface(context: FragmentListaMedicamentos) {
        fragmentListaMedicamentosInterface = context

    }

    private fun idMedicamentoTocandoObserver() {

        alarmReceiverInterface.getAlarmeTocandoLiveData().observe(context as LifecycleOwner) {

        }
    }

    private fun initAlarmReceiverInterface() {
        alarmReceiverInterface = AlarmReceiverSingleton.getInstance()
    }


    inner class ViewHolder(val binding: MedicamentoBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(medicamento: MedicamentoComDoses) {
            //pega o horario da proxima dose para mostrar no item
            pegarHorarioProximaDose(medicamento)
            Log.d("testeshakingclock", "medicamento atual: ${medicamento.medicamentoTratamento.nomeMedicamento} alarme tocando: ${medicamento.medicamentoTratamento.alarmeTocando}")





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
                    proxDose = doseMaisProximaNaoTomada(dose, medicamento.listaDoses).horarioDose
                    definiuProxDose = true
                    Log.d("bugcorretion", "proxDoseDifinida == ${proxDose}")
                }
                checarSeAlarmeDoMedicamentoEstaAtivado(medicamento)

                if (dose.jaTomouDose && i != medicamento.listaDoses.size - 1) {
                    definiuProxDose = false
                } else if (i == medicamento.listaDoses.size - 1 && dose.jaTomouDose) {
                    proxDose = medicamento.medicamentoTratamento.horaPrimeiraDose
                    fragmentListaMedicamentosInterface.launchCoroutineScope(medicamento)
                    Log.d("testeinserthistorico", "eu to aqui no else if do adapter")



                }

            }



            formataStringProximaDoseIfStringSize15(proxDose)
            formataStringProximaDoseIfStringSize16(proxDose)
            initDb()
            initClickListeners(proxDose, intervaloEntreDoses, medicamento)
            setMedNameOnItem(medicamento)

        }

        private fun doseMaisProximaNaoTomada(dose: Doses, listaDoses: List<Doses>): Doses{

            var doseMaisProximaNaoTomada = dose
            for(i in 0..listaDoses.size - 1){
                if(listaDoses[i] == dose){
                    for(z in i until listaDoses.size){
                        if(isPrimeiraDoseMaisProximoQueSegunda(doseMaisProximaNaoTomada, listaDoses[z])){
                            //doseMaisProximaNaoTomada é mais proxima que listaDoses[z]
                            Log.d("updatemethod", "if: dose que sera passada la para o metodo: ${doseMaisProximaNaoTomada.horarioDose}")
                        }else{
                            //listaDoses[z] é mais proxima que doseMaisProximaNaoTomada
                            if(!listaDoses[z].jaTomouDose){
                                doseMaisProximaNaoTomada = listaDoses[z]
                                Log.d("updatemethod", "else -> if: dose que sera passada la para o metodo: ${listaDoses[z].horarioDose}")

                            }
                        }
                    }
                }
            }
            Log.d("updatemethod", "dose que foi retornada: ${doseMaisProximaNaoTomada.horarioDose}")

            return doseMaisProximaNaoTomada
        }

        @SuppressLint("SimpleDateFormat")
        private fun isPrimeiraDoseMaisProximoQueSegunda(primeiraDose: Doses, segundaDose: Doses): Boolean{
            Log.d("updatemethod2", "isPrimeiraDoseMaisProximaQueSegunda? primeiraDose: ${primeiraDose.horarioDose} segundaDose: ${segundaDose.horarioDose}")

            val formatoDataHora = SimpleDateFormat("dd/MM/yyyy HH:mm")
            val horarioAtual = Calendar.getInstance().time

            val primeiraDataHora = formatoDataHora.parse(primeiraDose.horarioDose)
            val segundaDataHora = formatoDataHora.parse(segundaDose.horarioDose)

            ///30/05/2023 10:00  10ms         30/05/2023  10:45  23ms
            val diffPrimeiraDose = Math.abs(primeiraDataHora.time - horarioAtual.time)
            val diffSegundaDose = Math.abs(segundaDataHora.time - horarioAtual.time)

            Log.d("updatemethod2", "isPrimeiraDoseMaisProximaQueSegunda: primeira: ${diffPrimeiraDose} segunda:${diffSegundaDose}? ${diffPrimeiraDose > diffSegundaDose}")
            return diffPrimeiraDose < diffSegundaDose




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
                Log.d("testeshakingclock", "alarme ativado")
                showReloginhoDeAlarmeAtivado()
                checkIfMediaPlayerIsPlaying(medicamento)

            }else{
                Log.d("testeshakingclock", "alarme desativado")
                hideReloginhoDeAlarmeAtivado()

            }

        }

        private fun hideReloginhoDeAlarmeAtivado() {
            binding.alarmeAtivado.visibility = View.GONE
        }

        private fun checkIfMediaPlayerIsPlaying(medicamento: MedicamentoComDoses) {
            if (medicamento.medicamentoTratamento.alarmeTocando){
                Log.d("alarmetocando", "alarme tocando? ${medicamento.medicamentoTratamento.alarmeTocando}")

                if(fragmentListaMedicamentosInterface.getMediaPlayerInstance() != null){
                    Log.d("alarmetocando", "media player instance is not null ${fragmentListaMedicamentosInterface.getMediaPlayerInstance()}")


                    if (fragmentListaMedicamentosInterface.getMediaPlayerInstance()!!.isPlaying) {


                        initShakingClockAnimation()
                        Log.d("alarmetocando", "iniciei a shaking animation")


                    }else{
                        Log.d("alarmetocando", "media player não está tocando $${fragmentListaMedicamentosInterface.getMediaPlayerInstance()}")

                    }
                }else{
                    Log.d("alarmetocando", "media player instance is null")

                }

            }else{
                Log.d("alarmetocando", "alarme tocando? ${medicamento.medicamentoTratamento.alarmeTocando}")

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
        return listaIdMedicamentosTocandoNoMomento
    }

    override fun removeFromListaIdMedicamentosFromListaAdapter(id: Int) {
        listaIdMedicamentosTocandoNoMomento.remove(id)
    }

    override fun setMediaPlayerInstance(mp: MediaPlayer) {
        mediaPlayer = mp
    }


}

