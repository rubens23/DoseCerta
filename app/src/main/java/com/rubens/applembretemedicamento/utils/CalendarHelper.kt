package com.rubens.applembretemedicamento.utils

import java.util.Date

interface CalendarHelper {
    fun calculateHoursDifference(d2: Date): Long
    fun subtrairUmDiaNumaData(data: String): String
    fun somarUmDiaNumaData(data: String): String
    fun pegarDataHoraAtual(): String
    fun pegarDataAtual(): String
    fun verificarSeDataHoraJaPassou(dataHoraTerminoTratamento: String): Boolean
    fun verificarSeDataJaPassou(dataTerminoTratamento: String): Boolean
    fun convertStringToDate(dateHour: String?): Date?
    fun convertStringToDateSemSegundos(dateHour: String?): Date?
}