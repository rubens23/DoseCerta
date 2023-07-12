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
import java.util.Calendar
import java.util.Date
import java.util.Locale
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



    override fun convertStringToDateSemSegundos(dateHour: String?, is24HourFormat: Boolean, defaultDeviceDateFormat: String): Date? {
        if(is24HourFormat){
            var format: SimpleDateFormat
            if(defaultDeviceDateFormat == "dd/MM/yyyy"){
                format = SimpleDateFormat("dd/MM/yyyy HH:mm")

            }else{
                format = SimpleDateFormat("MM/dd/yyyy HH:mm")

            }
            try {
                return format.parse(dateHour)
            } catch (pe: ParseException) {
                Log.e("erroparsestringdata", pe.message!!)
            }
            return null
        }else{
            var format: SimpleDateFormat
            if(defaultDeviceDateFormat == "dd/MM/yyyy"){
                format = SimpleDateFormat("dd/MM/yyyy h:mm a")

            }else{
                format = SimpleDateFormat("MM/dd/yyyy h:mm a")

            }
            try {
                return format.parse(dateHour)
            } catch (pe: ParseException) {
                Log.e("erroparsestringdata", pe.message!!)
            }
            return null


        }

    }


    override fun verificarSeDataJaPassou(dataTerminoTratamento: String, defaultDeviceDateFormat: String): Boolean{
        val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")


        val date1Str = pegarDataAtual(defaultDeviceDateFormat)
        val date2Str = dataTerminoTratamento

        val date1 = LocalDate.parse(date1Str, dateFormat)
        val date2 = LocalDate.parse(date2Str, dateFormat)


        return date1 > date2



    }

    override fun verificarSeDataHoraJaPassou(horarioPrimeiraDoseTratamento: String, is24HourFormat: Boolean, defaultDeviceDateFormat: String): Boolean {
        Log.d("testingsetalarm3", "eu to aqui no metodo verificarSeDataHoraJaPassou, com a hora: $horarioPrimeiraDoseTratamento")

               //todo java.time.format.DateTimeParseException: Text '07/07/2023 3:00' could not be parsed at index 11




            var dateTimeFormat: DateTimeFormatter

        var dateTime1Str: String

        if(is24HourFormat){
            dateTime1Str = pegarDataHoraAtual(DateFormat.is24HourFormat(context), defaultDeviceDateFormat)
            if (defaultDeviceDateFormat == "dd/MM/yyyy"){
                dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

            }else{
                dateTimeFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")

            }


        }else{
            dateTime1Str = pegarDataHoraAtualIn12HoursFormat(defaultDeviceDateFormat)
            if (defaultDeviceDateFormat == "dd/MM/yyyy"){
                dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")

            }else{
                dateTimeFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")


            }



        }

        val dateTime2Str = horarioPrimeiraDoseTratamento

        val dateTime1 = LocalDateTime.parse(dateTime1Str, dateTimeFormat)
        var dateTime2: LocalDateTime
        try{
            dateTime2 = LocalDateTime.parse(dateTime2Str, dateTimeFormat)
        }catch (e: Exception){
            dateTime2 = LocalDateTime.parse(dateTime2Str, DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm"))

        }



        return dateTime1 > dateTime2
    }

    private fun pegarDataHoraAtualIn12HoursFormat(defaultDeviceDateFormat: String): String {
        val calendar = Calendar.getInstance()
        var dateFormat: SimpleDateFormat
        if(defaultDeviceDateFormat == "dd/MM/yyyy"){
            dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())

        }else{
            dateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault())

        }
        return dateFormat.format(calendar.time)

    }

    override fun pegarDataAtual(deviceDefaultDateFormat: String): String{
        var formatarData: SimpleDateFormat
        if(deviceDefaultDateFormat == "dd/MM/yyyy"){
            formatarData = SimpleDateFormat("dd/MM/yyyy")

        }else{
            formatarData = SimpleDateFormat("MM/dd/yyyy")

        }

        val data = Date()
        val dataFormatada = formatarData.format(data)

        return dataFormatada
    }

    override fun pegarDataAtualConsiderandoFormatacaoDaDataDoDevice(defaultDeviceDateFormat: String): String{
        var formatarData: SimpleDateFormat

        if (defaultDeviceDateFormat == "dd/MM/yyyy"){
            formatarData = SimpleDateFormat("dd/MM/yyyy")
        }else{
            formatarData = SimpleDateFormat("MM/dd/yyyy")
        }
        val data = Date()
        val dataFormatada = formatarData.format(data)

        return dataFormatada
    }

    override fun pegarDataHoraAtual(is24HourFormat: Boolean, defaultDeviceDateFormat: String): String {
        var dateTimeFormat: DateTimeFormatter

        if(is24HourFormat){
            if(defaultDeviceDateFormat == "dd/MM/yyyy"){
                dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

            }else{
                dateTimeFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")

            }
            val currentDateTime = LocalDateTime.now()

            return currentDateTime.format(dateTimeFormat)
        }else{
            if(defaultDeviceDateFormat == "dd/MM/yyyy"){
                dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")

            }else{
                dateTimeFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")

            }
            val currentDateTime = LocalDateTime.now()

            return currentDateTime.format(dateTimeFormat)
        }

    }

    override fun pegarFormatoDeDataPadraoDoDispositivoDoUsuario(context: Context): String{
        val currentDate = Date()

        val dateFormat = DateFormat.getDateFormatOrder(context)
        val formattedDate: String

        if (dateFormat[0] == 'd') {
            // Day comes before month
            formattedDate = "dd/MM/yyyy"
        } else {
            // Month comes before day
            formattedDate = "MM/dd/yyyy"
        }

        return formattedDate



    }

    fun usaFormatoAMPM(): Boolean{
        val symbols = DateFormatSymbols(Locale.getDefault())
        val amPmPattern = symbols.amPmStrings[0]

        Log.d("pmam", "$symbols")




        return amPmPattern.isNotBlank()
    }

    override fun somarUmDiaNumaData(data: String, defaultDeviceDateFormat: String): String{
        var data = data
        val calendar = Calendar.getInstance()

        var formatter: SimpleDateFormat
        if (defaultDeviceDateFormat == "dd/MM/yyyy"){
            formatter = SimpleDateFormat("dd/MM/yyyy")
        }else{
            formatter = SimpleDateFormat("MM/dd/yyyy")
        }


        val dt = formatter.parse(data)
        calendar.time = dt
        calendar.add(Calendar.DATE, 1)
        data = formatter.format(calendar.time)

        /*
        ele nao ta salvando todas as doses...
         */



        return data
    }

    override fun subtrairUmDiaNumaData(data: String, defaultDeviceDateFormat: String): String{
        var data = data
        val calendar = Calendar.getInstance()
        var formatter: SimpleDateFormat
        if (defaultDeviceDateFormat == "dd/MM/yyyy"){
            formatter = SimpleDateFormat("dd/MM/yyyy")
        }else{
            formatter = SimpleDateFormat("MM/dd/yyyy")
        }


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