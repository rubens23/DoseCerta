package com.rubens.applembretemedicamento.utils

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.format.DateTimeParseException
import java.util.*

class CalendarHelperImplTest{

    private lateinit var calendarHelper: CalendarHelperImpl

    @Before
    fun setUp(){
        calendarHelper = CalendarHelperImpl()
    }

    @Test
    fun shouldConvertStringToDateWhenValidStringIsProvided(){
        val dateHourString = "27/04/2023 11:00:00"
        val expectedDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dateHourString)
        val result = calendarHelper.convertStringToDate(dateHourString)
        assertThat(result, notNullValue())
        assertThat(result, instanceOf(Date::class.java))
        assertThat(result, equalTo(expectedDate))
    }

    @Test
    fun convertStringToDate_shouldThrowParseExceptionWhenStringNotValidIsProvided(){
        val dateHourString = "wrong date"
        assertThrows(ParseException::class.java){
            calendarHelper.convertStringToDate(dateHourString)
        }
    }

    @Test
    fun convertStringToDate_shouldThrowParseExceptionWhenStringDateNotValidIsProvided(){
        val dateHourString = "1/04/23 11:04:00"
        assertThrows(ParseException::class.java){
            calendarHelper.convertStringToDate(dateHourString)
        }
    }

    @Test
    fun verificarSeDataJaPassou_deveRetornarTrueQuandoDataInputadaJaTiverPassado(){
        //coloque nesse input uma data que ja passou
        val dataASerVerificada = "01/04/2023"

        val result = calendarHelper.verificarSeDataJaPassou(dataASerVerificada)
        assertThat(result, `is`(true))
    }

    @Test
    fun verificarSeDataJaPassou_deveRetornarFalseQuandoDataInputadaAindaNaoTiverPassado(){
        //coloque nesse input uma data que ainda não passou
        val dataASerVerificada = "29/04/2023"


        val result = calendarHelper.verificarSeDataJaPassou(dataASerVerificada)
        assertThat(result, `is`(false))
    }

    @Test
    fun verificarSeDataJaPassou_deveRetornarFalseQuandoDataInputadaForIgualADataDeHoje(){
        //passe a data de hoje
        val dataASerVerificada = "27/04/2023"


        val result = calendarHelper.verificarSeDataJaPassou(dataASerVerificada)
        assertThat(result, `is`(false))
    }

    @Test
    fun verificarSeDataJaPassou_deveRetornarDateTimeParseExceptionQuandoDataInputadaNaoForValida(){
        val dataASerVerificada = ""



        assertThrows(DateTimeParseException::class.java){
            calendarHelper.verificarSeDataJaPassou(dataASerVerificada)
        }
    }


    @Test
    fun `test pegarDataAtual`(){
        val expectedFormat = SimpleDateFormat("dd/MM/yyyy")
        val currentDate = Date()
        val expectedDate = expectedFormat.format(currentDate)

        val resultDate = calendarHelper.pegarDataAtual()

        assertThat(resultDate, `is`(expectedDate))
    }

    @Test
    fun somarUmDiaNumaData_somarUmDiaAPartirDeUmaDataInputada(){
        val dataInput = "28/04/2023"
        val expectedResult = "29/04/2023"

        val resultDate = calendarHelper.somarUmDiaNumaData(dataInput)

        assertThat(resultDate, `is`(expectedResult))


    }

    @Test
    fun somarUmDiaNumaData_SeStringEstiverVaziaRetornaParseException(){
        val dataInput = ""


        assertThrows(ParseException::class.java){
            calendarHelper.somarUmDiaNumaData(dataInput)
        }

    }

    @Test
    fun somarUmDiaNumaData_SeStringNaoEstiverNoFormatoDefinidoNoMetodoSomaMesmoAssim(){
        val dataInput = "1/4/2023"

        val expectedResult = "02/04/2023"

        val resultDate = calendarHelper.somarUmDiaNumaData(dataInput)

        assertThat(resultDate, `is`(expectedResult))

    }

    @Test
    fun somarUmDiaNumaData_SeAnoNaStringEstiverAbreviadoRetornaStringComAnoInvalido(){
        val dataInput = "1/4/23"

        val expectedResult = "02/04/0023"

        val resultDate = calendarHelper.somarUmDiaNumaData(dataInput)

        assertThat(resultDate, `is`(expectedResult))

    }


}