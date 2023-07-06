package com.rubens.applembretemedicamento.utils

import java.util.Date

interface CalendarHelper {
    fun calculateHoursDifference(d2: Date): Long
    fun subtrairUmDiaNumaData(data: String): String
    fun somarUmDiaNumaData(data: String): String
    fun pegarDataHoraAtual(is24HourFormat: Boolean): String
    fun pegarDataAtual(): String
    fun verificarSeDataHoraJaPassou(horarioPrimeiraDoseTratamento: String, is24HourFormat: Boolean): Boolean
    fun verificarSeDataJaPassou(dataTerminoTratamento: String): Boolean
    fun convertStringToDate(dateHour: String?): Date?
    fun convertStringToDateSemSegundos(dateHour: String?, is24HourFormat: Boolean): Date?
}