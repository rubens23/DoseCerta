package com.rubens.applembretemedicamento.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.rubens.applembretemedicamento.databinding.FragmentDetalhesMedicamentosBinding
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiver
import com.rubens.applembretemedicamento.framework.data.AppDatabase
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import com.rubens.applembretemedicamento.framework.viewModels.ViewModelFragmentCadastrarNovoMedicamento
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.AdapterListaMedicamentos
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.DetalhesMedicamentoAdapter
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.comunicacaoFragmentAdapter
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import java.io.Serializable


class FragmentDetalhesMedicamentos : Fragment(), FuncoesDeTempo, CalendarHelper, comunicacaoFragmentAdapter {


    private lateinit var extra: Serializable
    private lateinit var receiver: AlarmReceiver
    private var listaDoses  = arrayListOf<Doses>()
    private lateinit var adapter : DetalhesMedicamentoAdapter
    private var db: AppDatabase? = null
    private var medicamentoAdicionadoObserver: MutableLiveData<MedicamentoTratamento> = MutableLiveData()
    private var excluirDaListaDeMedicamentosNoAlarme: MutableLiveData<Int> = MutableLiveData()
    private var intervaloEntreDoses = 0.0
    lateinit var viewModel: ViewModelFragmentCadastrarNovoMedicamento

    private val args: FragmentDetalhesMedicamentosArgs by navArgs()



    companion object{
        lateinit var binding: FragmentDetalhesMedicamentosBinding
        var nomeMedicamento = ""
        var horaProxDose: String? = null
        lateinit var medicamento: MedicamentoTratamento

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetalhesMedicamentosBinding.inflate(inflater)

        setupToolbar()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()

        initAds()

        createNotificationChannel()

        observers()

        db = AppDatabase.getAppDatabase(requireContext())


        //val intent = requireActivity().intent
        //extra = intent.getSerializableExtra("medicamento")!!
        extra = args.medicamento
        val extraStringHorario = args.horaproximadose
        Log.d("testeextraproxdose", "${extraStringHorario}")
        horaProxDose = extraStringHorario
        intervaloEntreDoses = args.intervaloentredoses.toDouble()





        adapter = DetalhesMedicamentoAdapter(extra as MedicamentoComDoses, this)
        medicamentoAdicionadoObserver.postValue((extra as MedicamentoComDoses).medicamentoTratamento)
        binding.dataInicioTratamento.text = (extra as MedicamentoComDoses).medicamentoTratamento.dataInicioTratamento
        binding.dataTerminoTratamento.text = (extra as MedicamentoComDoses).medicamentoTratamento.dataTerminoTratamento
        binding.medDetalhesRecyclerView.adapter = adapter





        onClickListeners()
    }

    private fun observers() {
        excluirDaListaDeMedicamentosNoAlarme.observe(viewLifecycleOwner){
            AdapterListaMedicamentos.listaIdMedicamentos.remove(it)
        }
    }

    private fun setupToolbar() {
        MainActivity.binding.toolbar.visibility = View.VISIBLE
        MainActivity.binding.toolbar.title = ""
    }

    fun irParaFragmentLista(){
        findNavController().popBackStack()
    }

    private fun initAds() {
        MobileAds.initialize(requireContext()) {}


        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    override fun onStart() {
        super.onStart()

        medicamentoAdicionadoObserver.observe(this){
            medicamento = it
            nomeMedicamento = it.nomeMedicamento
            checarSeAlarmeEstaAtivado()


        }
    }

    private fun initViewModel() {

        viewModel = ViewModelProvider(requireActivity())[ViewModelFragmentCadastrarNovoMedicamento::class.java]
    }

    private fun checarSeAlarmeEstaAtivado() {
        if(medicamento.alarmeAtivado){
            if(AlarmReceiver.mp.isPlaying){
                Log.d("testeisplaying", "o mp esta tocando")
                //binding.btnCancelarAlarme.visibility = View.VISIBLE
                //binding.btnArmarAlarme.visibility = View.INVISIBLE
                //binding.btnArmarAlarme.isClickable = false
                binding.btnPararSom.visibility = View.VISIBLE
            }else{
                Log.d("testeisplaying", "o mp nao esta tocando")

                initializeAlarmManager()
            }

        }

    }

    private fun onClickListeners() {
        binding.btnArmarAlarme.setOnClickListener {
            viewModel.ligarAlarmeDoMedicamento(medicamento.nomeMedicamento, true)
            initializeAlarmManager()
        }
        binding.btnCancelarAlarme.setOnClickListener {
            Toast.makeText(requireContext(), "Alarme desativado!", Toast.LENGTH_SHORT).show()
            viewModel.ligarAlarmeDoMedicamento(medicamento.nomeMedicamento, false)
            receiver.cancelAlarm()


        }
        binding.btnPararSom.setOnClickListener {
            if (AlarmReceiver.mp.isPlaying){
                AlarmReceiver.mp.stop()
                binding.btnPararSom.visibility = View.GONE
                val listaAuxiliar = ArrayList<Int>()
                listaAuxiliar.addAll(AdapterListaMedicamentos.listaIdMedicamentos)
                listaAuxiliar.forEach {
                    if(AdapterListaMedicamentos.listaIdMedicamentos.contains(medicamento.idMedicamento)){
                        excluirDaListaDeMedicamentosNoAlarme.value = medicamento.idMedicamento
                        /*
                        quando os 2 "medicamentos" estiverem tocando faça o teste


                        abra teste 1 -> pare o som -> proximo alarme é setado -> volte para fragment principal -> reloginho teste 1 esta parado -> reloginho teste 2 continua se mexendo

                        parou o som dos dois.

                        nos alarmes seguintes o reloginho dos dois fica balançando

                        todo objetivo: se tiver só um medicamento com o alarme tocando, só um relóginho deve estar tocando, não dois.
                         */

                    }
                }


            }
            armarProximoAlarme()
        }
    }

    private fun armarProximoAlarme() {
        if((extra as MedicamentoComDoses).listaDoses.size > 0){


            val limiteFor = (extra as MedicamentoComDoses).listaDoses.size - 1

            iterarSobreDosesEAcharProxima(limiteFor)


        }

        Log.d("armarproximo6", "proximo Alarme: $horaProxDose")
        initializeAlarmManager()
    }

    private fun iterarSobreDosesEAcharProxima(limiteFor: Int) {
        for(i in 0..limiteFor){
            horaProxDose?.let {
                    horaProxDose->
                if(horaProxDose.length < 17){
                    if ((extra as MedicamentoComDoses).listaDoses[i].horarioDose + ":00" == horaProxDose +":00"){
                        Log.d("armarproximo7", "if 1 foram encontrados valores iguais!! horarioDose ${(extra as MedicamentoComDoses).listaDoses[i].horarioDose + ":00"} == horarioProxDose ${horaProxDose}:00")




                        if(i+1 == limiteFor){
                            //acabaram as doses

                        }else{
                            Companion.horaProxDose = (extra as MedicamentoComDoses).listaDoses[i+1].horarioDose + ":00"
                            return
                        }
                    }
                }else{

                    if ((extra as MedicamentoComDoses).listaDoses[i].horarioDose + ":00" == Companion.horaProxDose){
                        Log.d("armarproximo7", "if 2 foram encontrados valores iguais!! horarioDose ${(extra as MedicamentoComDoses).listaDoses[i].horarioDose + ":00"} == horarioProxDose $horaProxDose")

                        if(i+1 == limiteFor){
                            //acabaram as doses

                        }else{
                            Companion.horaProxDose = (extra as MedicamentoComDoses).listaDoses[i+1].horarioDose + ":00"
                            return

                        }
                    }
                }
            }


        }


    }

    private fun initializeAlarmManager() {



        if(AlarmReceiver.nomeMedicamento != ""){
            Log.d("alarmessimulta", "ja armei um alarme anteriormente")
        }else{
            Log.d("alarmessimulta", "ainda não armei nenhum alarme")
        }



        receiver = AlarmReceiver()


        receiver.horaProximaDoseObserver.observe(viewLifecycleOwner){
            if(!AlarmReceiver.mp.isPlaying){
                //horaProxDose = it
                //Toast.makeText(requireContext(), "Alarme ativado! próxima dose às: ${horaProxDose}", Toast.LENGTH_SHORT).show()
            }else{
                binding.btnCancelarAlarme.visibility = View.VISIBLE
                binding.btnArmarAlarme.visibility = View.INVISIBLE
                binding.btnArmarAlarme.isClickable = false
            }

        }

        var podeTocar = false

        horaProxDose?.let {
            hora->
            Log.d("smartalarm", "hora iterada $hora")
            var hr = hora

            //todo consertar isso. A proxima dose esta sendo as 5:10 ao inves das 23:10
            if(hora.length < 17){
                hr = hora+":00"
            }

            convertStringToDate(hr)?.let {
                Log.d("smartalarm", "data em millisegundos: $hora                       ${it.time}")
                Log.d("smartalarm", "current time em millis: ${System.currentTimeMillis()}")
                if(it.time >= System.currentTimeMillis()){
                    Log.d("smartalarm2", "essa dose ainda nao passou entao pode tocar: $hr")
                    podeTocar = true
                }else{
                    Log.d("smartalarm2", "essa dose ja passou $hr")
                    podeTocar = false

                }
            }


        }

        if(podeTocar){

            Toast.makeText(requireContext(), "Alarme ativado! próxima dose às: ${horaProxDose}", Toast.LENGTH_LONG).show()

            horaProxDose?.let { receiver.setAlarm2(intervaloEntreDoses, (extra as MedicamentoComDoses).medicamentoTratamento.idMedicamento, (extra as MedicamentoComDoses).listaDoses, requireContext(), it) }

        }else{
            armarProximoAlarme()

        }



    }

    private fun createNotificationChannel() {
        val importance = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager.IMPORTANCE_HIGH
        } else {
            TODO("VERSION.SDK_INT < N")
        }
        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel("something", "something notification channel", importance).apply {
                description = "Notification das doses"
            }
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        val notificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun fecharFragment() {
        findNavController().popBackStack()
    }

    override fun mostrarToastExcluido(nome: String) {
        Toast.makeText(requireContext(), "o medicamento $nome foi excluído", Toast.LENGTH_LONG).show()
    }

    override fun verificarSeDataJaPassou(dataFinalizacao: String): Boolean {
        return verificarSeDataJaPassou(medicamento.dataTerminoTratamento)

    }


}