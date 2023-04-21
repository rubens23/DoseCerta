package com.rubens.applembretemedicamento.framework.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appmedicamentos.data.repository.AddMedicineRepositoryImpl
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ViewModelFragmentCadastrarNovoMedicamento @Inject constructor(
    private val repositoryAdicionarMedicamento: AddMedicineRepositoryImpl
): ViewModel(), FuncoesDeTempo, CalendarHelper {

    private lateinit var medicamento: MedicamentoTratamento
    private var listaHorarioDoses = ArrayList<Doses>()
    var insertResponse: MutableLiveData<Long> = MutableLiveData()

    var insertDosesResponse: MutableLiveData<Long> = MutableLiveData()



    fun dealWithDosageTime(
        medicamento: MedicamentoTratamento,
        nomeMedicamento: String,
        qntDoses: Int,
        horarioPrimeiraDose: String
    ) {

        this.medicamento = medicamento
        //todo aqui eu ja tenho que passar para a tabela doses
        val horaDigitada = horarioPrimeiraDose
        var hora = 0
        var minutos = "00"
        Log.d("testeminInicial", "eu passei aqui na redefinição dos minutos")
        if (horaDigitada[1].toString() == ":") {//se hora tiver 1 digito
            if (horaDigitada[0].toString().toInt() in 1..9) {
                hora = horaDigitada[0].toString().toInt()
                minutos = horaDigitada[2].toString() + horaDigitada[3].toString()

            }


        } else if (horaDigitada[2].toString() == ":") {//se hora tiver 2 digitos
            if (horaDigitada[0].toString().toInt() == 0) {
                if (horaDigitada[1].toString().toInt() == 0) {
                    //é meia noite
                    //hora recebe 0
                    hora = 0
                    //minutos recebe indice3 e 4 para int
                    minutos = horaDigitada[3].toString() + horaDigitada[4].toString()

                } else {
                    if (horaDigitada[1].toString().toInt() > 0) {
                        //hora recebe horaDigitada[1]
                        hora = horaDigitada[1].toString().toInt()
                        //minutos recebe  indice3 e 4 para int
                        minutos = horaDigitada[3].toString() + horaDigitada[4].toString()
                    }
                }


            } else {
                if (horaDigitada[0].toString().toInt() in 1..2) {
                    if ((horaDigitada[0].toString() + horaDigitada[1].toString()).toInt() in 10..23) {
                        //hora recebe indice 0 e 1
                        hora = (horaDigitada[0].toString() + horaDigitada[1].toString()).toInt()
                        //minutos recebe indice 3 e 4
                        minutos = horaDigitada[3].toString() + horaDigitada[4].toString()
                    }
                } else if (horaDigitada[0].toString().toInt() == 2 && horaDigitada[1].toString()
                        .toInt() == 4
                ) {
                    //hora recebe 0
                    hora = 0
                    //minutos recebe indice 3 e 4
                    minutos = horaDigitada[3].toString() + horaDigitada[4].toString()
                }
            }
        }


        getAllDosages(nomeMedicamento, hora, qntDoses, minutos, horaDigitada)

    }

    fun insertMedicamento(medicamento: MedicamentoTratamento){
        val insertResponseLong = repositoryAdicionarMedicamento.insertMedicamento(medicamento)

        insertResponse.postValue(insertResponseLong)
    }


    fun ligarAlarmeDoMedicamento(nomeMedicamento: String, ativado: Boolean){
        repositoryAdicionarMedicamento.ligarAlarmeDoMedicamento(nomeMedicamento, ativado)
    }






    private fun getAllDosages(
        nomeMed: String,
        horaIni: Int,
        qntDoses: Int,
        min: String,
        horaInicialDigitada: String
    ) {

        var hora = horaIni
        var minInicial = min
        val intervaloEntreDoses = 24 / qntDoses
        val intervalo = (24.toDouble() / qntDoses)
        for (i in 1..medicamento.num_doses_num_unico_horario) {
            Log.d("primeirofor", "primeiro for")
            val medicamentoDose = criarDose(hora, minInicial, nomeMed, intervalo)
            colocarDoseNaLista(medicamentoDose)
        }


        for (i in 1..qntDoses - 1) {
            Log.d("segundofor", "segundo for")

            if (hora + intervaloEntreDoses > 24) {
                val intervaloEntreDosesIsGreaterThanOne =
                    checkIfIntervaloEntreDosesIsGreaterThanOne(intervaloEntreDoses.toDouble())
                if (intervaloEntreDosesIsGreaterThanOne) {
                    hora = hora + intervaloEntreDoses - 24

                    for (i in 1..medicamento.num_doses_num_unico_horario) {
                        Log.d("terceirofor", "terceiro for")
                        val medicamentoDose = criarDose(hora, minInicial, nomeMed, intervalo)
                        colocarDoseNaLista(medicamentoDose)
                    }

                } else {
                    var minutosSoma = intervalo * 60

                    var time = LocalTime.of(hora, minInicial.toInt())
                    time = time.plusMinutes(minutosSoma.toLong())
                    hora = time.hour
                    minInicial = time.minute.toString()





                    for (i in 1..medicamento.num_doses_num_unico_horario) {
                        Log.d("quartofor", "quarto for")


                        val medicamentoDose = criarDose(time, nomeMed, intervalo)
                        colocarDoseNaLista(medicamentoDose)
                    }
                }


            } else if (hora + intervaloEntreDoses < 24) {
                hora = hora + intervaloEntreDoses
                val intervaloEntreDosesIsGreaterThanOne =
                    checkIfIntervaloEntreDosesIsGreaterThanOne(intervaloEntreDoses.toDouble())
                if (intervaloEntreDosesIsGreaterThanOne) {
                    Log.d(
                        "controlelistadoses",
                        "segunda dose: ${hora.toString() + ":" + min + "h"}"
                    )
                    for (i in 1..medicamento.num_doses_num_unico_horario) {
                        Log.d("quintofor", "quinto for")

                        val medicamentoDose = criarDose(hora, minInicial, nomeMed, intervalo)
                        colocarDoseNaLista(medicamentoDose)
                    }
                } else {
                    var minutosSoma = intervalo * 60
                    Log.d("entendendo", "${minutosSoma.toInt()}")

                    //todo dar um jeito de somar os minutosSoma na hora da dose

                    var time = LocalTime.of(hora, minInicial.toInt())
                    time = time.plusMinutes(minutosSoma.toLong())
                    hora = time.hour
                    minInicial = time.minute.toString()


                    for (i in 1..medicamento.num_doses_num_unico_horario) {
                        Log.d("sextofor", "sexto for")

                        val medicamentoDose = criarDose(time, nomeMed, intervalo)
                        colocarDoseNaLista(medicamentoDose)
                    }


                }


            } else if (hora + intervaloEntreDoses == 24) {
                hora = 0
                Log.d("controlelistadoses", "segunda dose: ${hora.toString() + ":" + min + "h"}")
                val intervaloEntreDosesIsGreaterThanOne =
                    checkIfIntervaloEntreDosesIsGreaterThanOne(intervaloEntreDoses.toDouble())
                if (intervaloEntreDosesIsGreaterThanOne) {
                    for (i in 1..medicamento.num_doses_num_unico_horario) {
                        Log.d("setimofor", "setimo for")

                        val medicamentoDose = criarDose(hora, minInicial, nomeMed, intervalo)
                        colocarDoseNaLista(medicamentoDose)
                    }
                } else {
                    var minutosSoma = intervalo * 60
                    Log.d("entendendo", "${minutosSoma.toInt()}")

                    //todo dar um jeito de somar os minutosSoma na hora da dose

                    var time = LocalTime.of(hora, minInicial.toInt())
                    time = time.plusMinutes(minutosSoma.toLong())
                    hora = time.hour
                    minInicial = time.minute.toString()



                    for (i in 1..medicamento.num_doses_num_unico_horario) {
                        Log.d("oitavofor", "oitavo for")

                        val medicamentoDose = criarDose(time, nomeMed, intervalo)
                        colocarDoseNaLista(medicamentoDose)
                    }
                }
            }

        }

        insertDosesInTable()



    }

    private fun criarDose(hora: Int, minInicial: String, nomeMed: String, intervaloEntreDoses: Double): Doses {
        Log.d("criardose2", "to no segundo metodo de criar dose")

        return Doses(
            nomeMedicamento = nomeMed,
            horarioDose = hora.toString() + ":" + minInicial,
            jaTomouDose = false,
            intervaloEntreDoses = intervaloEntreDoses,
            qntDosesPorHorario = medicamento.num_doses_num_unico_horario
        )
        //todo ver se a lista esta populada
    }

    private fun checkIfIntervaloEntreDosesIsGreaterThanOne(intervaloEntreDoses: Double): Boolean {


        if (intervaloEntreDoses < 1) {
            return false
        }

        return true
    }

    private fun insertDosesInTable() {
        listaHorarioDoses.forEach {
                dose->
            insertDose(dose)
        }
    }




    private fun colocarDoseNaLista(medicamentoDose: Doses) {
        Log.d("addlista", "to no metodo de adicionar na lista")

        listaHorarioDoses.add(medicamentoDose)
    }

    private fun criarDose(hora: LocalTime?, nomeMed: String, intervaloEntreDoses: Double): Doses {
        Log.d("criardose1", "to no primeiro metodo de criar dose")
        return Doses(
            nomeMedicamento = nomeMed,
            horarioDose = hora.toString(),
            jaTomouDose = false,
            intervaloEntreDoses = intervaloEntreDoses,
            qntDosesPorHorario = medicamento.num_doses_num_unico_horario
        )

    }



    private fun insertDose(doses: Doses){
        val insertDosesResponseLong = repositoryAdicionarMedicamento.insertDoses(doses)

        insertDosesResponse.postValue(insertDosesResponseLong)
    }
}