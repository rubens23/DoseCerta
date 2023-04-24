package com.rubens.applembretemedicamento.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

interface FuncoesDeTempo {

    fun pegarDataDeTermino(data: String, diasAteTermino: Int): String {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        calendar.time = sdf.parse(data)

        calendar.add(Calendar.DATE, diasAteTermino)

        val resultDate = Date(calendar.timeInMillis)
        return sdf.format(resultDate)
    }


    fun pegarHoraAtual(): String{
        val formatarHora = SimpleDateFormat("HH:mm:ss")
        val hora = Calendar.getInstance().time

        val horaFormatada = formatarHora.format(hora)

        return horaFormatada
    }

    fun horasParaMillisegundos(hour:String): Long{
        //todo eu preciso de uma função que converta double para milliseconds
        //todo o meu double 0.5, o 0 representa as horas e o 5 representa os minutos. Eu posso separar esses dois, converter os dois para segundos, somar os segundos e depois converter para millisegundos
        val milliseconds = TimeUnit.SECONDS.toMillis(TimeUnit.HOURS.toSeconds(hour.toLong()))
        return milliseconds
    }

    fun minutosParaMillisegundos(min: String): Long{
        val milliseconds = TimeUnit.SECONDS.toMillis(TimeUnit.MINUTES.toSeconds(min.toLong()))
        return milliseconds

    }
}