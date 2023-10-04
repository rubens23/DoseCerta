package com.rubens.applembretemedicamento.presentation

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.databinding.FragmentConfiguracoesBinding
import com.rubens.applembretemedicamento.framework.data.datastore.interfaces.ThemeDataStoreInterface
import com.rubens.applembretemedicamento.framework.data.entities.ConfiguracoesEntity
import com.rubens.applembretemedicamento.framework.data.managers.RoomAccess
import com.rubens.applembretemedicamento.framework.viewModels.ActivityHostAndFragmentConfikguracoesSharedViewModel
import com.rubens.applembretemedicamento.framework.viewModels.ViewModelFragmentConfiguracoes
import com.rubens.applembretemedicamento.presentation.interfaces.MainActivityInterface
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FragmentConfiguracoes @Inject constructor(
    private val roomAccess: RoomAccess
) : Fragment() {

    private lateinit var binding: FragmentConfiguracoesBinding
    private lateinit var mainActivityInterface: MainActivityInterface
    private lateinit var sharedViewModel: ActivityHostAndFragmentConfikguracoesSharedViewModel
    private lateinit var viewModel: ViewModelFragmentConfiguracoes
    private var temaEscolhido = ""





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentConfiguracoesBinding.inflate(inflater)
        Log.d("bugconfig", "to aqui antes de chamar a toolbar")
        setupToolbar()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initAds()
        setupSwitchers()
        onClickListeners()
    }

    private fun setupSwitchers() {
        val configuracoes = viewModel.getSwitchersState()
        if (configuracoes != null){
            binding.toggleAtivadoDepoisDeDesligar.isChecked = configuracoes.podeTocarDepoisDeReiniciar
            binding.toggleTocarAlarmeSemSom.isChecked = configuracoes.podeTocarSom
        }else{
            viewModel.mudarConfiguracoes(ConfiguracoesEntity())
        }

    }

    private fun initViewModel() {
        sharedViewModel = ViewModelProvider(requireActivity())[ActivityHostAndFragmentConfikguracoesSharedViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity())[ViewModelFragmentConfiguracoes::class.java]
        initObservers()
    }

    private fun initObservers() {
        sharedViewModel.temaAtual.observe(viewLifecycleOwner){
            temaAtual->
            setarOTemaEscolhido(temaAtual)
        }
    }

    private fun setarOTemaEscolhido(temaAtual: String) {
        temaEscolhido = temaAtual

    }

    private fun onClickListeners() {
        binding.ivTemaAzul.setOnClickListener {
            if(temaEscolhido != "Azul"){
                mudarParaOTemaAzul()
                salvarColorResourceNoBanco("Azul")
                sharedViewModel.mudouTema(true)
            }


        }

        binding.ivTemaVermelho.setOnClickListener{
            if(temaEscolhido != "Vermelho"){
                mudarParaOTemaVermelho()
                salvarColorResourceNoBanco("Vermelho")
                sharedViewModel.mudouTema(true)
            }

        }


        binding.toggleAtivadoDepoisDeDesligar.setOnCheckedChangeListener {
                _, isChecked ->
            if (isChecked){
                viewModel.mudarConfiguracoes(ConfiguracoesEntity(podeTocarDepoisDeReiniciar = true))
            }else{
                viewModel.mudarConfiguracoes(ConfiguracoesEntity(podeTocarDepoisDeReiniciar = false))

            }

        }

        binding.toggleTocarAlarmeSemSom.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                viewModel.mudarConfiguracoes(ConfiguracoesEntity(podeTocarSom = true))
            }else{
                viewModel.mudarConfiguracoes(ConfiguracoesEntity(podeTocarSom = false))

            }
        }
    }

    private fun salvarColorResourceNoBanco(color: String) {
        val configuracoes = roomAccess.pegarConfiguracoes()

        if(color == "Azul"){
            if(configuracoes != null){
                roomAccess.colocarConfiguracoesAtualizadas(ConfiguracoesEntity(configuracoes.idConfiguracao, configuracoes.podeTocarDepoisDeReiniciar, colorResource = R.color.blue ))

            }

        }else{
            if(configuracoes != null){
                roomAccess.colocarConfiguracoesAtualizadas(ConfiguracoesEntity(configuracoes.idConfiguracao, configuracoes.podeTocarDepoisDeReiniciar, colorResource = R.color.rosa_salmao ))

            }
            //cor é vermelha
        }
    }

    private fun mudarParaOTemaVermelho() {
        mainActivityInterface.changeThemeToRedTheme()
    }

    private fun mudarParaOTemaAzul() {
        mainActivityInterface.changeThemeToBlueTheme()

    }

    private fun setupToolbar() {
        Log.d("bugconfig", "to aqui no setup toolbar $mainActivityInterface")

        mainActivityInterface.showToolbar()
        mainActivityInterface.hideToolbarTitle()
        mainActivityInterface.hideBtnDeleteMedicamento()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initMainActivityInterface(context)

    }

    private fun initMainActivityInterface(context: Context) {
        mainActivityInterface = context as MainActivityInterface

    }


    private fun initAds() {
        MobileAds.initialize(requireContext()) {}


        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }



}