package com.rubens.applembretemedicamento.presentation

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.databinding.FragmentCadastrarNovoMedicamentoBinding
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import com.rubens.applembretemedicamento.framework.viewModels.ViewModelFragmentCadastrarNovoMedicamento
import com.rubens.applembretemedicamento.presentation.interfaces.MainActivityInterface
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class FragmentCadastrarNovoMedicamento: Fragment(){

    lateinit var binding: FragmentCadastrarNovoMedicamentoBinding
    private var tratamentoDuraMeses = false
    lateinit var viewModel: ViewModelFragmentCadastrarNovoMedicamento

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

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        initAds()
        initViewModel()

        viewModel.is24HourFormat = DateFormat.is24HourFormat(requireContext())
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
        initCollectors()
    }

    private fun initCollectors() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.mostrarToastMedicamentoInseridoComSucesso.collectLatest {
                    msg->
                    showToastMedicamentoInseridoComSucesso(msg)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.mostrarToastFalhaNaInsercaoDoMedicamento.collectLatest {
                    showFailedInsertToast()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.podeFecharFragmento.collectLatest {
                    fecharFragmentAtual()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.mostrarToastHoraJaPassou.collectLatest {
                    mostrarToastJaPassouHora()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.mostrarToastErroNoCadastroDoMedicamento.collectLatest {
                    showErrorCadastratingNewMedicationToast()
                }
            }
        }
    }

    private fun initObservers() {
        viewModel.medicamentos.observe(viewLifecycleOwner){
            listaMedicamentos->
            var existe = false


            listaMedicamentos?.forEach { medicamento->
                existe = verificarSeJaExisteEsseMedicamentoNaListaDeMedicamentos(medicamento)

            }
            seMedicamentoNaoExisteProsseguirComCadastro(existe)

        }





        viewModel.insertResponse.observe(viewLifecycleOwner) {
            longInsert->
            viewModel.seInsertBemSucedidoProsseguirComCadastroDasDoses(longInsert)

        }


    }



    private fun seMedicamentoNaoExisteProsseguirComCadastro(existe: Boolean) {
        if(apertouBotaoCadastrar && !existe){

            getMedicationInfoBeforeSaving()
            apertouBotaoCadastrar = false

        }

    }

    private fun verificarSeJaExisteEsseMedicamentoNaListaDeMedicamentos(medicamento: MedicamentoComDoses): Boolean {
        if(medicamento.medicamentoTratamento.nomeMedicamento == getNomeRemedioFromEditText()){
            //medicamento com mesmo nome ja existe
            Toast.makeText(requireContext(), getString(R.string.medicine_already_registered), Toast.LENGTH_LONG).show()

            return true
        }
        return false
    }

    private fun showFailedInsertToast() {
        Toast.makeText(requireContext(), getString(R.string.error_registering_medication), Toast.LENGTH_LONG)
            .show()
    }

    private fun fecharFragmentAtual() {
        findNavController().popBackStack()
    }

    private fun showToastMedicamentoInseridoComSucesso(msg: String) {
        Toast.makeText(requireContext(), "$msg ${getString(R.string.registered_successfully)}", Toast.LENGTH_LONG)
            .show()
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

        var selectedTime: String




        val timePickerDialog = TimePickerDialog(requireContext(),
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minuteOfDay ->
                var timeFormat: SimpleDateFormat
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minuteOfDay)
                if(viewModel.is24HourFormat){
                    //selectedTime = String.format("%02d:%02d", hourOfDay, minuteOfDay)
                    timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    selectedTime = timeFormat.format(calendar.time)



                }else{



                    timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    selectedTime = timeFormat.format(calendar.time)
                }

                binding.tilTimeFirstTake.editText!!.setText(selectedTime)

                //editText.setText(selectedTime)
                timeFirstTake = selectedTime
            },
            hour, minute, viewModel.is24HourFormat)



        timePickerDialog.show()

        //editText.setOnClickListener { view -> timePickerDialog.show() }
    }



    private fun getMedicationInfoBeforeSaving() {
        val qntDiasTrat: Int?
        val diaInicioTratamento: String?
        viewModel.nomeRemedio = getNomeRemedioFromEditText()
        viewModel.qntDosesStr = getQntDosesFromEditText()
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

        viewModel.qntDoses = 0

        diaInicioTratamento = binding.inputDataInicioTratamento.text.toString()

        if (viewModel.qntDosesStr != "") {
            viewModel.qntDoses = transformQntDosesFromStringToInt(viewModel.qntDosesStr)
        }
        viewModel.horarioPrimeiraDose = getTimeFirstTake()


        viewModel.seeIfMedicamentoHasValidInfo(viewModel.nomeRemedio, viewModel.qntDoses, viewModel.horarioPrimeiraDose, qntDiasTrat, diaInicioTratamento, binding.inputDataInicioTratamento.isDone, binding.inputDataInicioTratamento.masked)



    }





    private fun showErrorCadastratingNewMedicationToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.error_registering_medication_check_data),
            Toast.LENGTH_LONG
        ).show()

    }

    private fun mostrarToastJaPassouHora(){
        Toast.makeText(requireContext(), getString(R.string.dose_time_already_passed), Toast.LENGTH_LONG).show()

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