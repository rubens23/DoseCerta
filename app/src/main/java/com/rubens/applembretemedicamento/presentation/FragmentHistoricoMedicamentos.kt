package com.rubens.applembretemedicamento.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.databinding.FragmentDetalhesMedicamentosBinding
import com.rubens.applembretemedicamento.databinding.FragmentHistoricoMedicamentosBinding
import com.rubens.applembretemedicamento.framework.data.AppDatabase
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.HistoricoMedicamentos
import com.rubens.applembretemedicamento.framework.viewModels.ViewModelFragmentHistoricoMedicamentos
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.HistoricoMedicamentosAdapter


class FragmentHistoricoMedicamentos : Fragment() {

    private lateinit var adapter: HistoricoMedicamentosAdapter
    private var db: AppDatabase? = null
    private var listaMedicamentos: ArrayList<HistoricoMedicamentos> = ArrayList()

    private lateinit var binding: FragmentHistoricoMedicamentosBinding
    private lateinit var viewModel: ViewModelFragmentHistoricoMedicamentos



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoricoMedicamentosBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initObservers()

        initAds()

        viewModel.carregarMedicamentosFinalizados()




    }

    private fun initObservers() {
        viewModel.medicamentos.observe(viewLifecycleOwner){
            listaMedicamentos.clear()
            listaMedicamentos.addAll(it)
            it.forEach {
                Log.d("capturahistorico", "consegui capturar o ${it.nomeMedicamento}")

            }
            setupAdapter()


        }
    }

    private fun setupAdapter() {
        adapter = HistoricoMedicamentosAdapter(listaMedicamentos)
        binding.recyclerViewHistoricoMedicamentos.adapter = adapter
    }


    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[ViewModelFragmentHistoricoMedicamentos::class.java]
    }

    private fun initAds() {
        MobileAds.initialize(requireContext()) {}


        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

}