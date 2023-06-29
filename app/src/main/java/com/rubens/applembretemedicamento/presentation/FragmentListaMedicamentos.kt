package com.rubens.applembretemedicamento.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.databinding.FragmentListaMedicamentosBinding
import com.rubens.applembretemedicamento.framework.data.AppDatabase
import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.HistoricoMedicamentos
import com.rubens.applembretemedicamento.framework.domain.eventbus.AlarmeMedicamentoTocando
import com.rubens.applembretemedicamento.framework.domain.eventbus.MediaPlayerTocando
import com.rubens.applembretemedicamento.framework.domain.MedicamentoManager
import com.rubens.applembretemedicamento.framework.services.ServiceMediaPlayer
import com.rubens.applembretemedicamento.framework.viewModels.ActivityHostAndFragmentListaMedicamentosSharedViewModel
import com.rubens.applembretemedicamento.framework.viewModels.ViewModelFragmentLista
import com.rubens.applembretemedicamento.presentation.interfaces.AdapterListaMedicamentosInterface
import com.rubens.applembretemedicamento.presentation.interfaces.FragmentListaMedicamentosInterface
import com.rubens.applembretemedicamento.presentation.interfaces.MainActivityInterface
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.AdapterListaMedicamentos
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.Inject
import com.rubens.applembretemedicamento.utils.AlarmUtilsInterface

@AndroidEntryPoint
class FragmentListaMedicamentos @Inject constructor(private val alarmUtilsInterface: AlarmUtilsInterface,
                                                    private val medicamentoManager: MedicamentoManager,
                                                    private val funcoesDeTempo: FuncoesDeTempo,
                                                    private val calendarHelper: CalendarHelper,
                                                    private val context: Context
                                                    ): Fragment(), FragmentListaMedicamentosInterface {

    private lateinit var binding: FragmentListaMedicamentosBinding
    private var listaMedicamentos: ArrayList<MedicamentoComDoses> = ArrayList()
    private lateinit var adapter: AdapterListaMedicamentos
    lateinit var viewModel: ViewModelFragmentLista
    private lateinit var sharedViewModel: ActivityHostAndFragmentListaMedicamentosSharedViewModel
    private var db: AppDatabase? = null
    private var isEventBusRegistered = false
    private lateinit var adapterListaMedicamentosInterface: AdapterListaMedicamentosInterface


    private var mediaPlayer: MediaPlayer? = null

    private var mainActivityInterface: MainActivityInterface? = null

    fun setMainActivityInterface(mainActivityInterface: MainActivityInterface){
        this.mainActivityInterface = mainActivityInterface
        Log.d("changelistener", "to aqui antes de chamar o setup toolbar")

        setupToolbar()

    }







    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initDb()

        binding = FragmentListaMedicamentosBinding.inflate(inflater)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        registerAlarmeMedicamentoTocandoEventBus()

        initViewModel()

        initAds()
        colocarDataAtualNaTextViewDaData()

        escutarMediaPlayer()

        onClickListeners()

        getRecyclerViewPositionIfItWasSaved(savedInstanceState)

        initSpinningCogAnimation()





    }

    private fun initSpinningCogAnimation(){
        val spin = AnimationUtils.loadAnimation(requireContext(), R.anim.cog_shake)
        binding.btnSettings.startAnimation(spin)
    }


    private fun initAdapterInterface() {
        adapterListaMedicamentosInterface = adapter
    }

    private fun registerAlarmeMedicamentoTocandoEventBus() {
        if(!isEventBusRegistered){
            EventBus.getDefault().register(this)
            isEventBusRegistered = true

        }
    }


    private fun getRecyclerViewPositionIfItWasSaved(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            viewModel.recyclerViewPosition = it.getInt("RECYCLER_VIEW_POSITION")
        }

    }

    private fun colocarDataAtualNaTextViewDaData() {
        binding.dataAtual.text = calendarHelper.pegarDataAtual()

    }


    private fun initAds() {
        MobileAds.initialize(requireContext()) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    private fun setupToolbar() {

        if(mainActivityInterface != null){
            mainActivityInterface!!.hideToolbar()
            mainActivityInterface!!.hideToolbarTitle()

        }


        Log.d("hidingtitle", "eu to aqui escondendo o title")

    }

    private fun initViewModel(){
        sharedViewModel = ViewModelProvider(requireActivity())[ActivityHostAndFragmentListaMedicamentosSharedViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity())[ViewModelFragmentLista::class.java]
        initObservers()
        initFragmentInstance()

    }

    private fun initFragmentInstance() {
        sharedViewModel.getFragmentListaInstance(this)
    }


    private fun initObservers() {
        viewModel.medicamentos.observe(viewLifecycleOwner){
           listaMedicamentoComDoses->
            Log.d("testeshakingclock", "to dentro do observer de getMedicamentos")

            if(listaMedicamentoComDoses != null){
                if(listaMedicamentoComDoses.isNotEmpty()){
                    hideTVNoData()
                    hideCaixaVazia()
                    Log.d("emptylist", "lista não esta vazia")
                }else{
                    showTVNoData()
                    showCaixaVazia()
                    Log.d("emptylist", "lista esta vazia")


                }
                pegarListaAtualizadaDeMedicamentos(listaMedicamentoComDoses)

                setAdapter(listaMedicamentos)
            }else{
                Log.d("emptylist", "lista é nula")

            }




        }
    }

    private fun pegarListaAtualizadaDeMedicamentos(listaMedicamentoComDoses: List<MedicamentoComDoses>) {
        listaMedicamentos.clear()



        listaMedicamentoComDoses.forEach {
            //Log.d("listaprincipal", "${it.medicamentoTratamento.nomeMedicamento} tempo restante de tratamento: ${it.medicamentoTratamento.diasRestantesDeTratamento} dia, ultima dose horario: ${it.listaDoses[it.listaDoses.size-1].horarioDose} dose ja tomada? ${it.listaDoses[it.listaDoses.size-1].jaTomouDose}")
            if(it.medicamentoTratamento.diasRestantesDeTratamento == 1){
                if(!it.listaDoses[it.listaDoses.size-1].jaTomouDose){
                    passarMedicamentoParaListaMedicamentos(it)
                }else{
                    launchCoroutineScope(it)
                }
            }else{
                passarMedicamentoParaListaMedicamentos(it)
            }

        }

    }

    private fun passarMedicamentoParaListaMedicamentos(medicamento: MedicamentoComDoses) {
        if(!listaMedicamentos.contains(medicamento)){
            listaMedicamentos.add(medicamento)
        }

    }

    private fun showCaixaVazia() {
        binding.caixaVazia.visibility = View.VISIBLE
    }

    private fun showTVNoData() {
        binding.txtNoData.visibility = View.VISIBLE
    }

    private fun hideCaixaVazia() {
        binding.caixaVazia.visibility = View.INVISIBLE


    }

    private fun hideTVNoData() {
        binding.txtNoData.visibility = View.INVISIBLE


    }

    private fun escutarMediaPlayer() {
        alarmUtilsInterface.getAlarmeTocandoLiveData().observe(viewLifecycleOwner){
            if(alarmUtilsInterface.getMediaPlayerInstance() != null){
                if (alarmUtilsInterface.getMediaPlayerInstance()!!.isPlaying){
                    setAdapter(listaMedicamentos)
                }
            }

        }
    }

    fun setAdapter(medicamentos: List<MedicamentoComDoses>?){
        Log.d("testeshakingclock", "to dentro do setadapter")

            adapter = AdapterListaMedicamentos(medicamentos as ArrayList<MedicamentoComDoses>, this,context, medicamentoManager, alarmUtilsInterface)
            binding.recyclerView.adapter = adapter

        initAdapterInterface()

            hideLoading()

            setAdapterOnScrollListener()
            voltarARecyclerParaAPosicaoSalva()
            loadAlarms(medicamentos)




    }

    private fun loadAlarms(medicamentos: ArrayList<MedicamentoComDoses>) {
        medicamentos.forEach {
            val medicamentoComAlarmeAtivado = alarmUtilsInterface.verSeMedicamentoEstaComAlarmeAtivado(it.medicamentoTratamento)
            if(medicamentoComAlarmeAtivado){
                    alarmUtilsInterface.initAlarmManager(context)
                    alarmUtilsInterface.initAlarmIntent(context)
                    alarmUtilsInterface.pegarProximaDoseESetarAlarme(it)


            }
        }


    }

    private fun hideLoading() {
        Log.d("listavazia", "eu to aqui antes de esconder o cardview")



        binding.loadingProgressBar?.visibility = View.GONE
    }

    @Subscribe
    fun onAlarmeMedicamentoTocando(event: AlarmeMedicamentoTocando){
        val idMedicamento = event.idMedicamentoTocando
        Log.d("testenotrelogio", "data recebido no event bus: $idMedicamento")
        setarAlarmeTocandoParaMedicamentoId(idMedicamento)

    }

    @Subscribe(sticky = true)
    fun onMediaPlayerTocando(event: MediaPlayerTocando) {
        mediaPlayer = event.mediaPlayer
        Log.d("alarmetocando", "instancia do media player aqui no fragmento lista: $mediaPlayer")

    }

    override fun getMediaPlayerInstance(): MediaPlayer? {
        return mediaPlayer
    }





    private fun setarAlarmeTocandoParaMedicamentoId(idMedicamento: Int?) {
        listaMedicamentos.forEach {
            medicamentoComDoses ->
            if(medicamentoComDoses.medicamentoTratamento.idMedicamento == idMedicamento){
                //atualiza esse medicamento para avisar que ele ta tocando
                viewModel.alarmeMedicamentoTocando(idMedicamento, true)
            }
        }

        pegarListaAtualizada()


    }

    private fun pegarListaAtualizada() {
        viewModel.loadMedications()
        Log.d("testeshakingclock", "to aqui no metodo que vai chamar o loading medications")

    }

    private fun voltarARecyclerParaAPosicaoSalva() {
        binding.recyclerView.layoutManager?.scrollToPosition(viewModel.recyclerViewPosition)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }


    private fun setAdapterOnScrollListener() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
                val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                viewModel.onRecyclerViewScrolled(position)
            }
        })
    }




    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        if(this::viewModel.isInitialized){
            viewModel.recyclerViewPosition.let {
                outState.putInt("RECYCLER_VIEW_POSITION", it)
            }
        }
    }
    override fun onResume() {
        super.onResume()





        viewModel.loadMedications()
    }

    private fun onClickListeners() {
        binding.fab.setOnClickListener {

            findNavController().navigate(R.id.action_medicamentosFragment_to_fragmentCadastrarNovoMedicamento)
        }
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_medicamentosFragment_to_fragmentConfiguracoes)
        }
        binding.btnStopService.setOnClickListener {
                val serviceIntent = Intent(requireContext(), ServiceMediaPlayer::class.java)
                serviceIntent.action = "STOP_SERVICE"
                ContextCompat.startForegroundService(requireContext(), serviceIntent)

        }
    }



    override fun initDb() {
        db = AppDatabase.getAppDatabase(requireContext())

    }

    override fun getMedicamentoDao(): MedicamentoDao {
        return db!!.medicamentoDao
    }


    override fun launchCoroutineScope(medicamento: MedicamentoComDoses) {
        Log.d("testeinserthistorico", "eu to dentro do metodo launch coroutine scope")
        lifecycleScope.launch {

            val medicamentoDao = getMedicamentoDao()
            if(medicamento.medicamentoTratamento.diasRestantesDeTratamento > 1){
                medicamentoDao.diaConcluido(medicamento.medicamentoTratamento.diasRestantesDeTratamento - medicamento.medicamentoTratamento.diasRestantesDeTratamento, medicamento.medicamentoTratamento.nomeMedicamento)
                //medicamentoDao.resetarDosesTomadasParaDiaNovoDeTratamento(false, medicamento.medicamentoTratamento.nomeMedicamento)
                Log.d("testeinserthistorico", "eu to dentro do if do coroutine scope")

            }else{
                val sdf = getSimpleDateFormat()
                val c = Calendar.getInstance()
                val date = sdf.format(c.time)


                medicamentoDao.insertNaTabelaHistoricoMedicamentos(
                    HistoricoMedicamentos(
                        medicamento.medicamentoTratamento.nomeMedicamento,
                        medicamento.medicamentoTratamento.totalDiasTratamento,
                        date
                    )
                )


                medicamentoDao.deleteMedicamentoFromMedicamentoTratamento(medicamento.medicamentoTratamento.nomeMedicamento)
                medicamentoDao.deleteDosesDoMedicamentoFinalizado(medicamento.medicamentoTratamento.nomeMedicamento)


            }




        }
    }


    override fun onMedicamentoClick(
        proxDose: String?,
        intervaloEntreDoses: Double,
        medicamento: MedicamentoComDoses,
        medicamentoManager: MedicamentoManager
    ) {
        val action = proxDose?.let { it1 ->
            FragmentListaMedicamentosDirections.actionMedicamentosFragmentToFragmentDetalhesMedicamentos(medicamento,
                it1, intervaloEntreDoses.toString(),
                medicamentoManager
            )
        }

        if (action != null) {
            view?.findNavController()?.navigate(action)
        }


    }

    override fun changeFloatingActionButtonColor(color: Int) {
        if(!this::binding.isInitialized){
            binding = FragmentListaMedicamentosBinding.inflate(this.layoutInflater)
        }
        binding.fab.backgroundTintList = ContextCompat.getColorStateList(requireContext(), color)

    }

    @SuppressLint("SimpleDateFormat")
    private fun getSimpleDateFormat(): SimpleDateFormat {
        return SimpleDateFormat("dd/MM/yyyy")

    }




}