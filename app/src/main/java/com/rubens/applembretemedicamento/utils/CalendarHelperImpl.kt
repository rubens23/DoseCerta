package com.rubens.applembretemedicamento.utils

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import java.text.DateFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject


class CalendarHelperImpl @Inject constructor(
    private val context: Context
): CalendarHelper {

    override fun convertStringToDate(dateHour: String?): Date? {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        try {
            return format.parse(dateHour)
        } catch (pe: Exception) {
            Log.e("erroparsestringdata", pe.message!!)
        }
        return null
    }

    override fun convertStringToDateSemSegundos(dateHour: String?, is24HourFormat: Boolean): Date? {
        if(is24HourFormat){
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm")
            try {
                return format.parse(dateHour)
            } catch (pe: ParseException) {
                Log.e("erroparsestringdata", pe.message!!)
            }
            return null
        }else{
            val format = SimpleDateFormat("dd/MM/yyyy h:mm a")
            try {
                return format.parse(dateHour)
            } catch (pe: ParseException) {
                Log.e("erroparsestringdata", pe.message!!)
            }
            return null


        }

    }

    override fun verificarSeDataJaPassou(dataTerminoTratamento: String): Boolean{
        val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")


        val date1Str = pegarDataAtual()
        val date2Str = dataTerminoTratamento

        val date1 = LocalDate.parse(date1Str, dateFormat)
        val date2 = LocalDate.parse(date2Str, dateFormat)


        return date1 > date2



    }

    override fun verificarSeDataHoraJaPassou(horarioPrimeiraDoseTratamento: String, is24HourFormat: Boolean): Boolean {
        Log.d("testingsetalarm3", "eu to aqui no metodo verificarSeDataHoraJaPassou, com a hora: $horarioPrimeiraDoseTratamento")



        var dateTimeFormat: DateTimeFormatter

        var dateTime1Str: String

        if(is24HourFormat){
            dateTime1Str = pegarDataHoraAtual(DateFormat.is24HourFormat(context))
            dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

            Log.d("pmam2", "formato de 24 $dateTime1Str")

        }else{
            dateTime1Str = pegarDataHoraAtualIn12HoursFormat() //03/07/2023 14:13
            dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")

            Log.d("pmam2", "formato de 12 $dateTime1Str")


        }

        val dateTime2Str = horarioPrimeiraDoseTratamento //03/07/2023 05:30

        val dateTime1 = LocalDateTime.parse(dateTime1Str, dateTimeFormat)
        val dateTime2 = LocalDateTime.parse(dateTime2Str, dateTimeFormat)

        Log.d("pmam2", "dateTime1 = $dateTime1 dateTime2 = $dateTime2  dateTime1 é maior que dateTime2? ${dateTime1 > dateTime2}")



        Log.d("pmam2", "dateTime1 = $dateTime1 dateTime2 = $dateTime2  dateTime1 é maior que dateTime2? ${dateTime1 > dateTime2}")


        return dateTime1 > dateTime2
    }

    private fun pegarDataHoraAtualIn12HoursFormat(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)

    }

    override fun pegarDataAtual(): String{
        val formatarData = SimpleDateFormat("dd/MM/yyyy")
        val data = Date()
        val dataFormatada = formatarData.format(data)

        return dataFormatada
    }

    override fun pegarDataHoraAtual(is24HourFormat: Boolean): String {
        if(is24HourFormat){
            val dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            val currentDateTime = LocalDateTime.now()

            return currentDateTime.format(dateTimeFormat)
        }else{
            val dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")
            val currentDateTime = LocalDateTime.now()

            return currentDateTime.format(dateTimeFormat)
        }

    }

    fun usaFormatoAMPM(): Boolean{
        val symbols = DateFormatSymbols(Locale.getDefault())
        val amPmPattern = symbols.amPmStrings[0]

        Log.d("pmam", "$symbols")




        return amPmPattern.isNotBlank()
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