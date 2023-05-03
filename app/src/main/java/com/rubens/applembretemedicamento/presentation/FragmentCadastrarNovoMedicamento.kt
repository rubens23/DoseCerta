package com.rubens.applembretemedicamento.presentation

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isNotEmpty
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.databinding.FragmentCadastrarNovoMedicamentoBinding
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import com.rubens.applembretemedicamento.framework.viewModels.ViewModelFragmentCadastrarNovoMedicamento
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import com.rubens.applembretemedicamento.utils.comunicacaoFragmentAdapter

class FragmentCadastrarNovoMedicamento : Fragment(), FuncoesDeTempo, CalendarHelper{

    lateinit var binding: FragmentCadastrarNovoMedicamentoBinding
    private lateinit var medicamento: MedicamentoTratamento
    private var tratamentoDuraMeses = false
    private var listaHorarioDoses = ArrayList<Doses>()
    lateinit var viewModel: ViewModelFragmentCadastrarNovoMedicamento
    private lateinit var horarioPrimeiraDose: String
    private lateinit var nomeRemedio: String
    private lateinit var qntDosesStr: String
    private var qntDoses: Int = 0
    private var medicamentoAdicionadoObserver: MutableLiveData<MedicamentoTratamento> = MutableLiveData()





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCadastrarNovoMedicamentoBinding.inflate(inflater, container, false)

        setupToolbar()



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        onClickListeners()

        initAds()

        initViewModel()
    }

    private fun setupToolbar() {
        MainActivity.binding.toolbar.visibility = View.VISIBLE
        MainActivity.binding.toolbar.title = ""
        MainActivity.binding.btnDeleteMedicamento.visibility = View.GONE

    }

    override fun onResume() {
        super.onResume()
        setupToolbar()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[ViewModelFragmentCadastrarNovoMedicamento::class.java]
        viewModel.insertResponse.observe(viewLifecycleOwner) {
            Log.d("observerinsertnovo", "eu adicionei um medicamento novo")
            if (it > -1) {
                if(this::medicamento.isInitialized){
                    medicamentoAdicionadoObserver.postValue(medicamento)

                    viewModel.dealWithDosageTime(medicamento, nomeRemedio, qntDoses, horarioPrimeiraDose)
                    Toast.makeText(requireContext(), "${nomeRemedio} cadastrado com sucesso!", Toast.LENGTH_LONG)
                        .show()

                    findNavController().popBackStack()
                }
                //pode colocar as doses na tabela de doses

            } else {
                Toast.makeText(requireContext(), "Erro ao cadastrar medicamento", Toast.LENGTH_LONG)
                    .show()
            }
        }
        viewModel.insertDosesResponse.observe(viewLifecycleOwner) {
            Log.d("observerdosesinserter", "resultado do insert ddas doses: ${it}")
        }

        medicamentoAdicionadoObserver.observe(viewLifecycleOwner) {
            Log.d("testeobmedicamento", "medicamento adicionado com sucesso")
            Log.d("testeobmedicamento", "${it.nomeMedicamento} ${it.num_doses_num_unico_horario}")
            //assim que eu obtiver uma instancia não nula de medicamento, eu posso usar essa instancia para passar o numero de doses por hora para as linhas da tabela Doses
        }

        //pegar o medicamento que acabou de ser passado para o banco
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
            Toast.makeText(requireContext(), "meses", Toast.LENGTH_LONG).show()
            binding.containerButtons.visibility = View.INVISIBLE
            binding.tilMedicineTimeTreatment.hint = "Quantos meses?"
            binding.tilMedicineTimeTreatment.visibility = View.VISIBLE
            tratamentoDuraMeses = true

        }


        binding.btnConfirmNewMedication.setOnClickListener {
            val qntDiasTrat: Int?
            nomeRemedio = binding.tilMedicineName.editText?.text.toString()
            qntDosesStr = binding.tilMedicineQntPerDay.editText?.text.toString()
            val qntTratamentoStr = binding.tilMedicineQntPerDay.editText?.text.toString()
            val qntDuracaoTratamentoStr = binding.tilMedicineTimeTreatment.editText?.text.toString()
            if (qntDuracaoTratamentoStr.isNotEmpty()) {
                if (tratamentoDuraMeses) {
                    qntDiasTrat = qntDuracaoTratamentoStr.toInt() * 30
                } else {
                    qntDiasTrat = qntDuracaoTratamentoStr.toInt()
                }
            } else {
                qntDiasTrat = null
            }


            qntDoses = 0
            var sucesso = 0

            if (qntDosesStr != "") {
                qntDoses = qntDosesStr.toInt()
            }
            horarioPrimeiraDose = binding.tilTimeFirstTake.editText?.text.toString()





            if (nomeRemedio != null
                &&
                qntDoses != null &&
                qntDoses > 0 &&
                horarioPrimeiraDose != null &&
                nomeRemedio.isNotEmpty() &&
                horarioPrimeiraDose.isNotEmpty()
                && horarioPrimeiraDose.length == 5
                && horarioPrimeiraDose[2].toString() == ":"
                && binding.tilNumberOfPillsByTake.editText!!.text != null
                && binding.tilNumberOfPillsByTake.editText!!.text.toString() != ""
                && binding.tilNumberOfPillsByTake.isNotEmpty()
                && binding.inputDataInicioTratamento.isDone
                && qntDiasTrat != null
            ) {
                //aplicar mascara aqui pra n permitir valores maiores que 24 horas
                Log.d("testeinsert", "eu to aqui no metodo que vai inserir o medicamento")
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


            } else {
                Toast.makeText(
                    requireContext(),
                    "Erro ao cadastrar medicamento. Verifique o preenchimento dos dados.",
                    Toast.LENGTH_LONG
                ).show()

            }

        }

    }

    fun mudarVisibilidadeDasViewsRelacionadasADuracaoTratamento() {
        binding.containerButtons.visibility = View.INVISIBLE
        binding.tilMedicineTimeTreatment.hint = "Quantos dias?"
        binding.tilMedicineTimeTreatment.visibility = View.VISIBLE
    }


    override fun onDestroy() {
        super.onDestroy()

        Log.d("testeendfra", "o ondestroy desse fragment foi chamado")
    }




}