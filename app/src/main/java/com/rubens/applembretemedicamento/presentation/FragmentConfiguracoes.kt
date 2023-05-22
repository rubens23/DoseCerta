package com.rubens.applembretemedicamento.presentation

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.databinding.FragmentConfiguracoesBinding
import com.rubens.applembretemedicamento.framework.data.datastore.interfaces.ThemeDataStoreInterface
import com.rubens.applembretemedicamento.presentation.interfaces.MainActivityInterface

class FragmentConfiguracoes : Fragment() {

    private lateinit var binding: FragmentConfiguracoesBinding
    private lateinit var mainActivityInterface: MainActivityInterface





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentConfiguracoesBinding.inflate(inflater)
        setupToolbar()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClickListeners()
    }

    private fun onClickListeners() {
        binding.ivTemaAzul.setOnClickListener {
            mudarParaOTemaAzul()

        }

        binding.ivTemaVermelho.setOnClickListener{
            mudarParaOTemaVermelho()
        }
    }

    private fun mudarParaOTemaVermelho() {
        mainActivityInterface.changeThemeToRedTheme()
    }

    private fun mudarParaOTemaAzul() {
        mainActivityInterface.changeThemeToBlueTheme()

    }

    private fun setupToolbar() {
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


}