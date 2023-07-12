package com.rubens.applembretemedicamento.utils

import android.content.Context
import java.util.Date

interface CalendarHelper {
    fun calculateHoursDifference(d2: Date): Long
    fun subtrairUmDiaNumaData(data: String, defaultDeviceDateFormat: String): String
    fun somarUmDiaNumaData(data: String, defaultDeviceDateFormat: String): String
    fun pegarDataHoraAtual(is24HourFormat: Boolean, defaultDeviceDateFormat: String): String
    fun pegarDataAtual(deviceDefaultDateFormat: String): String
    fun verificarSeDataHoraJaPassou(horarioPrimeiraDoseTratamento: String, is24HourFormat: Boolean, defaultDeviceDateFormat: String): Boolean
    fun verificarSeDataJaPassou(dataTerminoTratamento: String, defaultDeviceDateFormat: String): Boolean
    fun convertStringToDate(dateHour: String?): Date?
    fun convertStringToDateSemSegundos(dateHour: String?, is24HourFormat: Boolean, defaultDeviceDateFormat: String): Date?
    fun pegarFormatoDeDataPadraoDoDispositivoDoUsuario(context: Context): String
    fun pegarDataAtualConsiderandoFormatacaoDaDataDoDevice(defaultDeviceDateFormat: String): String
}