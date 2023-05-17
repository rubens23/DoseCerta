package com.rubens.applembretemedicamento.presentation

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiver
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiverInterface
import com.rubens.applembretemedicamento.framework.data.AppDatabase
import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.HistoricoMedicamentos
import com.rubens.applembretemedicamento.framework.domain.AlarmEvent
import com.rubens.applembretemedicamento.framework.domain.AlarmeMedicamentoTocando
import com.rubens.applembretemedicamento.framework.domain.MediaPlayerTocando
import com.rubens.applembretemedicamento.framework.domain.MedicamentoManager
import com.rubens.applembretemedicamento.framework.singletons.AlarmReceiverSingleton
import com.rubens.applembretemedicamento.framework.viewModels.ViewModelFragmentLista
import com.rubens.applembretemedicamento.presentation.interfaces.AdapterListaMedicamentosInterface
import com.rubens.applembretemedicamento.presentation.interfaces.FragmentListaMedicamentosInterface
import com.rubens.applembretemedicamento.presentation.interfaces.MainActivityInterface
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.AdapterListaMedicamentos
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.SimpleDateFormat
import java.util.Calendar


class FragmentListaMedicamentos : Fragment(), FuncoesDeTempo, CalendarHelper, FragmentListaMedicamentosInterface {

    private lateinit var binding: FragmentListaMedicamentosBinding
    private var listaMedicamentos: ArrayList<MedicamentoComDoses> = ArrayList()
    private lateinit var adapter: AdapterListaMedicamentos
    lateinit var viewModel: ViewModelFragmentLista
    private lateinit var mainActivityInterface: MainActivityInterface
    private lateinit var alarmReceiverInterface: AlarmReceiverInterface
    private lateinit var alarmReceiver: AlarmReceiver
    private var db: AppDatabase? = null
    private var isEventBusRegistered = false
    private lateinit var adapterListaMedicamentosInterface: AdapterListaMedicamentosInterface

    private var mediaPlayer: MediaPlayer? = null





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        initMainActivityInterface()
        initAlarmReceiver()
        initAlarmReceiverInterface()
        setupToolbar()

        binding = FragmentListaMedicamentosBinding.inflate(inflater)
        return binding.root
    }

    private fun initAlarmReceiver() {
        alarmReceiver = AlarmReceiverSingleton.getInstance()
    }



    private fun initAlarmReceiverInterface() {
        alarmReceiverInterface = alarmReceiver
    }

    private fun initMainActivityInterface() {
        mainActivityInterface = requireContext() as MainActivityInterface
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
        binding.dataAtual.text = pegarDataAtual()

    }


    private fun initAds() {
        MobileAds.initialize(requireContext()) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    private fun setupToolbar() {
        mainActivityInterface.hideToolbar()
        mainActivityInterface.hideToolbarTitle()

    }

    private fun initViewModel(){
        viewModel = ViewModelProvider(requireActivity())[ViewModelFragmentLista::class.java]
        initObservers()

    }

    private fun initObservers() {
        viewModel.medicamentos.observe(viewLifecycleOwner){
           listaMedicamentoComDoses->
            Log.d("testeshakingclock", "to dentro do observer de getMedicamentos")

            if(listaMedicamentoComDoses != null){
                if(listaMedicamentoComDoses.isNotEmpty()){
                    hideTVNoData()
                    hideCaixaVazia()
                }else{
                    showTVNoData()
                    showCaixaVazia()

                }
                pegarListaAtualizadaDeMedicamentos(listaMedicamentoComDoses)

                setAdapter(listaMedicamentos)
            }




        }
    }

    private fun pegarListaAtualizadaDeMedicamentos(listaMedicamentoComDoses: List<MedicamentoComDoses>) {
        listaMedicamentos.clear()



        listaMedicamentoComDoses.forEach {
            Log.d("listaprincipal", "${it.medicamentoTratamento.nomeMedicamento} tempo restante de tratamento: ${it.medicamentoTratamento.diasRestantesDeTratamento} dia, ultima dose horario: ${it.listaDoses[it.listaDoses.size-1].horarioDose} dose ja tomada? ${it.listaDoses[it.listaDoses.size-1].jaTomouDose}")
            if(it.medicamentoTratamento.diasRestantesDeTratamento == 1){
                if(!it.listaDoses[it.listaDoses.size-1].jaTomouDose){
                    passarMedicamentoParaListaMedicamentos(it)
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
        alarmReceiverInterface.getAlarmeTocandoLiveData().observe(viewLifecycleOwner){
            if (alarmReceiverInterface.getMediaPlayerInstance().isPlaying){
                setAdapter(listaMedicamentos)
            }
        }
    }

    fun setAdapter(medicamentos: List<MedicamentoComDoses>?){
        Log.d("testeshakingclock", "to dentro do setadapter")

            adapter = AdapterListaMedicamentos(medicamentos as ArrayList<MedicamentoComDoses>, this)
            binding.recyclerView.adapter = adapter

        initAdapterInterface()

            hideLoading()

            setAdapterOnScrollListener()
            voltarARecyclerParaAPosicaoSalva()




    }

    private fun hideLoading() {
        Log.d("listavazia", "eu to aqui antes de esconder o cardview")



        binding.cardViewLoading?.visibility = View.GONE
        binding.loadingProgressBar?.visibility = View.GONE
    }

    @Subscribe
    fun onAlarmeMedicamentoTocando(event: AlarmeMedicamentoTocando){
        val idMedicamento = event.idMedicamentoTocando
        Log.d("testenotrelogio", "data recebido no event bus: $idMedicamento")
        setarAlarmeTocandoParaMedicamentoId(idMedicamento)

    }

    @Subscribe
    fun onMediaPlayerTocando(event: MediaPlayerTocando) {
        mediaPlayer = event.mp
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
                val position = layoutManager.findFirstVisibleItemPosition()
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
    }

    override fun onStop() {
        super.onStop()

    }

    override fun initDb() {
        db = AppDatabase.getAppDatabase(requireContext())

    }

    override fun getMedicamentoDao(): MedicamentoDao {
        return db!!.medicamentoDao
    }


    override fun launchCoroutineScope(medicamento: MedicamentoComDoses) {
        //todo fazer a viewModel correspondente fazer essas operações
        lifecycleScope.launch {

            val medicamentoDao = getMedicamentoDao()
            if(medicamento.medicamentoTratamento.diasRestantesDeTratamento > 1){
                medicamentoDao.diaConcluido(medicamento.medicamentoTratamento.diasRestantesDeTratamento - 1, medicamento.medicamentoTratamento.nomeMedicamento)
                medicamentoDao.resetarDosesTomadasParaDiaNovoDeTratamento(false, medicamento.medicamentoTratamento.nomeMedicamento)
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

    @SuppressLint("SimpleDateFormat")
    private fun getSimpleDateFormat(): SimpleDateFormat {
        return SimpleDateFormat("dd/MM/yyyy")

    }


}