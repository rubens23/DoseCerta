package com.rubens.applembretemedicamento.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

interface FuncoesDeTempo {

    fun pegarDataDeTermino(data: String, diasAteTermino: Int): String


    fun pegarHoraAtual(): String

    fun horasParaMillisegundos(hour:String): Long

    fun minutosParaMillisegundos(min: String): Long
}