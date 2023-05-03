package com.rubens.applembretemedicamento.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.databinding.FragmentListaMedicamentosBinding
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiver
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiverInterface
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.viewModels.ViewModelFragmentLista
import com.rubens.applembretemedicamento.presentation.interfaces.MainActivityInterface
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.AdapterListaMedicamentos
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo


class FragmentListaMedicamentos : Fragment(), FuncoesDeTempo, CalendarHelper {

    private lateinit var binding: FragmentListaMedicamentosBinding
    private var listaMedicamentos: ArrayList<MedicamentoComDoses> = ArrayList()
    private lateinit var adapter: AdapterListaMedicamentos
    lateinit var viewModel: ViewModelFragmentLista
    private lateinit var mainActivityInterface: MainActivityInterface
    private lateinit var alarmReceiverInterface: AlarmReceiverInterface
    private lateinit var alarmReceiver: AlarmReceiver





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        initMainActivityInterface()
        initAlarmReceiver()
        initAlarmReceiverInterface()
        setupToolbar()

        binding = FragmentListaMedicamentosBinding.inflate(inflater)
        return binding.root
    }

    private fun initAlarmReceiver() {
        alarmReceiver = AlarmReceiver()
    }

    private fun initAlarmReceiverInterface() {
        alarmReceiverInterface = alarmReceiver as AlarmReceiverInterface
    }

    private fun initMainActivityInterface() {
        mainActivityInterface = requireContext() as MainActivityInterface
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()

        initAds()
        colocarDataAtualNaTextViewDaData()

        escutarMediaPlayer()

        onClickListeners()

        getRecyclerViewPositionIfItWasSaved(savedInstanceState)

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
            if(listaMedicamentoComDoses != null){
                if(listaMedicamentoComDoses.isNotEmpty()){
                    hideTVNoData()
                    hideCaixaVazia()
                }else{
                    showTVNoData()
                    showCaixaVazia()
                }
                updateListaMedicamento(listaMedicamentoComDoses)

                setAdapter(listaMedicamentoComDoses)
            }



        }
    }

    private fun updateListaMedicamento(listaMedicamentoComDoses: List<MedicamentoComDoses>) {
        listaMedicamentos.clear()
        listaMedicamentoComDoses.forEach {
            if(!listaMedicamentos.contains(it)){
                listaMedicamentos.add(it)
            }
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

    fun setAdapter(medicamentos: List<MedicamentoComDoses>?) {
        adapter = AdapterListaMedicamentos(medicamentos as ArrayList<MedicamentoComDoses>, requireContext())
        adapter.listaComDosesToast.observe(viewLifecycleOwner){

        }
        binding.recyclerView.adapter = adapter

        setAdapterOnScrollListener()
        voltarARecyclerParaAPosicaoSalva()

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
            viewModel.recyclerViewPosition?.let {
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


}