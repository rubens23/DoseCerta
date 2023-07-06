package com.rubens.applembretemedicamento.presentation.recyclerviewadapters

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.databinding.MedicamentoBinding
import com.rubens.applembretemedicamento.utils.AlarmUtilsInterface
import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.domain.MedicamentoManager
import com.rubens.applembretemedicamento.presentation.FragmentListaMedicamentos
import com.rubens.applembretemedicamento.presentation.interfaces.AdapterListaMedicamentosInterface
import com.rubens.applembretemedicamento.presentation.interfaces.FragmentListaMedicamentosInterface
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.collections.ArrayList

class AdapterListaMedicamentos(
    private val list: ArrayList<MedicamentoComDoses>,
    val fragmentListaInstance: FragmentListaMedicamentos,
    val context: Context,
    val medicamentoManager: MedicamentoManager,
    private val alarmReceiver: AlarmUtilsInterface,
    private val is24HourFormat: Boolean
) : RecyclerView.Adapter<AdapterListaMedicamentos.ViewHolder>(), AdapterListaMedicamentosInterface {
    private lateinit var alarmUtilsInterface: AlarmUtilsInterface
    private var listaIdMedicamentosTocandoNoMomento: ArrayList<Int> = ArrayList()
    private lateinit var fragmentListaMedicamentosInterface: FragmentListaMedicamentosInterface
    private var mediaPlayer = MediaPlayer.create(fragmentListaInstance.requireContext(), Settings.System.DEFAULT_RINGTONE_URI)


    private lateinit var medicamentoDoseDao: MedicamentoDao


    init {
        initFragmentListaInterface(fragmentListaInstance)
        fragmentListaMedicamentosInterface.initDb()
        initAlarmReceiverInterface()
        idMedicamentoTocandoObserver()
    }

    private fun initFragmentListaInterface(context: FragmentListaMedicamentos) {
        fragmentListaMedicamentosInterface = context

    }

    private fun idMedicamentoTocandoObserver() {

        alarmUtilsInterface.getAlarmeTocandoLiveData().observe(fragmentListaInstance as LifecycleOwner) {

        }
    }

    private fun initAlarmReceiverInterface() {
        alarmUtilsInterface = alarmReceiver
    }


    inner class ViewHolder(val binding: MedicamentoBinding) :
        RecyclerView.ViewHolder(binding.root) {


        private var shake: Animation? = null

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
                    Log.d("logdoparse2", dose.horarioDose)
                    proxDose = doseMaisProximaNaoTomada(dose, medicamento.listaDoses).horarioDose
                    definiuProxDose = true
                    Log.d("bugcorretion", "proxDoseDifinida == ${proxDose}")
                }
                checarSeAlarmeDoMedicamentoEstaAtivado(medicamento)

                if (dose.jaTomouDose && i != medicamento.listaDoses.size - 1) {
                    definiuProxDose = false
                } else if (i == medicamento.listaDoses.size - 1 && dose.jaTomouDose) {
                    //proxDose = medicamento.medicamentoTratamento.horaPrimeiraDose
                    fragmentListaMedicamentosInterface.launchCoroutineScope(medicamento)
                    binding.horaProximaDose.text = context.getString(R.string.finished)
                    Log.d("testeinserthistorico", "eu to aqui no else if do adapter")



                }

            }



            if (proxDose != null){
                binding.horaProximaDose.text = pegarSoParteDaHora(proxDose)

            }
            //formataStringProximaDoseIfStringSize15(proxDose)
            //formataStringProximaDoseIfStringSize16(proxDose)
            initDb()
            initClickListeners(proxDose, intervaloEntreDoses, medicamento)
            Log.d("achandoatrihora", "eu to aqui no metodo do adapter da lista de medicamentos 'pegarHorarioProximaDose' $proxDose")

            setMedNameOnItem(medicamento)

        }

        private fun pegarSoParteDaHora(proxDose: String): String {
            Log.d("testproxDose", proxDose)
            return proxDose.substringAfter(" ")

        }

        private fun doseMaisProximaNaoTomada(dose: Doses, listaDoses: List<Doses>): Doses{

            var doseMaisProximaNaoTomada = dose
            Log.d("achandoatrihora", "eu to aqui no metodo doseMaisProximaNaoTomada do adapter da lista de medicamentos ${doseMaisProximaNaoTomada}")

            for(i in 0..listaDoses.size - 1){
                if(listaDoses[i] == dose){
                    for(z in i until listaDoses.size){
                        if(isPrimeiraDoseMaisProximoQueSegunda(doseMaisProximaNaoTomada, listaDoses[z])){

                            //doseMaisProximaNaoTomada é mais proxima que listaDoses[z]
                            Log.d("achandoatrihora", "eu to aqui no metodo doseMaisProximaNaoTomada do adapter da lista isPrimeiraDoseMaisProximoQueSegunda true")

                        }else{
                            //listaDoses[z] é mais proxima que doseMaisProximaNaoTomada
                            Log.d("achandoatrihora", "eu to aqui no metodo doseMaisProximaNaoTomada do adapter da lista isPrimeiraDoseMaisProximoQueSegunda false")

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
            var formatoDataHora: SimpleDateFormat

            if(is24HourFormat){
                formatoDataHora = SimpleDateFormat("dd/MM/yyyy HH:mm")

            }else{
                formatoDataHora = SimpleDateFormat("dd/MM/yyyy h:mm a")

            }

            val horarioAtual = Calendar.getInstance().time

            val primeiraDataHora = formatoDataHora.parse(primeiraDose.horarioDose)
            val segundaDataHora = formatoDataHora.parse(segundaDose.horarioDose)

            ///30/05/2023 10:00  10ms         30/05/2023  10:45  23ms
            val diffPrimeiraDose = Math.abs(primeiraDataHora.time - horarioAtual.time)
            val diffSegundaDose = Math.abs(segundaDataHora.time - horarioAtual.time)

            Log.d("achandoatrihora", "isPrimeiraDoseMaisProximaQueSegunda: primeira: ${primeiraDose.horarioDose} segunda:${segundaDose.horarioDose}? ${diffPrimeiraDose > diffSegundaDose}")
            Log.d("achandoatrihora", "horario atual: ${horarioAtual.time}")
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
                Log.d("testefimdoses", "$proxDose")

                Log.d("testeformatadapter", "to no if ${proxDose.length}")

                if (proxDose.length == 16) {
                    Log.d("testeformatadapter", "to no if length == 18")

                    binding.horaProximaDose.text = proxDose.subSequence(11, 16)
                }else{
                    Log.d("testefimdoses", "as doses acabaram")

                }
            }

        }

        private fun formataStringProximaDoseIfStringSize15(proxDose: String?) {
            if (proxDose != null) {
                Log.d("testefimdoses", "$proxDose")

                Log.d("testeformatadapter", "to no if ${proxDose.length}")
                if (proxDose.length == 15) {
                    Log.d("testeformatadapter", "to no if length é 17")

                    binding.horaProximaDose.text = proxDose.subSequence(11, 15)
                }else{
                    Log.d("testefimdoses", "as doses acabaram")

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
                if(fragmentListaMedicamentosInterface.getMediaPlayerInstance() != null){
                    if (fragmentListaMedicamentosInterface.getMediaPlayerInstance()!!.isPlaying) {
                        initShakingClockAnimation()

                    }
                }

            }else{
                stopShakingClockAnimation()


            }



        }

        private fun stopShakingClockAnimation() {
            if(shake != null){
                if (shake!!.hasStarted()){
                    shake!!.cancel()

                }
            }

        }

        private fun initShakingClockAnimation() {
            shake = AnimationUtils.loadAnimation(binding.root.context, R.anim.shake)
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

