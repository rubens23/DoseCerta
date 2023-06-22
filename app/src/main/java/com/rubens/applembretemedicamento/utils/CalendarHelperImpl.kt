package com.rubens.applembretemedicamento.utils

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CalendarHelperImpl: CalendarHelper {

    override fun convertStringToDate(dateHour: String?): Date? {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        try {
            return format.parse(dateHour)
        } catch (pe: ParseException) {
            Log.e("erroparsestringdata", pe.message!!)
        }
        return null
    }

    override fun convertStringToDateSemSegundos(dateHour: String?): Date? {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm")
        try {
            return format.parse(dateHour)
        } catch (pe: ParseException) {
            Log.e("erroparsestringdata", pe.message!!)
        }
        return null
    }

    override fun verificarSeDataJaPassou(dataTerminoTratamento: String): Boolean{
        val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")


        val date1Str = pegarDataAtual()
        val date2Str = dataTerminoTratamento

        val date1 = LocalDate.parse(date1Str, dateFormat)
        val date2 = LocalDate.parse(date2Str, dateFormat)

        return date1 > date2



    }

    override fun verificarSeDataHoraJaPassou(dataHoraTerminoTratamento: String): Boolean {
        val dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

        val dateTime1Str = pegarDataHoraAtual()
        val dateTime2Str = dataHoraTerminoTratamento

        val dateTime1 = LocalDateTime.parse(dateTime1Str, dateTimeFormat)
        val dateTime2 = LocalDateTime.parse(dateTime2Str, dateTimeFormat)

        return dateTime1 > dateTime2
    }

    override fun pegarDataAtual(): String{
        val formatarData = SimpleDateFormat("dd/MM/yyyy")
        val data = Date()
        val dataFormatada = formatarData.format(data)

        return dataFormatada
    }

    override fun pegarDataHoraAtual(): String {
        val dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val currentDateTime = LocalDateTime.now()

        return currentDateTime.format(dateTimeFormat)
    }

    override fun somarUmDiaNumaData(data: String): String{
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

    override fun subtrairUmDiaNumaData(data: String): String{
        var data = data
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd/MM/yyyy")


        val dt = formatter.parse(data)
        calendar.time = dt
        calendar.add(Calendar.DATE, -1)
        data = formatter.format(calendar.time)

        /*
        ele nao ta salvando todas as doses...
         */



        return data
    }



    override fun calculateHoursDifference(d2: Date): Long {
        val cal = Calendar.getInstance()
        val d1 = cal.time //current time
        val diff = d2.time - d1.time
        val diffHours = diff / (60 * 60 * 1000)
        val diffMinutes = diff / (60 * 1000) % 60
        Log.d(
            "testediferencahoras",
            "tempo que falta ate 15:00 $diffHours:$diffMinutes"
        )

        //converter horas e minutos para segundos somar e retornar
        val horasEmSegundos = diffHours * 60 * 60
        val minutosEmHoras = diffMinutes * 60
        return horasEmSegundos + minutosEmHoras
    }


}