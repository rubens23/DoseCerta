package com.rubens.applembretemedicamento.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.MobileAds
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.databinding.FragmentDetalhesMedicamentosBinding
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiver
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiverInterface
import com.rubens.applembretemedicamento.framework.data.AppDatabase
import com.rubens.applembretemedicamento.framework.data.MyDataStore
import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import com.rubens.applembretemedicamento.framework.domain.MedicamentoManager
import com.rubens.applembretemedicamento.framework.viewModels.ViewModelFragmentCadastrarNovoMedicamento
import com.rubens.applembretemedicamento.presentation.interfaces.AdapterListaMedicamentosInterface
import com.rubens.applembretemedicamento.presentation.interfaces.ConexaoBindingAdapterDetalhesMedicamentos
import com.rubens.applembretemedicamento.presentation.interfaces.DetalhesMedicamentosAdapterInterface
import com.rubens.applembretemedicamento.presentation.interfaces.FragmentDetalhesMedicamentosUi
import com.rubens.applembretemedicamento.presentation.interfaces.MainActivityInterface
import com.rubens.applembretemedicamento.presentation.interfaces.OnDeleteMedicamentoListener
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.AdapterListaMedicamentos
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.DetalhesMedicamentoAdapter
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.comunicacaoFragmentAdapter
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable


class FragmentDetalhesMedicamentos : Fragment(), FuncoesDeTempo, CalendarHelper, comunicacaoFragmentAdapter,
    OnDeleteMedicamentoListener, FragmentDetalhesMedicamentosUi, DetalhesMedicamentosAdapterInterface {


    private lateinit var extra: Serializable
    private lateinit var adapter : DetalhesMedicamentoAdapter
    lateinit var viewModel: ViewModelFragmentCadastrarNovoMedicamento
    private var medicamentoAdicionadoObserver: MutableLiveData<MedicamentoTratamento> = MutableLiveData()
    private var excluirDaListaDeMedicamentosNoAlarme: MutableLiveData<Int> = MutableLiveData()
    private var db: AppDatabase? = null
    private lateinit var medicamentoManager: MedicamentoManager
    private val args: FragmentDetalhesMedicamentosArgs by navArgs()
    private lateinit var myDataStore: MyDataStore
    private var mInterstitial: InterstitialAd? = null
    private lateinit var mainActivityInterface: MainActivityInterface
    private lateinit var binding: FragmentDetalhesMedicamentosBinding
    private lateinit var alarmReceiverInterface: AlarmReceiverInterface
    private var alarmReceiver: AlarmReceiver = AlarmReceiver()
    private lateinit var medicamentoDoseDao: MedicamentoDao
    private lateinit var conexaoBindingAdapterDetalhesMedicamentos: ConexaoBindingAdapterDetalhesMedicamentos
    private lateinit var adapterListaMedicamentosInterface: AdapterListaMedicamentosInterface


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetalhesMedicamentosBinding.inflate(inflater)

        initAlarmReceiverInterface()
        setupToolbar()
        initConexaoComAdapterBinding()
        initAdapterListaMedicamentosInterface()

        return binding.root
    }

    private fun initAdapterListaMedicamentosInterface() {
        adapterListaMedicamentosInterface = context as AdapterListaMedicamentosInterface
    }

    private fun initConexaoComAdapterBinding() {
        conexaoBindingAdapterDetalhesMedicamentos = requireContext() as ConexaoBindingAdapterDetalhesMedicamentos
    }

    private fun initAlarmReceiverInterface() {
        alarmReceiverInterface = alarmReceiver as AlarmReceiverInterface
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initAds()
        createNotificationChannel()
        initExtraFromListaMedicamentosFragment()
        initMedicamentoManager()
        sendExtraToMedicamentoManagerClass()
        getHorarioStringFromExtraAndInitMedicamentoManagerMethods()
        initDetalhesMedicamentosAdapter()
        dizerObserverQueMedicamentoFoiRecebidoDoExtra()
        configTextViewsDuracaoTratamento()
        setAdapterToRecyclerView()
        initDataBase()
        initDao()
        observers()
        onClickListeners()
    }

    override fun initDao() {
        medicamentoDoseDao = db!!.medicamentoDao
    }

    override fun initDataBase() {
        db = AppDatabase.getAppDatabase(requireContext())
    }

    override fun onDoseClick(doses: Doses) {
        if(!doses.jaTomouDose){
            //mostrar o dialog confirmando a dose a ser tomada
            val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
            alert.setTitle("Tomar ${doses.nomeMedicamento}")
            alert.setMessage("Você quer tomar a dose de ${doses.horarioDose} agora?")
            Log.d("testehora", "${doses.horarioDose}")
            alert.setPositiveButton("Sim", DialogInterface.OnClickListener { dialog, which ->

                viewLifecycleOwner.lifecycleScope.launch {
                    medicamentoDoseDao.tomarDoseMedicamento(true, doses.idDose)
                }
                conexaoBindingAdapterDetalhesMedicamentos.getBinding().ivStatusDosage.setImageResource(R.drawable.med_taken)

                dialog.dismiss()
            })

            alert.setNegativeButton("Não",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss() })

            alert.show()
        }else{
            //mostrar o dialog confirmando a dose a ser tomada
            val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
            alert.setTitle("Dose Tomada!")
            alert.setMessage("Você já tomou a dose das ${doses.horarioDose}!")
            alert.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->


                dialog.dismiss()
            })


            alert.show()

        }
    }

    override fun onDoseImageViewClick(doses: Doses) {

        if(!doses.jaTomouDose){
            //mostrar o dialog confirmando a dose a ser tomada
            val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
            alert.setTitle("Tomar ${doses.nomeMedicamento}")
            alert.setMessage("Você quer tomar a dose de ${doses.horarioDose} agora?")
            alert.setPositiveButton("Sim", DialogInterface.OnClickListener { dialog, which ->

                GlobalScope.launch {
                    medicamentoDoseDao.tomarDoseMedicamento(true, doses.idDose)
                }

                conexaoBindingAdapterDetalhesMedicamentos.getBinding().ivStatusDosage.setImageResource(R.drawable.med_taken)

                dialog.dismiss()
            })

            alert.setNegativeButton("Não",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss() })

            alert.show()
        }else{
            //mostrar o dialog confirmando a dose a ser tomada
            val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
            alert.setTitle("Dose Tomada!")
            alert.setMessage("Você já tomou a dose das ${doses.horarioDose}!")
            alert.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->


                dialog.dismiss()
            })


            alert.show()

        }

    }

    private fun setAdapterToRecyclerView() {
        binding.medDetalhesRecyclerView.adapter = adapter
    }

    private fun configTextViewsDuracaoTratamento() {
        binding.dataInicioTratamento.text = (extra as MedicamentoComDoses).medicamentoTratamento.dataInicioTratamento
        binding.dataTerminoTratamento.text = (extra as MedicamentoComDoses).medicamentoTratamento.dataTerminoTratamento
    }

    private fun dizerObserverQueMedicamentoFoiRecebidoDoExtra() {
        medicamentoAdicionadoObserver.postValue((extra as MedicamentoComDoses).medicamentoTratamento)
    }

    private fun initDetalhesMedicamentosAdapter() {
        adapter = DetalhesMedicamentoAdapter(extra as MedicamentoComDoses, requireContext())
    }

    private fun getHorarioStringFromExtraAndInitMedicamentoManagerMethods() {
        val extraStringHorario = args.horaproximadose
        medicamentoManager.startUpdateHoraProxDose(extraStringHorario)
        medicamentoManager.startUpdateIntervaloEntreDoses(args.intervaloentredoses.toDouble())
    }

    private fun sendExtraToMedicamentoManagerClass() {
        medicamentoManager.initializeExtra(extra)
    }

    private fun initMedicamentoManager() {
        medicamentoManager = args.medicamentoManager
    }

    private fun initExtraFromListaMedicamentosFragment() {
        extra = args.medicamento
    }


    private fun observers() {
        excluirDaListaDeMedicamentosNoAlarme.observe(viewLifecycleOwner){
            removeMedicamentoById(it)

        }

        medicamentoManager.updateDataStore.observe(viewLifecycleOwner){
            markToastAsShownInDataStore()
        }

        medicamentoManager.horaProximaDoseObserver.observe(viewLifecycleOwner){
                if(alarmReceiverInterface.getMediaPlayerInstance().isPlaying){
                    showBtnCancelarAlarme()
                    hideBtnArmarAlarme()
                }
        }
    }

    override fun hideBtnArmarAlarme() {
        binding.btnArmarAlarme.visibility = View.INVISIBLE
        binding.btnArmarAlarme.isClickable = false
    }

    override fun showBtnCancelarAlarme() {
        binding.btnCancelarAlarme.visibility = View.VISIBLE
    }

    private fun markToastAsShownInDataStore() {
        viewLifecycleOwner.lifecycleScope.launch {
            val TOAST_ALREADY_SHOWN = booleanPreferencesKey(medicamentoManager.getMedicamento().stringDataStore)

            val hasToastAlreadyShow = myDataStore.hasToastAlreadyShown(TOAST_ALREADY_SHOWN)
            if (hasToastAlreadyShow){
                Log.d("testepodetocar", "toast ja foi mostrado, portanto ele não vai aparecer denovo")

            }else{
                Log.d("testepodetocar", "toast ainda não foi mostrado, portanto ele aparecerá")


                Toast.makeText(requireContext(), "Alarme ativado! próxima dose às: ${medicamentoManager.getHrProxDose()}", Toast.LENGTH_LONG).show()
                myDataStore.markToastAsShown(TOAST_ALREADY_SHOWN)

            }
        }
    }

    private fun removeMedicamentoById(id: Int?) {
        if (id != null) {
            adapterListaMedicamentosInterface.removeFromListaIdMedicamentosFromListaAdapter(id)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initMainActivityInterface(context)

    }

    private fun initMainActivityInterface(context: Context) {
        mainActivityInterface = context as MainActivityInterface

    }

    private fun setupToolbar() {
        mainActivityInterface.showToolbar()
        mainActivityInterface.hideToolbarTitle()
        mainActivityInterface.showBtnDeleteMedicamento()
    }


    private fun initAds() {
        MobileAds.initialize(requireContext()) {}


        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    override fun onStart() {
        super.onStart()

        medicamentoAdicionadoObserver.observe(this){
            medicamentoManager.startUpdateMedicamento(it)
            medicamentoManager.startChecarSeAlarmeEstaAtivado(this)


        }
    }

    override fun onResume() {
        super.onResume()

        setupToolbar()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[ViewModelFragmentCadastrarNovoMedicamento::class.java]
    }



    private fun onClickListeners() {
        binding.btnArmarAlarme.setOnClickListener {
            salvarNoBancoAInformacaoDeQueOAlarmeDoMedicamentoEstaLigado()
            ligarAlarme()
        }
        binding.btnCancelarAlarme.setOnClickListener {
            mostrarToastDeAlarmeDesativado()
            salvarNoBancoAInformacaoDeQueOAlarmeDoMedicamentoEstaDesligado()
            cancelarOAlarmeNoBroadcastReceiver()
            markToastAsNotShownInDataStore()
        }
        binding.btnPararSom.setOnClickListener {
            markToastAsNotShownInDataStore()
            if (alarmReceiverInterface.getMediaPlayerInstance().isPlaying){
                stopMusicPlayer()
                hideBtnPararSom()
                createListaAuxiliarERemoverOMedicamentoDasListasDeAlarmeTocando()
                removeMedicamentoDaListaDeAlarmesTocando()
            }
            armarProximoAlarme()
        }


    }

    private fun createDeleteAlertDialog(medicamento: MedicamentoTratamento) {
        val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(binding.root.context)
        alert.setTitle(medicamento.nomeMedicamento)
        alert.setMessage("Tem certeza que deseja deletar o medicamento ${medicamento.nomeMedicamento}")
        alert.setPositiveButton("Sim", DialogInterface.OnClickListener { dialog, which ->

            procedimentosDaExclusaoDoMedicamento()

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

    private fun procedimentosDaExclusaoDoMedicamento() {
        viewLifecycleOwner.lifecycleScope.launch {
            medicamentoDoseDao.deleteMedicamentoFromMedicamentoTratamento(medicamentoManager.getMedicamento().nomeMedicamento)
            medicamentoDoseDao.deleteDosesDoMedicamentoFinalizado(medicamentoManager.getMedicamento().nomeMedicamento)
            myDataStore.markToastAsNotShown(booleanPreferencesKey(medicamentoManager.getMedicamento().stringDataStore))
            myDataStore.deleteDataStoreByKey(booleanPreferencesKey(medicamentoManager.getMedicamento().stringDataStore))

        }
    }

    private fun cancelarOAlarmeNoBroadcastReceiver() {
        medicamentoManager.getReceiver().cancelAlarm()
    }

    private fun salvarNoBancoAInformacaoDeQueOAlarmeDoMedicamentoEstaDesligado() {
        viewModel.ligarAlarmeDoMedicamento(medicamentoManager.getMedicamento().nomeMedicamento, false)
    }

    private fun mostrarToastDeAlarmeDesativado() {
        Toast.makeText(requireContext(), "Alarme desativado!", Toast.LENGTH_LONG).show()
    }

    private fun ligarAlarme() {
        medicamentoManager.startAlarmManager(this)
    }

    private fun salvarNoBancoAInformacaoDeQueOAlarmeDoMedicamentoEstaLigado() {
        viewModel.ligarAlarmeDoMedicamento(medicamentoManager.getMedicamento().nomeMedicamento, true)
    }

    private fun armarProximoAlarme() {
        medicamentoManager.startArmarProximoAlarme()
    }

    private fun createListaAuxiliarERemoverOMedicamentoDasListasDeAlarmeTocando() {
        val listaAuxiliar = ArrayList<Int>()
        listaAuxiliar.addAll(adapterListaMedicamentosInterface.getListaIdMedicamentosFromAdapterListaMedicamentos())
        listaAuxiliar.forEach {
            if(it == medicamentoManager.getMedicamento().idMedicamento){
                removeMedicamentoDaListaDeAlarmesTocando()

                if(alarmReceiverInterface.getListaIdMedicamentosTocandoNoMomentoFromAlarmReceiver().contains(medicamentoManager.getMedicamento().idMedicamento)){
                    avisaQueEsseMedicamentoNaoEstaComOAlarmeTocandoNoMomento()

                }
            }
        }
    }

    private fun avisaQueEsseMedicamentoNaoEstaComOAlarmeTocandoNoMomento() {
        alarmReceiverInterface.removeFromListaIdMedicamentoTocandoNoMomento(medicamentoManager.getMedicamento().idMedicamento)
    }

    private fun removeMedicamentoDaListaDeAlarmesTocando() {
        adapterListaMedicamentosInterface.removeFromListaIdMedicamentosFromListaAdapter(medicamentoManager.getMedicamento().idMedicamento)
    }

    private fun hideBtnPararSom() {
        binding.btnPararSom.visibility = View.GONE
    }

    private fun stopMusicPlayer() {
        alarmReceiverInterface.stopMediaPlayer()
    }

    private fun markToastAsNotShownInDataStore() {
        viewLifecycleOwner.lifecycleScope.launch {
            myDataStore.markToastAsNotShown(booleanPreferencesKey(medicamentoManager.getMedicamento().stringDataStore))
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
        return verificarSeDataJaPassou(medicamentoManager.getMedicamento().dataTerminoTratamento)

    }

    override fun markToastAsNotShown() {
        viewLifecycleOwner.lifecycleScope.launch {
            myDataStore.markToastAsNotShown(booleanPreferencesKey(medicamentoManager.getMedicamento().stringDataStore))
        }
    }

    override fun deleteDataStoreByKey() {
        viewLifecycleOwner.lifecycleScope.launch {
            myDataStore.deleteDataStoreByKey(booleanPreferencesKey(medicamentoManager.getMedicamento().stringDataStore))
        }
    }

    override fun cancelarBroadcastReceiver() {
        if(medicamentoManager.checkIfReceiverIsInitialized()){


            medicamentoManager.getReceiver().cancelAlarmByMedicamentoId(medicamentoManager.getMedicamento().idMedicamento, requireContext())
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

    override fun onDeleteMedicamento() {
        createDeleteAlertDialog(medicamentoManager.getMedicamento())
    }



    override fun hideBtnCancelarAlarme() {
        binding.btnCancelarAlarme.visibility = View.GONE


    }

    override fun showBtnArmarAlarme() {
        binding.btnArmarAlarme.visibility = View.VISIBLE
        binding.btnArmarAlarme.isClickable = true

    }

    override fun showBtnPararSom() {
        binding.btnPararSom.visibility = View.VISIBLE

    }


}