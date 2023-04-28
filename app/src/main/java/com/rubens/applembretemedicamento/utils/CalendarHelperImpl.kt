package com.rubens.applembretemedicamento.utils

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class CalendarHelperImpl {

    @Throws(ParseException::class)
    fun convertStringToDate(dateHour: String?): Date? {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        format.isLenient = false // não permitir datas inválidas
        if (dateHour != null) {
            if (!dateHour.matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}".toRegex())) {
                throw ParseException("Data inválida", 0)
            }
        }
        return format.parse(dateHour)
    }

    fun verificarSeDataJaPassou(dataTerminoTratamento: String): Boolean{
        val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")


        val date1Str = pegarDataAtual()
        val date2Str = dataTerminoTratamento

        val date1 = LocalDate.parse(date1Str, dateFormat)
        val date2 = LocalDate.parse(date2Str, dateFormat)

        return date1 > date2



    }

    fun pegarDataAtual(): String{
        val formatarData = SimpleDateFormat("dd/MM/yyyy")
        val data = Date()
        val dataFormatada = formatarData.format(data)

        return dataFormatada
    }

    fun somarUmDiaNumaData(data: String): String{
        var data = data
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd/MM/yyyy")


        val dt = formatter.parse(data)
        calendar.time = dt
        calendar.add(Calendar.DATE, 1)
        data = formatter.format(calendar.time)

        /*
        ele nao ta salvando todas as doses...
         */



        return data
    }



    fun calculateHoursDifference(d2: Date): Long {
        val cal = Calendar.getInstance()
        val d1 = cal.time //current time
        val diff = d2.time - d1.time
        val diffHours = diff / (60 * 60 * 1000)
        val diffMinutes = diff / (60 * 1000) % 60
        val horasEmSegundos = diffHours * 60 * 60
        val minutosEmHoras = diffMinutes * 60
        return horasEmSegundos + minutosEmHoras
    }
}