package com.rubens.applembretemedicamento.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.rubens.applembretemedicamento.databinding.FragmentHistoricoMedicamentosBinding
import com.rubens.applembretemedicamento.framework.data.entities.HistoricoMedicamentos
import com.rubens.applembretemedicamento.framework.viewModels.ViewModelFragmentHistoricoMedicamentos
import com.rubens.applembretemedicamento.presentation.interfaces.MainActivityInterface
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.HistoricoMedicamentosAdapter


class FragmentHistoricoMedicamentos : Fragment() {

    private lateinit var adapter: HistoricoMedicamentosAdapter
    private var listaMedicamentos: ArrayList<HistoricoMedicamentos> = ArrayList()

    private lateinit var binding: FragmentHistoricoMedicamentosBinding
    private lateinit var viewModel: ViewModelFragmentHistoricoMedicamentos

    private lateinit var mainActivityInterface: MainActivityInterface



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoricoMedicamentosBinding.inflate(inflater)

        Log.d("ciclodevida19", "to no oncreate view do fragmentHistorico")



        setupToolbar()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        initMainActivityInterface(context)

    }

    private fun initMainActivityInterface(context: Context) {
        mainActivityInterface = context as MainActivityInterface
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initObservers()
        initAds()
        carregarMedicamentosFinalizados()

        Log.d("ciclodevida19", "to no onviewcreated do fragmentHistorico")

    }

    private fun setupToolbar() {
        mainActivityInterface.showToolbar()
        mainActivityInterface.hideToolbarTitle()
        mainActivityInterface.hideBtnDeleteMedicamento()

    }

    private fun carregarMedicamentosFinalizados() {
        viewModel.carregarMedicamentosFinalizados()
    }

    private fun initObservers() {
        viewModel.medicamentos.observe(viewLifecycleOwner){
            listaHistoricoMedicamentos->
            updateListaMedicamentosFinalizados(listaHistoricoMedicamentos)
            setupAdapter()


        }
    }

    private fun updateListaMedicamentosFinalizados(listaHistoricoMedicamentos: List<HistoricoMedicamentos>) {
        listaMedicamentos.clear()
        listaMedicamentos.addAll(listaHistoricoMedicamentos)
    }

    private fun setupAdapter() {
        adapter = HistoricoMedicamentosAdapter(listaMedicamentos)
        binding.recyclerViewHistoricoMedicamentos.adapter = adapter
    }


    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[ViewModelFragmentHistoricoMedicamentos::class.java]
    }

    override fun onResume() {
        super.onResume()

        setupToolbar()


        Log.d("ciclodevida19", "to no onresume do fragmentHistorico")
    }

    override fun onStop() {
        super.onStop()

        Log.d("ciclodevida19", "to no onstop view do fragmentHistorico")


    }


    private fun initAds() {
        MobileAds.initialize(requireContext()) {}


        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

}