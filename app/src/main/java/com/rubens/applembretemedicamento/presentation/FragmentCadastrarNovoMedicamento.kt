package com.rubens.applembretemedicamento.presentation

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.databinding.FragmentCadastrarNovoMedicamentoBinding
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import com.rubens.applembretemedicamento.framework.viewModels.ViewModelFragmentCadastrarNovoMedicamento
import com.rubens.applembretemedicamento.presentation.interfaces.MainActivityInterface
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class FragmentCadastrarNovoMedicamento @Inject constructor(
    private val funcoesDeTempo: FuncoesDeTempo,
    private val calendarHelper: CalendarHelper
) : Fragment(){

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
    private var apertouBotaoCadastrar = false
    private var timeFirstTake = ""






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
        mainActivityInterface = requireActivity() as MainActivityInterface
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        initAds()

        initViewModel()

        onClickListeners()

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
        Log.d("perseguindofluxo", "entrei aqui no init observers")
        viewModel.medicamentos.observe(viewLifecycleOwner){
            listaMedicamentos->
            Log.d("perseguindofluxo", "entrei aqui no observer")

            if(listaMedicamentos != null){
                Log.d("perseguindofluxo", "lista nao é nula")

                //verifica se medicamento ja existe
                listaMedicamentos.forEach {
                    medicamento->
                    if(medicamento.medicamentoTratamento.nomeMedicamento == getNomeRemedioFromEditText()){
                        //medicamento com mesmo nome ja existe
                        Log.d("perseguindofluxo", "ja tem um medicmaento com esse nome")
                        Toast.makeText(requireContext(), getString(R.string.medicine_already_registered), Toast.LENGTH_LONG).show()

                        return@observe
                    }
                }
                if(apertouBotaoCadastrar){
                    Log.d("perseguindofluxo", "apertou o botao é true e ntrei aqui no if")

                    getMedicationInfoBeforeSaving()
                    apertouBotaoCadastrar = false

                }

            }else{
                Log.d("perseguindofluxo", "a lista é nula")

                if(apertouBotaoCadastrar){
                    Log.d("perseguindofluxo", "apertou o botao é true e ntrei aqui no if")

                    getMedicationInfoBeforeSaving()
                    apertouBotaoCadastrar = false
                }
                //nenhum medicamento existe entao nao tem como o medicamento existir
                //continua para cadastrar esse medicamento
            }
        }
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
        Toast.makeText(requireContext(), getString(R.string.error_registering_medication), Toast.LENGTH_LONG)
            .show()
    }

    private fun fecharFragmentAtual() {
        findNavController().popBackStack()
    }

    private fun showToastMedicamentoInseridoComSucesso() {
        Toast.makeText(requireContext(), "$nomeRemedio ${getString(R.string.registered_successfully)}", Toast.LENGTH_LONG)
            .show()
    }

    private fun startMakingDosageTimes() {
        viewModel.gerenciarHorariosDosagem(medicamento, nomeRemedio, qntDoses, horarioPrimeiraDose)
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
            apertouBotaoCadastrar = true
            viewModel.loadMedications()




        }
        binding.btnOpenTimePicker.setOnClickListener {
            createTimePickerDialog()
        }

    }

    private fun createTimePickerDialog() {
        val cal: Calendar = Calendar.getInstance()
        val hour: Int = cal.get(Calendar.HOUR_OF_DAY)
        val minute: Int = cal.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(),
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minuteOfDay ->
                //Manipule o horário selecionado pelo usuário aqui
                val selectedTime = String.format("%02d:%02d", hourOfDay, minuteOfDay)
                binding.tilTimeFirstTake.editText!!.setText(selectedTime)

                //editText.setText(selectedTime)
                timeFirstTake = selectedTime
            },
            hour, minute, true)



        timePickerDialog.show()

        //editText.setOnClickListener { view -> timePickerDialog.show() }
    }



    private fun getMedicationInfoBeforeSaving() {
        val qntDiasTrat: Int?
        val diaInicioTratamento: String?
        nomeRemedio = getNomeRemedioFromEditText()
        qntDosesStr = getQntDosesFromEditText()
        timeFirstTake = binding.tilTimeFirstTake.editText?.text.toString()
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

        diaInicioTratamento = binding.inputDataInicioTratamento.text.toString()

        if (qntDosesStr != "") {
            qntDoses = transformQntDosesFromStringToInt(qntDosesStr)
        }
        horarioPrimeiraDose = getTimeFirstTake()


        seeIfMedicamentoHasValidInfo(nomeRemedio, qntDoses, horarioPrimeiraDose, qntDiasTrat, diaInicioTratamento)


    }



    private fun seeIfMedicamentoHasValidInfo(
        nomeRemedio: String,
        qntDoses: Int,
        horarioPrimeiraDose: String,
        qntDiasTrat: Int?,
        diaInicioTratamento: String
    ) {

        if (diaInicioTratamento.length == 10 && diaInicioTratamento.isNotEmpty() && qntDoses > 0 && nomeRemedio.isNotEmpty() && horarioPrimeiraDose.isNotEmpty() && horarioPrimeiraDose.length == 5 && horarioPrimeiraDose[2].toString() == ":" && binding.inputDataInicioTratamento.isDone && qntDiasTrat != null
            && horarioPrimeiraDose.isNotEmpty() && horarioPrimeiraDose.length == 5 && horarioPrimeiraDose[2].toString() == ":") {
            if(!calendarHelper.verificarSeDataHoraJaPassou("$diaInicioTratamento $horarioPrimeiraDose")){


                saveNewMedication(nomeRemedio, qntDoses, horarioPrimeiraDose, qntDiasTrat)
            }else{
                Toast.makeText(requireContext(), getString(R.string.dose_time_already_passed), Toast.LENGTH_LONG).show()
            }



        } else {
            showErrorCadastratingNewMedicationToast()

        }

    }

    private fun showErrorCadastratingNewMedicationToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.error_registering_medication_check_data),
            Toast.LENGTH_LONG
        ).show()

    }

    private fun saveNewMedication(nomeRemedio: String, qntDoses: Int, horarioPrimeiraDose: String, qntDiasTrat: Int) {
        medicamento = MedicamentoTratamento(
            nomeMedicamento = nomeRemedio,
            totalDiasTratamento = qntDiasTrat,
            horaPrimeiraDose = horarioPrimeiraDose,
            qntDoses = qntDoses,
            tratamentoFinalizado = false,
            diasRestantesDeTratamento = qntDiasTrat,
            dataInicioTratamento = binding.inputDataInicioTratamento.masked,
            dataTerminoTratamento = funcoesDeTempo.pegarDataDeTermino(
                binding.inputDataInicioTratamento.masked,
                qntDiasTrat
            ),
            stringDataStore = "toast_already_shown"+"_$nomeRemedio"
        )
        viewModel.insertMedicamento(
            medicamento
        )

    }

    private fun getTimeFirstTake(): String {
        return timeFirstTake

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
        binding.tilMedicineTimeTreatment.hint = getString(R.string.how_many_months)
    }

    private fun hideContainerButtons() {
        binding.containerButtons.visibility = View.INVISIBLE

    }

    fun mudarVisibilidadeDasViewsRelacionadasADuracaoTratamento() {
        binding.containerButtons.visibility = View.INVISIBLE
        binding.tilMedicineTimeTreatment.hint = getString(R.string.how_many_days)
        binding.tilMedicineTimeTreatment.visibility = View.VISIBLE
    }






}