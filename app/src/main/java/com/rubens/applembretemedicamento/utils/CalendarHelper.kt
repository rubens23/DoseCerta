package com.rubens.applembretemedicamento.utils

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

interface CalendarHelper {

    fun convertStringToDate(dateHour: String?): Date? {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        try {
            return format.parse(dateHour)
        } catch (pe: ParseException) {
            Log.e("erroparsestringdata", pe.message!!)
        }
        return null
    }

    fun calculateHoursDifference(d2: Date): Long {
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