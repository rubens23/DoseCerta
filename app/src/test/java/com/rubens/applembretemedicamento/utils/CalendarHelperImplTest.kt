package com.rubens.applembretemedicamento.utils

import android.app.Application
import android.content.Context
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.format.DateTimeParseException
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class CalendarHelperImplTest{

    private lateinit var calendarHelper: CalendarHelperImpl
    private val defaultDateFormat = "dd/MM/yyyy"

    @Mock
    private lateinit var mockContext: Context


    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)
        calendarHelper = CalendarHelperImpl(mockContext)
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
    fun convertStringToDate_shouldThrowExceptionWhenStringNotValidIsProvided(){
        val dateHourString = "wrong date"
        assertThrows(Exception::class.java){
            calendarHelper.convertStringToDate(dateHourString)
        }
    }



    @Test
    fun convertStringToDate_successfullyParsesOneDigitDay(){
        val dateHourString = "1/04/23 11:04:00"

        val result = calendarHelper.convertStringToDate(dateHourString)

        assertThat(result, instanceOf(Date::class.java))

    }

    @Test
    fun convertStringToDate_dateWithDayOfOneDigitToStringReturnsValidDate(){
        val dateHourString = "1/04/23 11:04:00"

        val result = calendarHelper.convertStringToDate(dateHourString)
        val expectedResult = "Thu Apr 01 11:04:00 BRT 23"

        assertThat(result.toString(), `is`(expectedResult))


    }

    @Test
    fun verificarSeDataJaPassou_deveRetornarTrueQuandoDataInputadaJaTiverPassado(){
        //coloque nesse input uma data que ja passou
        val dataASerVerificada = "01/04/2023"

        val result = calendarHelper.verificarSeDataJaPassou(dataASerVerificada)
        assertThat(result, `is`(true))
    }

    @Test
    fun verificarSeDataJaPassou_deveRetornarFalseQuandoDataInputadaNaoTiverPassado(){
        val dataASerVerificada = "01/07/2030"

        val result = calendarHelper.verificarSeDataJaPassou(dataASerVerificada)
        assertThat(result, `is`(false))
    }



    @Test
    fun verificarSeDataJaPassou_deveRetornarFalseQuandoDataInputadaForIgualADataDeHoje(){
        val expectedDateFormat = "dd/MM/yyyy"
        val dataASerVerificada = SimpleDateFormat(expectedDateFormat).format(Date())


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

        val resultDate = calendarHelper.somarUmDiaNumaData(dataInput, defaultDateFormat)

        assertThat(resultDate, `is`(expectedResult))


    }

    @Test
    fun somarUmDiaNumaData_SeStringEstiverVaziaRetornaParseException(){
        val dataInput = ""


        assertThrows(ParseException::class.java){
            calendarHelper.somarUmDiaNumaData(dataInput, defaultDateFormat)
        }

    }

    @Test
    fun somarUmDiaNumaData_SeStringNaoEstiverNoFormatoDefinidoNoMetodoSomaMesmoAssim(){
        val dataInput = "1/4/2023"

        val expectedResult = "02/04/2023"

        val resultDate = calendarHelper.somarUmDiaNumaData(dataInput, defaultDateFormat)

        assertThat(resultDate, `is`(expectedResult))

    }

    @Test
    fun somarUmDiaNumaData_SeAnoNaStringEstiverAbreviadoRetornaStringComAnoInvalido(){
        val dataInput = "1/4/23"

        val expectedResult = "02/04/0023"

        val resultDate = calendarHelper.somarUmDiaNumaData(dataInput, defaultDateFormat)

        assertThat(resultDate, `is`(expectedResult))

    }


}