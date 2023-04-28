package com.rubens.applembretemedicamento.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.MobileAds
import com.rubens.applembretemedicamento.databinding.FragmentDetalhesMedicamentosBinding
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiver
import com.rubens.applembretemedicamento.framework.data.AppDatabase
import com.rubens.applembretemedicamento.framework.data.MyDataStore
import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import com.rubens.applembretemedicamento.framework.viewModels.ViewModelFragmentCadastrarNovoMedicamento
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.AdapterListaMedicamentos
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.DetalhesMedicamentoAdapter
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.comunicacaoFragmentAdapter
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
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

    private lateinit var myDataStore: MyDataStore

    private var mInterstitial: InterstitialAd? = null

    private lateinit var medicamentoDoseDao: MedicamentoDao










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

        db = AppDatabase.getAppDatabase(requireContext())
        medicamentoDoseDao = db!!.medicamentoDao





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
        MainActivity.binding.btnDeleteMedicamento.visibility = View.VISIBLE
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

    override fun onResume() {
        super.onResume()

        setupToolbar()
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

                AlarmReceiver.listaIdMedicamentosTocandoNoMomento.forEach {
                    if(it == medicamento.idMedicamento){
                        binding.btnPararSom.visibility = View.VISIBLE
                    }
                }

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
            viewLifecycleOwner.lifecycleScope.launch {
                myDataStore.markToastAsNotShown(booleanPreferencesKey(medicamento.stringDataStore))
            }


        }
        binding.btnPararSom.setOnClickListener {

            viewLifecycleOwner.lifecycleScope.launch {
                myDataStore.markToastAsNotShown(booleanPreferencesKey(medicamento.stringDataStore))
            }
            if (AlarmReceiver.mp.isPlaying){
                AlarmReceiver.mp.stop()
                binding.btnPararSom.visibility = View.GONE
                val listaAuxiliar = ArrayList<Int>()
                listaAuxiliar.addAll(AdapterListaMedicamentos.listaIdMedicamentos)
                listaAuxiliar.forEach {
                    if(it == medicamento.idMedicamento){
                        AdapterListaMedicamentos.listaIdMedicamentos.remove(medicamento.idMedicamento)
                        if(AlarmReceiver.listaIdMedicamentosTocandoNoMomento.contains(medicamento.idMedicamento)){
                            AlarmReceiver.listaIdMedicamentosTocandoNoMomento.remove(medicamento.idMedicamento)
                        }


                    }
                }

                AdapterListaMedicamentos.listaIdMedicamentos.remove(medicamento.idMedicamento)
                AdapterListaMedicamentos.listaIdMedicamentos.forEach {
                    Log.d("testelistaid", "item restante na lista ao parar o alarme: $it")

                }




            }
            armarProximoAlarme()
        }

        MainActivity.binding.btnDeleteMedicamento.setOnClickListener {
            Log.d("testecliquedelete", "eu cliquei no botão de excluir medicamento")
            val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(binding.root.context)
            alert.setTitle("${medicamento.nomeMedicamento}")
            alert.setMessage("Tem certeza que deseja deletar o medicamento ${medicamento.nomeMedicamento}?")
            alert.setPositiveButton("Sim", DialogInterface.OnClickListener { dialog, which ->

                viewLifecycleOwner.lifecycleScope.launch {
                    medicamentoDoseDao.deleteMedicamentoFromMedicamentoTratamento(medicamento.nomeMedicamento)
                    medicamentoDoseDao.deleteDosesDoMedicamentoFinalizado(medicamento.nomeMedicamento)
                    myDataStore.markToastAsNotShown(booleanPreferencesKey(medicamento.stringDataStore))
                    myDataStore.deleteDataStoreByKey(booleanPreferencesKey(medicamento.stringDataStore))


                }

                dialog.dismiss()
                cancelarBroadcastReceiver()
                mostrarToastExcluido(medicamento.nomeMedicamento)
                fecharFragment()


            })

            alert.setNegativeButton("Não",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    initIntertitialAd()



                })

            alert.show()



        }
    }

    private fun armarProximoAlarme() {
        if((extra as MedicamentoComDoses).listaDoses.size > 0){


            val limiteFor = (extra as MedicamentoComDoses).listaDoses.size - 1

            iterarSobreDosesEAcharProxima(limiteFor)


        }

        Log.d("armarproximo6", "proximo Alarme: $horaProxDose")
        viewLifecycleOwner.lifecycleScope.launch {
            val TOAST_ALREADY_SHOWN = booleanPreferencesKey(medicamento.stringDataStore)

            val hasToastAlreadyShow = myDataStore.hasToastAlreadyShown(TOAST_ALREADY_SHOWN)
            if (hasToastAlreadyShow){
                Log.d("depoisdomute", "toast ja foi mostrado, portanto ele não vai aparecer denovo")

            }else{
                Log.d("depoisdomute", "toast ainda não foi mostrado, portanto ele aparecerá")


                //Toast.makeText(requireContext(), "Alarme ativado! próxima dose às: ${horaProxDose}", Toast.LENGTH_LONG).show()
                //myDataStore.markToastAsShown(TOAST_ALREADY_SHOWN)

            }
        }

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

                        /**
                         * dois medicamentos com o alarme tocando ao mesmo tempo...o botão de parar som some...o ideal
                         * era identificar que o alarme ja esta tocando e fazer uma lista ao inves de uma variavel com um espaço só.
                         */
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

            Log.d("testepodetocar", "eu to aqui no pode tocar true")








            horaProxDose?.let { receiver.setAlarm2(intervaloEntreDoses, (extra as MedicamentoComDoses).medicamentoTratamento.idMedicamento, (extra as MedicamentoComDoses).listaDoses, requireContext(), it) }

            viewLifecycleOwner.lifecycleScope.launch {
                val TOAST_ALREADY_SHOWN = booleanPreferencesKey(medicamento.stringDataStore)

                val hasToastAlreadyShow = myDataStore.hasToastAlreadyShown(TOAST_ALREADY_SHOWN)
                if (hasToastAlreadyShow){
                    Log.d("testepodetocar", "toast ja foi mostrado, portanto ele não vai aparecer denovo")

                }else{
                    Log.d("testepodetocar", "toast ainda não foi mostrado, portanto ele aparecerá")


                    Toast.makeText(requireContext(), "Alarme ativado! próxima dose às: ${horaProxDose}", Toast.LENGTH_LONG).show()
                    myDataStore.markToastAsShown(TOAST_ALREADY_SHOWN)

                }
            }

        }else{
            armarProximoAlarme()

        }



    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myDataStore = MyDataStore(requireContext().applicationContext)
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

    override fun markToastAsNotShown() {
        viewLifecycleOwner.lifecycleScope.launch {
            myDataStore.markToastAsNotShown(booleanPreferencesKey(medicamento.stringDataStore))
        }
    }

    override fun deleteDataStoreByKey() {
        viewLifecycleOwner.lifecycleScope.launch {
            myDataStore.deleteDataStoreByKey(booleanPreferencesKey(medicamento.stringDataStore))
        }
    }

    override fun cancelarBroadcastReceiver() {
        if(this::receiver.isInitialized){

            receiver.cancelAlarmByMedicamentoId(medicamento.idMedicamento, requireContext())
        }
    }
    override fun initIntertitialAd() {
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(requireContext(), "ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback(){
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Log.d("tagintersticial", p0.toString())
                mInterstitial = null
            }

            override fun onAdLoaded(p0: InterstitialAd) {
                super.onAdLoaded(p0)
                Log.d("tagintersticial", "Ad was loaded.")
                mInterstitial = p0
                mInterstitial?.show(requireActivity())
                Log.d("tagintersticial", "O show interstitial foi chamado")


            }
        })

        mInterstitial?.fullScreenContentCallback = object: FullScreenContentCallback(){
            override fun onAdClicked() {
                super.onAdClicked()
                Log.d("tagintersticial", "Ad was clicked")

            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()

                Log.d("tagintersticial", "Ad dismissed fullscreen content")

            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)

                Log.d("tagintersticial", "Ad failed to show fullscreen content")

            }

            override fun onAdImpression() {
                super.onAdImpression()

                Log.d("tagintersticial", "Ad recorded an impression")

            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()

                Log.d("tagintersticial", "Ad showed fullscreen content.")

            }
        }

        if(mInterstitial != null){
            mInterstitial?.show(requireActivity())
            Log.d("tagintersticial", "O show interstitial foi chamado")

        }else{
            Log.d("tagintersticial", "The interstitial ad wasnt ready yet...")
        }
    }


}