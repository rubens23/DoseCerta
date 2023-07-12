package com.rubens.applembretemedicamento.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

class FuncoesDeTempoImpl: FuncoesDeTempo {

    override fun pegarDataDeTermino(data: String, diasAteTermino: Int, defaultDeviceDateFormat: String): String {
        val calendar = Calendar.getInstance()
        var sdf: SimpleDateFormat
        if(defaultDeviceDateFormat == "dd/MM/yyyy"){
            sdf = SimpleDateFormat("dd/MM/yyyy")

        }else{
            sdf = SimpleDateFormat("MM/dd/yyyy")

        }
        calendar.time = sdf.parse(data)

        calendar.add(Calendar.DATE, diasAteTermino)

        val resultDate = Date(calendar.timeInMillis)
        return sdf.format(resultDate)
    }


    override fun pegarHoraAtual(is24HourFormat: Boolean): String{
        var formatarHora: SimpleDateFormat

        if(is24HourFormat){
            formatarHora = SimpleDateFormat("HH:mm")

        }else{
            formatarHora = SimpleDateFormat("h:mm a")

        }
        val hora = Calendar.getInstance().time

        val horaFormatada = formatarHora.format(hora)

        return horaFormatada
    }

    override fun horasParaMillisegundos(hour:String): Long{
        //todo eu preciso de uma função que converta double para milliseconds
        //todo o meu double 0.5, o 0 representa as horas e o 5 representa os minutos. Eu posso separar esses dois, converter os dois para segundos, somar os segundos e depois converter para millisegundos
        val milliseconds = TimeUnit.SECONDS.toMillis(TimeUnit.HOURS.toSeconds(hour.toLong()))
        return milliseconds
    }

    override fun minutosParaMillisegundos(min: String): Long{
        val milliseconds = TimeUnit.SECONDS.toMillis(TimeUnit.MINUTES.toSeconds(min.toLong()))
        return milliseconds

    }
}