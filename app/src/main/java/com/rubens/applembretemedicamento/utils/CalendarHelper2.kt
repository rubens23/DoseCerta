package com.rubens.applembretemedicamento.utils

import java.text.ParseException
import java.util.Date

interface CalendarHelper2 {
    fun pegarDataHoraAtual(): String
    fun formatarDataHoraComSegundos(dataString: String): String
    fun formatarDataHoraSemSegundos(dataString: String): String
    fun calculateHoursDifference(d2: Date): Long
    fun somarUmDiaNumaData(data: String): String
    fun pegarDataAtual(): String
    fun verificarSeDataJaPassou(dataTerminoTratamento: String): Boolean
    @Throws(ParseException::class)
    fun convertStringToDate(dateHour: String?): Date?
}