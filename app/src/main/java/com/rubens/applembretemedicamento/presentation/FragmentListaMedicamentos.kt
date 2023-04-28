package com.rubens.applembretemedicamento.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.databinding.FragmentListaMedicamentosBinding
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiver
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.viewModels.ViewModelFragmentLista
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.AdapterListaMedicamentos
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo


class FragmentListaMedicamentos : Fragment(), FuncoesDeTempo, CalendarHelper {

    private lateinit var binding: FragmentListaMedicamentosBinding
    private var listaMedicamentos: ArrayList<MedicamentoComDoses> = ArrayList()
    private lateinit var adapter: AdapterListaMedicamentos
    lateinit var viewModel: ViewModelFragmentLista




    lateinit var mAdView : AdView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setupToolbar()

        binding = FragmentListaMedicamentosBinding.inflate(inflater)




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        initViewModel()





        initAds()
        binding.dataAtual.text = pegarDataAtual()

        escutarMediaPlayer()

        onClickListeners()

        savedInstanceState?.let {
            viewModel.recyclerViewPosition = it.getInt("RECYCLER_VIEW_POSITION")
        }
    }



    private fun initAds() {
        MobileAds.initialize(requireContext()) {}


        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    private fun setupToolbar() {
        MainActivity.binding.toolbar.visibility = View.GONE
        MainActivity.binding.toolbar.title = ""

    }

    private fun initViewModel(){
        viewModel = ViewModelProvider(requireActivity())[ViewModelFragmentLista::class.java]
        viewModel.medicamentos.observe(viewLifecycleOwner){
            //recebe lista de medicamentos que usuario esta utilizando
            //a partir dessa lista eu preciso de uma lista de cada medicamento com suas doses
            Log.d("testemedicamentos", "tamanho da lista ${it.size}")
            if(it != null){
                if(it.size > 0){
                    binding.txtNoData.visibility = View.INVISIBLE
                    binding.caixaVazia.visibility = View.INVISIBLE
                }else{
                    binding.txtNoData.visibility = View.VISIBLE
                    binding.caixaVazia.visibility = View.VISIBLE
                }
                listaMedicamentos.clear()
                it.forEach {
                    if(!listaMedicamentos.contains(it)){
                        listaMedicamentos.add(it)
                    }
                }
                setAdapter(it)
            }



        }
    }

    private fun escutarMediaPlayer() {
        AlarmReceiver.alarmeTocando.observe(viewLifecycleOwner){
            if (AlarmReceiver.mp.isPlaying){
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