package com.rubens.applembretemedicamento.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isNotEmpty
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.rubens.applembretemedicamento.databinding.FragmentCadastrarNovoMedicamentoBinding
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import com.rubens.applembretemedicamento.framework.viewModels.ViewModelFragmentCadastrarNovoMedicamento
import com.rubens.applembretemedicamento.presentation.interfaces.MainActivityInterface
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo

class FragmentCadastrarNovoMedicamento : Fragment(), FuncoesDeTempo, CalendarHelper{

    lateinit var binding: FragmentCadastrarNovoMedicamentoBinding
    private lateinit var medicamento: MedicamentoTratamento
    private var tratamentoDuraMeses = false
    lateinit var viewModel: ViewModelFragmentCadastrarNovoMedicamento
    private lateinit var horarioPrimeiraDose: String
    private lateinit var nomeRemedio: String
    private lateinit var qntDosesStr: String
    private var qntDoses: Int = 0
    private var medicamentoAdicionadoObserver: MutableLiveData<MedicamentoTratamento> = MutableLiveData()
    private lateinit var mainActivityInterface: MainActivityInterface






    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCadastrarNovoMedicamentoBinding.inflate(inflater, container, false)

        initMainActivityInterface()
        setupToolbar()



        return binding.root
    }

    private fun initMainActivityInterface() {
        mainActivityInterface = requireContext() as MainActivityInterface
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        onClickListeners()

        initAds()

        initViewModel()
    }

    private fun setupToolbar() {
        mainActivityInterface.showToolbar()
        mainActivityInterface.hideToolbarTitle()
        mainActivityInterface.hideBtnDeleteMedicamento()

    }

    override fun onResume() {
        super.onResume()
        setupToolbar()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[ViewModelFragmentCadastrarNovoMedicamento::class.java]
        initObservers()
    }

    private fun initObservers() {
        viewModel.insertResponse.observe(viewLifecycleOwner) {
            longInsert->
            //long > -1 == success insert
            //long < 0 == failed insert
            if (longInsert > -1) {
                if(this::medicamento.isInitialized){
                    informarQueMedicamentoFoiAdicionado()
                    startMakingDosageTimes()
                    showToastMedicamentoInseridoComSucesso()
                    fecharFragmentAtual()
                }
            } else {
                showFailedInsertToast()

            }
        }


    }

    private fun showFailedInsertToast() {
        Toast.makeText(requireContext(), "Erro ao cadastrar medicamento", Toast.LENGTH_LONG)
            .show()
    }

    private fun fecharFragmentAtual() {
        findNavController().popBackStack()
    }

    private fun showToastMedicamentoInseridoComSucesso() {
        Toast.makeText(requireContext(), "$nomeRemedio cadastrado com sucesso!", Toast.LENGTH_LONG)
            .show()
    }

    private fun startMakingDosageTimes() {
        viewModel.dealWithDosageTime(medicamento, nomeRemedio, qntDoses, horarioPrimeiraDose)
    }

    private fun informarQueMedicamentoFoiAdicionado() {
        medicamentoAdicionadoObserver.postValue(medicamento)
    }

    private fun initAds() {
        MobileAds.initialize(requireContext()) {}


        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }


    private fun onClickListeners() {
        binding.btnDuracaoDias.setOnClickListener {
            mudarVisibilidadeDasViewsRelacionadasADuracaoTratamento()

        }
        binding.btnDuracaoMeses.setOnClickListener {
            hideContainerButtons()
            setTilMedicineTimeTreatmentHint()
            showTilMedicineTimeTreatment()
            ativarVariavelTratamentoDuraMeses()
        }


        binding.btnConfirmNewMedication.setOnClickListener {
            getMedicationInfoBeforeSaving()


        }

    }

    private fun getMedicationInfoBeforeSaving() {
        val qntDiasTrat: Int?
        nomeRemedio = getNomeRemedioFromEditText()
        qntDosesStr = getQntDosesFromEditText()
        val qntDuracaoTratamentoStr = getDuracaoTratamentoFromEditText()
        qntDiasTrat = if (qntDuracaoTratamentoStr.isNotEmpty()) {
            if (tratamentoDuraMeses) {
                transformarMesesEmDias(qntDuracaoTratamentoStr)
            } else {
                transformDuracaoEmDiasStringToInt(qntDuracaoTratamentoStr)
            }
        } else {
            null
        }

        qntDoses = 0

        if (qntDosesStr != "") {
            qntDoses = transformQntDosesFromStringToInt(qntDosesStr)
        }
        horarioPrimeiraDose = getTimeFirstTakeFromEditText()

        seeIfMedicamentoHasValidInfo(nomeRemedio, qntDoses, horarioPrimeiraDose, qntDiasTrat)


    }

    private fun seeIfMedicamentoHasValidInfo(
        nomeRemedio: String,
        qntDoses: Int,
        horarioPrimeiraDose: String,
        qntDiasTrat: Int?
    ) {

        if (qntDoses > 0 && nomeRemedio.isNotEmpty() && horarioPrimeiraDose.isNotEmpty() && horarioPrimeiraDose.length == 5 && horarioPrimeiraDose[2].toString() == ":" && binding.tilNumberOfPillsByTake.editText!!.text != null && binding.tilNumberOfPillsByTake.editText!!.text.toString() != "" && binding.tilNumberOfPillsByTake.isNotEmpty() && binding.inputDataInicioTratamento.isDone && qntDiasTrat != null
        ) {
            saveNewMedication(nomeRemedio, qntDoses, horarioPrimeiraDose, qntDiasTrat)

        } else {
            showErrorCadastratingNewMedicationToast()

        }

    }

    private fun showErrorCadastratingNewMedicationToast() {
        Toast.makeText(
            requireContext(),
            "Erro ao cadastrar medicamento. Verifique o preenchimento dos dados.",
            Toast.LENGTH_LONG
        ).show()

    }

    private fun saveNewMedication(nomeRemedio: String, qntDoses: Int, horarioPrimeiraDose: String, qntDiasTrat: Int) {
        medicamento = MedicamentoTratamento(
            nomeMedicamento = nomeRemedio,
            totalDiasTratamento = qntDiasTrat,
            horaPrimeiraDose = horarioPrimeiraDose,
            qntDoses = qntDoses,
            num_doses_num_unico_horario = binding.tilNumberOfPillsByTake.editText!!.text.toString()
                .toInt(),
            tratamentoFinalizado = false,
            diasRestantesDeTratamento = qntDiasTrat,
            dataInicioTratamento = binding.inputDataInicioTratamento.masked,
            dataTerminoTratamento = pegarDataDeTermino(
                binding.inputDataInicioTratamento.masked,
                qntDiasTrat
            ),
            stringDataStore = "toast_already_shown"+"_$nomeRemedio"
        )
        viewModel.insertMedicamento(
            medicamento
        )

    }

    private fun getTimeFirstTakeFromEditText(): String {
        return binding.tilTimeFirstTake.editText?.text.toString()

    }

    private fun transformQntDosesFromStringToInt(qntDosesStr: String): Int {
        return qntDosesStr.toInt()

    }

    private fun transformDuracaoEmDiasStringToInt(qntDuracaoTratamentoStr: String): Int {
        return qntDuracaoTratamentoStr.toInt()
    }

    private fun transformarMesesEmDias(qntDuracaoTratamentoStr: String): Int {
        return qntDuracaoTratamentoStr.toInt() * 30

    }

    private fun getDuracaoTratamentoFromEditText(): String {
        return binding.tilMedicineTimeTreatment.editText?.text.toString()
    }

    private fun getQntDosesFromEditText(): String {
        return binding.tilMedicineQntPerDay.editText?.text.toString()

    }

    private fun getNomeRemedioFromEditText(): String {
        return binding.tilMedicineName.editText?.text.toString()
    }

    private fun ativarVariavelTratamentoDuraMeses() {
        tratamentoDuraMeses = true
    }

    private fun showTilMedicineTimeTreatment() {
        binding.tilMedicineTimeTreatment.visibility = View.VISIBLE
    }

    private fun setTilMedicineTimeTreatmentHint() {
        binding.tilMedicineTimeTreatment.hint = "Quantos meses?"
    }

    private fun hideContainerButtons() {
        binding.containerButtons.visibility = View.INVISIBLE

    }

    fun mudarVisibilidadeDasViewsRelacionadasADuracaoTratamento() {
        binding.containerButtons.visibility = View.INVISIBLE
        binding.tilMedicineTimeTreatment.hint = "Quantos dias?"
        binding.tilMedicineTimeTreatment.visibility = View.VISIBLE
    }






}