package com.rubens.applembretemedicamento.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

interface FuncoesDeTempo {

    fun pegarDataDeTermino(data: String, diasAteTermino: Int, defaultDeviceDateFormat: String): String


    fun pegarHoraAtual(is24HourFormat: Boolean): String

    fun horasParaMillisegundos(hour:String): Long

    fun minutosParaMillisegundos(min: String): Long
}