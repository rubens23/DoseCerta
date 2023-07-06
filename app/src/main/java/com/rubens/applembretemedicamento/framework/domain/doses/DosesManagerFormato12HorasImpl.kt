package com.rubens.applembretemedicamento.framework.domain.doses

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.appmedicamentos.data.repository.AddMedicineRepositoryImpl
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class DosesManagerFormato12HorasImpl @Inject constructor(
    private val repositoryAdicionarMedicamento: AddMedicineRepositoryImpl
): DosesManagerFormato12Horas {

    private var listaHorarioDoses = ArrayList<Doses>()




    override fun pegarTodasAsDosesParaOMedicamento(
        nomeMedicamento: String,
        horaPrimeiraDose: String,
        qntDosesPorDia: Int,
        totalDeDiasDeTratamento: Int,
        diaInicioTratamento: String
    ) {
        fazerTodasAsDoses(nomeMedicamento, horaPrimeiraDose, qntDosesPorDia, totalDeDiasDeTratamento, diaInicioTratamento)



    }

    fun fazerTodasAsDoses(
        nomeMedicamento: String,
        horaPrimeiraDose: String,
        qntDosesPorDia: Int,
        totalDeDiasDeTratamento: Int,
        diaInicioTratamento: String
    ) {
        val horaDose = horaPrimeiraDose.split(":")
        val hora = horaDose[0].trim().toInt()
        val minuto = horaDose[1].split(" ")[0].trim().toInt()
        val periodo = horaDose[1].split(" ")[1].trim()

        val format = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        val calendar = Calendar.getInstance()

        for (dia in 0 until totalDeDiasDeTratamento) {
            for (dose in 0 until qntDosesPorDia) {
                calendar.time = format.parse("$diaInicioTratamento $hora:$minuto $periodo")
                calendar.add(Calendar.DAY_OF_MONTH, dia)
                calendar.add(Calendar.HOUR_OF_DAY, dose * (24 / qntDosesPorDia))

                val horaDoseFormatada = format.format(calendar.time)

                adicionarNaListaDeDoses(Doses(nomeMedicamento = nomeMedicamento,
                horarioDose = horaDoseFormatada,
                jaTomouDose = false,
                intervaloEntreDoses = (24 / qntDosesPorDia).toDouble()))
            }
        }

        insertListaDeDosesInTable()
    }

    private fun adicionarNaListaDeDoses(dose: Doses) {
        listaHorarioDoses.add(dose)

    }

    private fun insertListaDeDosesInTable() {
        listaHorarioDoses.forEach {
                dose->
            Log.d("printateaqui2", "dose: ${dose.horarioDose}")



            insertDose(dose)
        }
    }

    private fun insertDose(doses: Doses){
        repositoryAdicionarMedicamento.insertDoses(doses)

    }


}