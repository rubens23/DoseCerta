package com.rubens.applembretemedicamento.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.rubens.applembretemedicamento.framework.data.AppDatabase
import com.rubens.applembretemedicamento.framework.data.MyDataStore
import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import com.rubens.applembretemedicamento.framework.domain.eventbus.AlarmEvent
import com.rubens.applembretemedicamento.framework.domain.eventbus.MediaPlayerTocando
import com.rubens.applembretemedicamento.framework.domain.MedicamentoManager
import com.rubens.applembretemedicamento.framework.viewModels.ViewModelFragmentCadastrarNovoMedicamento
import com.rubens.applembretemedicamento.presentation.interfaces.AccessAdapterMethodsInterface
import com.rubens.applembretemedicamento.presentation.interfaces.ConexaoBindingAdapterDetalhesMedicamentos
import com.rubens.applembretemedicamento.presentation.interfaces.DetalhesMedicamentosAdapterInterface
import com.rubens.applembretemedicamento.presentation.interfaces.FragmentDetalhesMedicamentosUi
import com.rubens.applembretemedicamento.presentation.interfaces.MainActivityInterface
import com.rubens.applembretemedicamento.presentation.interfaces.OnDeleteMedicamentoListener
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.DetalhesMedicamentoAdapter
import com.rubens.applembretemedicamento.utils.AlarmUtilsInterface
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.CalendarHelper2
import com.rubens.applembretemedicamento.utils.comunicacaoFragmentAdapter
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.Serializable
import javax.inject.Inject


@AndroidEntryPoint
class FragmentDetalhesMedicamentos @Inject constructor(private val alarmUtilsInterface: AlarmUtilsInterface,
                                                       private val funcoesDeTempo: FuncoesDeTempo,
                                                       private val calendarHelper: CalendarHelper,
                                                       private val calendarHelper2: CalendarHelper2
) : Fragment(), comunicacaoFragmentAdapter,
    OnDeleteMedicamentoListener, FragmentDetalhesMedicamentosUi, DetalhesMedicamentosAdapterInterface {


    private var qntAlarmesTocando: Int? = null
    private lateinit var extra: Serializable
    private lateinit var adapter : DetalhesMedicamentoAdapter
    lateinit var viewModel: ViewModelFragmentCadastrarNovoMedicamento
    private var medicamentoAdicionadoObserver: MutableLiveData<MedicamentoTratamento> = MutableLiveData()
    private var mudancaMedicamentoComDoses: MutableLiveData<MedicamentoComDoses> = MutableLiveData()
    private var mudancaMedicamentoComDosesAlarmeTocando: MutableLiveData<MedicamentoComDoses> = MutableLiveData()
    private var excluirDaListaDeMedicamentosNoAlarme: MutableLiveData<Int> = MutableLiveData()
    private var viewHolderInstanceLiveData: MutableLiveData<DetalhesMedicamentoAdapter.ViewHolder> = MutableLiveData()

    private var isEventBusRegistered = false


    private var db: AppDatabase? = null
    private lateinit var medicamentoManager: MedicamentoManager
    private val args: FragmentDetalhesMedicamentosArgs by navArgs()
    private lateinit var myDataStore: MyDataStore
    private var mInterstitial: InterstitialAd? = null
    private lateinit var mainActivityInterface: MainActivityInterface
    private lateinit var adapterMethodsInterface: AccessAdapterMethodsInterface
    private lateinit var binding: FragmentDetalhesMedicamentosBinding
    private lateinit var medicamentoDoseDao: MedicamentoDao
    private lateinit var conexaoBindingAdapterDetalhesMedicamentos: ConexaoBindingAdapterDetalhesMedicamentos
    private var diaAtualSelecionado = ""

    private var mediaPlayer: MediaPlayer? = null
    private var initExtraMedicamentoId = -1




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetalhesMedicamentosBinding.inflate(inflater)

        Log.d("testeshowcancel", "eu to aqui no onCreate view do fragment")


        Log.d("ciclodevida19", "to no oncreate view do fragment detalhes")
        

        setupToolbar()
        initAdapterListaMedicamentosInterface()


        return binding.root
    }

    private fun initAdapterMethodsInterface() {
        adapterMethodsInterface = adapter
    }


    


    private fun initAdapterListaMedicamentosInterface() {
        //todo o adapter lista medicamentos ja ta fechado quando eu abro a tela de detalhes, essa soluçao que eu implementei não é boa
        //adapterListaMedicamentosInterface = context as AdapterListaMedicamentosInterface
    }

    private fun initConexaoComAdapterBinding(viewHolderInstance: DetalhesMedicamentoAdapter.ViewHolder) {

            conexaoBindingAdapterDetalhesMedicamentos = viewHolderInstance

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initAds()
        createNotificationChannel()
        initExtraFromListaMedicamentosFragment()
        initTextViewWithCurrentDate()
        initMedicamentoManager()
        sendExtraToMedicamentoManagerClass()
        getHorarioStringFromExtraAndInitMedicamentoManagerMethods()

        dizerObserverQueMedicamentoFoiRecebidoDoExtra()
        configTextViewsDuracaoTratamento()
        initDetalhesMedicamentosAdapter()
        setAdapterToRecyclerView()
        initAdapterMethodsInterface()
        checarSeAlarmeEstaAtivado()
        checarSeDataSelecionadaIgualADataDeInicioTratamento()
        checarSeDataSelecionadaIgualADataDeTerminoDeTratamento()
        initDataBase()
        initDao()
        registerAlarmEventBus()
        observers()
        onClickListeners()


        Log.d("ciclodevida19", "to no onviewcreated view do fragment detalhes")

    }

    private fun checarSeAlarmeEstaAtivado() {
        if((extra as MedicamentoComDoses).medicamentoTratamento.alarmeAtivado){
            showBtnCancelarAlarme()
            hideBtnArmarAlarme()
        }
    }

    private fun checarSeDataSelecionadaIgualADataDeTerminoDeTratamento(): Boolean {
        if((extra as MedicamentoComDoses).medicamentoTratamento.dataTerminoTratamento == diaAtualSelecionado){
            //apaga o botão de avançar
            hideForwardArrow()
            showBackArrow()
            return false
        }else{
            //reativa o botao de avancar
            showBackArrow()
            showForwardArrow()
            return true
        }
    }

    private fun showForwardArrow() {
        binding.forwardArrow.visibility = View.VISIBLE
    }

    private fun hideForwardArrow() {
        binding.forwardArrow.visibility = View.GONE
    }

    private fun checarSeDataSelecionadaIgualADataDeInicioTratamento(): Boolean {
        if((extra as MedicamentoComDoses).medicamentoTratamento.dataInicioTratamento == diaAtualSelecionado){
            //apaga o botão de voltar
            hideBackArrow()
            showForwardArrow()
            return false
        }else{
            //reativa o botao de voltar
            showForwardArrow()
            showBackArrow()
            return true
        }


    }

    private fun showBackArrow() {
        binding.backArrow.visibility = View.VISIBLE
    }

    private fun hideBackArrow() {
        binding.backArrow.visibility = View.GONE
    }

    private fun initTextViewWithCurrentDate() {
        diaAtualSelecionado = calendarHelper.pegarDataAtual()
        binding.dataAtualSelecionada.text = diaAtualSelecionado
    }

    private fun registerAlarmEventBus() {
        if(!isEventBusRegistered){
            EventBus.getDefault().register(this)
            isEventBusRegistered = true

        }
    }

    @Subscribe(sticky = true)
    fun onAlarmEvent(event: AlarmEvent){
        Log.d("entendendoshowstop", "to aqui no listener do eventBus")

        loadUpdatedMedicamento(initExtraMedicamentoId)





    }

    private fun loadUpdatedMedicamento(medicamentoId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            mudancaMedicamentoComDosesAlarmeTocando.postValue(medicamentoDoseDao.getMedicamentoDosesById(medicamentoId))

        }

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
            alert.setTitle("${getString(R.string.take)} ${doses.nomeMedicamento}")
            alert.setMessage("${getString(R.string.do_u_wanna)} (${doses.horarioDose})?")
            alert.setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener { dialog, _ ->

                viewLifecycleOwner.lifecycleScope.launch {
                    medicamentoDoseDao.tomarDoseMedicamento(true, doses.idDose)
                }
                Toast.makeText(requireContext(), "${doses.horarioDose} ${getString(R.string.dose_taken)}", Toast.LENGTH_LONG).show()

                adapterMethodsInterface.atualizarDose(Doses(doses.idDose, doses.nomeMedicamento, doses.horarioDose, doses.intervaloEntreDoses, doses.dataHora, doses.qntDosesPorHorario, true))


                //conexaoBindingAdapterDetalhesMedicamentos.getItemDetalhesMedicamentosBinding().ivStatusDosage.setImageResource(R.drawable.med_taken)

                dialog.dismiss()
            })

            alert.setNegativeButton("Não",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss() })

            alert.show()
        }else{
            //mostrar o dialog confirmando a dose a ser tomada
            val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
            alert.setTitle(getString(R.string.dose_already_taken))
            alert.setMessage(getString(R.string.dose_already_taken2))
            alert.setPositiveButton(getString(R.string.ok), DialogInterface.OnClickListener { dialog, which ->


                dialog.dismiss()
            })


            alert.show()

        }

    }





    override fun onDoseImageViewClick(doses: Doses) {



        if(!doses.jaTomouDose){
            //mostrar o dialog confirmando a dose a ser tomada
            val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
            alert.setTitle("${getString(R.string.take)} ${doses.nomeMedicamento}")
            alert.setMessage("${getString(R.string.do_u_wanna)} (${doses.horarioDose})?")
            alert.setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener { dialog, which ->

                GlobalScope.launch {
                    //doseAtualizada recebe a dose atualizada
                     medicamentoDoseDao.tomarDoseMedicamento(true, doses.idDose)
                }

                Toast.makeText(requireContext(), "${doses.horarioDose} ${getString(R.string.dose_taken)}", Toast.LENGTH_LONG).show()

                adapterMethodsInterface.atualizarDose(Doses(doses.idDose, doses.nomeMedicamento, doses.horarioDose, doses.intervaloEntreDoses, doses.dataHora, doses.qntDosesPorHorario, true))


                //conexaoBindingAdapterDetalhesMedicamentos.getItemDetalhesMedicamentosBinding().ivStatusDosage.setImageResource(R.drawable.med_taken)

                dialog.dismiss()
            })

            alert.setNegativeButton(getString(R.string.no),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss() })

            alert.show()
        }else{
            //mostrar o dialog confirmando a dose a ser tomada
            val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
            alert.setTitle(getString(R.string.dose_already_taken))
            alert.setMessage(getString(R.string.dose_already_taken2))
            alert.setPositiveButton(getString(R.string.ok), DialogInterface.OnClickListener { dialog, _ ->


                dialog.dismiss()
            })


            alert.show()

        }



    }

    override fun setViewHolderLiveDataValue(vh: DetalhesMedicamentoAdapter.ViewHolder) {
        viewHolderInstanceLiveData.value = vh
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
        adapter = DetalhesMedicamentoAdapter(extra as MedicamentoComDoses, this, diaAtualSelecionado, calendarHelper2)
    }

    private fun getHorarioStringFromExtraAndInitMedicamentoManagerMethods() {
        val extraStringHorario = args.horaproximadose
        Log.d("extrastring", args.horaproximadose)
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
        initExtraMedicamentoId = (args.medicamento).medicamentoTratamento.idMedicamento
        Log.d("correctingidbug", "init medicamento adicionado a o extra agora: ${(extra as MedicamentoComDoses).medicamentoTratamento.nomeMedicamento}, id: ${(extra as MedicamentoComDoses).medicamentoTratamento.idMedicamento}")

    }


    private fun observers() {
        excluirDaListaDeMedicamentosNoAlarme.observe(viewLifecycleOwner){
            removeMedicamentoById(it)

        }

        medicamentoManager.horaProximaDoseObserver.observe(viewLifecycleOwner){
            if(alarmUtilsInterface.getMediaPlayerInstance() != null){
                if(alarmUtilsInterface.getMediaPlayerInstance()!!.isPlaying){
                    //showBtnCancelarAlarme()
                    //hideBtnArmarAlarme()
                }
            }

        }
        initButtonChangeListener()

        alarmUtilsInterface.initButtonStateLiveData()

        alarmUtilsInterface.getButtonChangeLiveData().observe(viewLifecycleOwner){
            //showBtnPararSom()
        }

        viewHolderInstanceLiveData.observe(viewLifecycleOwner){
            viewHolderInstance->
            initConexaoComAdapterBinding(viewHolderInstance)
        }

        mudancaMedicamentoComDoses.observe(viewLifecycleOwner){
            it.listaDoses.forEach {
                Log.d("listupdate", "testando lista atualizada: ${it.nomeMedicamento} ${it.horarioDose} ja tomou dose: ${it.jaTomouDose}")
            }
            extra = it
            Log.d("correctingidbug", "mudancaMedicamentoComDoses: medicamento adicionado a o extra agora: ${(extra as MedicamentoComDoses).medicamentoTratamento.nomeMedicamento}, id: ${(extra as MedicamentoComDoses).medicamentoTratamento.idMedicamento}")

            adapterMethodsInterface.updateList(it)
            adapterMethodsInterface.updateRecyclerViewOnDateChange(diaAtualSelecionado)
        }

        mudancaMedicamentoComDosesAlarmeTocando.observe(viewLifecycleOwner){
            if(it != null){
                extra = it

                if((extra as MedicamentoComDoses).medicamentoTratamento.alarmeTocando){
                    if(mediaPlayer != null){
                        if(mediaPlayer!!.isPlaying){
                            showBtnPararSom()

                        }
                    }

                }
            }

        }



    }

    private fun initButtonChangeListener() {
        alarmUtilsInterface.initButtonStateLiveData()
    }

    override fun hideBtnArmarAlarme() {
        binding.btnArmarAlarme.visibility = View.INVISIBLE
        binding.btnArmarAlarme.isClickable = false
        binding.btnCancelarAlarme.isClickable = true

        Log.d("testeshowcancel", "eu to aqui no metodo de esconder botao de armar alarme, na implementação")

    }

    override fun showBtnArmarAlarme() {
        binding.btnArmarAlarme.visibility = View.VISIBLE
        binding.btnArmarAlarme.isClickable = true
        binding.btnCancelarAlarme.isClickable = false


        Log.d("testebtncancel", "eu to aqui no metodo que mostra o botao de armar alarme")

    }

    override fun showBtnCancelarAlarme() {
        binding.btnCancelarAlarme.visibility = View.VISIBLE
        binding.btnCancelarAlarme.isClickable = true
        binding.btnArmarAlarme.isClickable = false




        Log.d("testebtncancel", "to aqui no metodo de mostrar botao cancelar alarme, na implementação")

    }


    private fun removeMedicamentoById(id: Int?) {
        if (id != null) {
            //todo achar outra solucao pois o adapter da lista de medicamentos já esta morto
            //adapterListaMedicamentosInterface.removeFromListaIdMedicamentosFromListaAdapter(id)
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

        Log.d("testeshowcancel", "eu to aqui no onResume do fragment")


        Log.d("ciclodevida19", "to no onresume view do fragment detalhes")

    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[ViewModelFragmentCadastrarNovoMedicamento::class.java]
    }



    private fun onClickListeners() {
        binding.btnArmarAlarme.setOnClickListener {
            salvarNoBancoAInformacaoDeQueOAlarmeDoMedicamentoEstaLigado()
            ligarAlarme()
            hideBtnArmarAlarme()
            showBtnCancelarAlarme()
            Log.d("testeshowcancel", "eu to aqui no clique do botão armar alarme")
        }
        binding.btnCancelarAlarme.setOnClickListener {
            pararSomSeMediaPlayerEstiverTocando()
            mostrarToastDeAlarmeDesativado()
            salvarNoBancoAInformacaoDeQueOAlarmeDoMedicamentoEstaDesligado()
            cancelarOAlarmeNoBroadcastReceiver()
            hideBtnCancelarAlarme()
            showBtnArmarAlarme()



        }
        binding.btnPararSom.setOnClickListener {
            pararSomSeMediaPlayerEstiverTocando()

            armarProximoAlarme()
        }

        binding.backArrow.setOnClickListener {
            pegarDosesDeOntem()

        }

        binding.forwardArrow.setOnClickListener {
            pegarDosesDeAmanha()


        }


    }

    private fun pararSomSeMediaPlayerEstiverTocando(){
        if (getMediaPlayerInstance() != null){
            Log.d("testeplay2", "media player instance is not null ${getMediaPlayerInstance()}")

            if (getMediaPlayerInstance()!!.isPlaying){
                Log.d("testeplay2", "media player is playing ${getMediaPlayerInstance()}")



                stopMusicPlayer()
                hideBtnPararSom()
                avisarQueMedicamentoNaoEstaTocando()
            }else{
                Log.d("testeplay2", "media player is not playing")
            }
        }else{
            Log.d("testeplay2", "media player instance is null")
            hideBtnPararSom()
            avisarQueMedicamentoNaoEstaTocando()

        }
    }

    private fun avisarQueMedicamentoNaoEstaTocando() {
        viewLifecycleOwner.lifecycleScope.launch {
            //todo talvez o bug esteja aqui, talvez ele esteja mandando o id errado para o metodo de acesso ao banco, verificar isso
            Log.d("correctingidbug", "aqui antes de atualizar o db: medicamento adicionado a o extra agora: ${(extra as MedicamentoComDoses).medicamentoTratamento.nomeMedicamento}, id: ${(extra as MedicamentoComDoses).medicamentoTratamento.idMedicamento}")

            medicamentoDoseDao.alarmeMedicamentoTocando(initExtraMedicamentoId, false)

        }
    }

    private fun getMediaPlayerInstance(): MediaPlayer? {
        return mediaPlayer
    }

    @Subscribe(sticky = true)
    fun onMediaPlayerTocando(event: MediaPlayerTocando) {
        mediaPlayer = event.mediaPlayer
        Log.d("testebusdetalhes", "eu recebi o mp ${event.mediaPlayer} e instanciei: ${mediaPlayer}")
    }



    private fun pegarDosesDeAmanha() {
        val podeAvancar = checarSeDataSelecionadaIgualADataDeTerminoDeTratamento()
        if(podeAvancar){
            somarUmDiaADataAtual()
            atualizarTextViewDaDataAtual()
            lifecycleScope.launch {
                mudancaMedicamentoComDoses.postValue(medicamentoDoseDao.getMedicamentoDosesByName((extra as MedicamentoComDoses).medicamentoTratamento.nomeMedicamento))


            }
            //adapterMethodsInterface.updateRecyclerViewOnDateChange(diaAtualSelecionado)
        }
        checarSeDataSelecionadaIgualADataDeTerminoDeTratamento()
    }

    private fun pegarDosesDeOntem() {
        val podeVoltar = checarSeDataSelecionadaIgualADataDeInicioTratamento()
        if(podeVoltar){
            subtrairUmDiaDaDataAtual()
            atualizarTextViewDaDataAtual()
            lifecycleScope.launch {
                //mudancaMedicamentoComDoses.postValue(medicamentoDoseDao.getMedicamentoDosesByName((extra as MedicamentoComDoses).medicamentoTratamento.nomeMedicamento))
                mudancaMedicamentoComDoses.postValue(medicamentoDoseDao.getMedicamentoDosesById((extra as MedicamentoComDoses).medicamentoTratamento.idMedicamento))


            }


        }
        checarSeDataSelecionadaIgualADataDeInicioTratamento()
    }

    private fun subtrairUmDiaDaDataAtual() {
        diaAtualSelecionado = calendarHelper.subtrairUmDiaNumaData(diaAtualSelecionado)
    }

    private fun atualizarTextViewDaDataAtual() {
        binding.dataAtualSelecionada.text = diaAtualSelecionado
    }

    private fun somarUmDiaADataAtual() {
        diaAtualSelecionado = calendarHelper.somarUmDiaNumaData(diaAtualSelecionado)
    }

    private fun createDeleteAlertDialog(medicamento: MedicamentoTratamento) {
        val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(binding.root.context)
        alert.setTitle(medicamento.nomeMedicamento)
        alert.setMessage("${getString(R.string.sure_delete)} ${medicamento.nomeMedicamento}")
        alert.setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener { dialog, _ ->

            procedimentosDaExclusaoDoMedicamento()

            dialog.dismiss()
            cancelarBroadcastReceiver()
            mostrarToastExcluido(medicamento.nomeMedicamento)
            fecharFragment()
        })

        alert.setNegativeButton(getString(R.string.no),
            DialogInterface.OnClickListener { dialog, _ ->
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
        if(qntAlarmesTocando != null){
            medicamentoManager.getReceiver().cancelAlarm(requireContext(), qntAlarmesTocando!!)

        }
    }

    private fun salvarNoBancoAInformacaoDeQueOAlarmeDoMedicamentoEstaDesligado() {
        viewModel.ligarAlarmeDoMedicamento(medicamentoManager.getMedicamento().nomeMedicamento, false)
    }


    private fun mostrarToastDeAlarmeDesativado() {
        Toast.makeText(requireContext(), "${getString(R.string.alarm_deactivated_for)} ${(extra as MedicamentoComDoses).medicamentoTratamento.nomeMedicamento}!", Toast.LENGTH_LONG).show()
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





    private fun hideBtnPararSom() {
        binding.btnPararSom.visibility = View.GONE
    }

    private fun stopMusicPlayer() {
        qntAlarmesTocando = viewModel.pegarTodosOsMedicamentosComAlarmeTocando()?.size
        if(qntAlarmesTocando != null){
            if(qntAlarmesTocando == 1){
                Log.d("controlcancel", "quantidade de alarmes tocando é igual a 1 stopMusicPlayer")
                //só para o som do alarme se só um medicamento estiver tocando o alarme no momento
                alarmUtilsInterface.stopAlarmSound(requireContext())
            }else{
                Log.d("controlcancel", "quantidade de alarmes tocando é igual a $qntAlarmesTocando dont stopMusicPlayer")

            }
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
        Toast.makeText(requireContext(), "${getString(R.string.the_medicine)} $nome ${getString(R.string.was_excluded)}", Toast.LENGTH_LONG).show()
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
        binding.btnCancelarAlarme.visibility = View.INVISIBLE
        binding.btnCancelarAlarme.isClickable = false
        binding.btnArmarAlarme.isClickable = true

        Log.d("testebtncancel", "to aqui no hide btn cancelar alarme")




    }



    override fun showBtnPararSom() {
        binding.btnPararSom.visibility = View.VISIBLE

    }

    override fun showAlarmConfirmationToast(
        horaProxDose: String,
        medicamentoComDoses: MedicamentoComDoses
    ) {
        Toast.makeText(requireContext(), "${getString(R.string.alarm_activated)} ${medicamentoComDoses.medicamentoTratamento.nomeMedicamento} ${getString(R.string.at)} $horaProxDose", Toast.LENGTH_LONG).show()
    }

    override fun showToastDosesAcabaram() {
        Toast.makeText(requireActivity(), getString(R.string.doses_are_over), Toast.LENGTH_SHORT).show()
    }


}
