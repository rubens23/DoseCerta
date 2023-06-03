package com.rubens.applembretemedicamento.framework.domain.doses
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.appmedicamentos.data.repository.AddMedicineRepositoryImpl
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
/**
 * input
 *
 * nomeMedicamento: omeprazol,
 * hora: 10,
 * qntDoses: 1,
 * minutos: 30,
 * medicamento: MedicamentoTratamento(nomeMedicamento=omeprazol, horaPrimeiraDose=10:30, qntDoses=1, idMedicamento=0, alarmeAtivado=false, num_doses_num_unico_horario=2, totalDiasTratamento=2, diasRestantesDeTratamento=2, tratamentoFinalizado=false, dataInicioTratamento=02/06/2023, dataTerminoTratamento=04/06/2023, stringDataStore=toast_already_shown_omeprazol, alarmeTocando=false, colunaTeste=)

 */

class DosesManager: DosesManagerInterface {
    lateinit var medicamento: MedicamentoTratamento
    private var diaAtual = ""
    private var maiorHoraAteAgora = 0
    private var intervaloEntreDosesMaiorQueUmaHora = true
    private var dosesPorTomada = 1
    private var temMaisDeUmaDosePorTomada = false
    private var qntDosesRemanescentes = 0
    private var qntDosesDia = 0
    private var listaHorarioDoses = ArrayList<Doses>()
    private var dosesTomadasNaHoraAtual = 0
    private var qntTotalDeDoses = 0
    override var insertDosesResponse: MutableLiveData<Long> = MutableLiveData()
    private lateinit var repositoryAdicionarMedicamento: AddMedicineRepositoryImpl







    /*


   // essa parte é usada para testar o cadastro de medicamentos e ver as horas das doses

    init {


        pegarTodasAsDosesParaONovoMedicamento("dipirona",
            20,
            1,
            "20")

    }

     */











    private fun definirValorInicialDeDosesPorTomada(medicamento: MedicamentoTratamento) {
        dosesPorTomada = medicamento.num_doses_num_unico_horario


    }

    private fun definirQuantidadeDeDosesPorDia(qntDoses: Int) {
        qntDosesDia = qntDoses

    }

    private fun definirValorInicialDeDosesRemanescentes(qntDoses: Int) {
        qntDosesRemanescentes = qntDoses

    }

    private fun checkIfMedicineHasMoreThanOneDosagePerTake(medicamento: MedicamentoTratamento) {

        if(medicamento.num_doses_num_unico_horario > 1){
            temMaisDeUmaDosePorTomada = true
        }

    }

    private fun clearListaHorarioDoses() {
        listaHorarioDoses.clear()

    }

    private fun initializeMedicamento(medicine: MedicamentoTratamento) {
        this.medicamento = medicine

    }

    private fun initializeMedicamentoTeste(medicine: MedicamentoTratamento) {
        this.medicamento = medicine

    }



    override fun gerenciarHorariosDosagem(
        medicamento: MedicamentoTratamento,
        nomeMedicamento: String,
        qntDoses: Int,
        horarioPrimeiraDose: String,
        repositoryAdicionarMedicamento: AddMedicineRepositoryImpl
    ) {



        initRepositoryAddMedicine(repositoryAdicionarMedicamento)

        definirValorInicialDeDosesRemanescentes(qntDoses)

        definirQuantidadeDeDosesPorDia(qntDoses)

        definirValorInicialDeDosesPorTomada(medicamento)


        checkIfMedicineHasMoreThanOneDosagePerTake(medicamento)

        clearListaHorarioDoses()

        //descomente a linha abaixo para testar a geração das doses utilizando um medicamento "fake" ou para utilizar o medicamento real
        //initializeMedicamentoTeste(MedicamentoTratamento("dipirona",	"18:30",	1,	34,	false,	1,	2,	2,	false,	"02/06/2023",	"04/06/2023",	"toast_already_shown_dipirona",	false, ""	))
        initializeMedicamento(medicamento)


        val horaDigitada = horarioPrimeiraDose
        diaAtual = medicamento.dataInicioTratamento
        var hora = 0
        var minutos = "00"

        val horaDeUmDigito = horaDigitada[1].toString() == ":"
        val primeiroDigitoValido = horaDigitada[0].toString().toInt() in 1..9
        val horaDeDoisDigitos = horaDigitada[2].toString() == ":"
        val primeiroDigitoZero = horaDigitada[0].toString().toInt() == 0
        val segundoDigitoZero = horaDigitada[1].toString().toInt() == 0
        val minutosStr3e4 = horaDigitada.substring(3)
        val minutosStr2e3 = horaDigitada.substring(2, 4)
        val segundoDigitoHoraMaiorQueZero = horaDigitada[1].toString().toInt() > 0
        val primeiroDigitoHoraValido = horaDigitada[0].toString().toInt() in 1..2
        val horaValida = horaDigitada.substring(0, 2).toInt() in 10..23
        val horaVinteEQuatro = horaDigitada == "24:00"

        if (horaDeUmDigito) {
            if (primeiroDigitoValido) {
                hora = horaDigitada[0].toString().toInt()
                minutos = minutosStr2e3
            }
        } else if (horaDeDoisDigitos) {
            if (primeiroDigitoZero) {
                if (segundoDigitoZero) {
                    hora = 0
                    minutos = minutosStr3e4
                } else {
                    if (segundoDigitoHoraMaiorQueZero) {
                        hora = horaDigitada[1].toString().toInt()
                        minutos = minutosStr3e4
                    }
                }
            } else {
                if (primeiroDigitoHoraValido) {
                    if (horaValida) {
                        hora = horaDigitada.substring(0, 2).toInt()
                        minutos = minutosStr3e4
                    }
                } else if (horaVinteEQuatro) {
                    hora = 0
                    minutos = minutosStr3e4
                }
            }
        }


        pegarTodasAsDosesParaONovoMedicamento(nomeMedicamento, hora, qntDoses, minutos)
        Log.d("variaveis", "nomeMedicamento: $nomeMedicamento,hora: $hora,qntDoses: $qntDoses,minutos: $minutos,medicamento: $medicamento")

    }



    private fun initRepositoryAddMedicine(repositoryAdicionarMedicamento: AddMedicineRepositoryImpl) {
        this.repositoryAdicionarMedicamento = repositoryAdicionarMedicamento

    }

    private fun insertDosesInTable() {
        listaHorarioDoses.forEach {
                dose->
            Log.d("printateaqui2", "dose: ${dose.horarioDose}")



            insertDose(dose)
        }
    }

    private fun insertDose(doses: Doses){
        val insertDosesResponseLong = repositoryAdicionarMedicamento.insertDoses(doses)

        insertDosesResponse.postValue(insertDosesResponseLong)
    }




    private fun pegarTodasAsDosesParaONovoMedicamento(
        nomeMed: String,
        horaIni: Int,
        qntDoses: Int,
        min: String
    ) {


        //medicamento = MedicamentoTratamento("dipirona",	"20:20",	1,	34,	false,	1,	2,	2,	false,	"02/06/2023",	"04/06/2023",	"toast_already_shown_dipirona",	false, ""	)
        Log.d("testinginputs", "nomeMed: $nomeMed, horaIni: ${horaIni}, qntDoses: $qntDoses, min: $min, medicamento: $medicamento")
        maiorHoraAteAgora = 0


        diaAtual = medicamento.dataInicioTratamento

        var hora = horaIni
        var minInicial = min
        val intervaloEntreDoses = 24 / qntDoses
        val intervalo = (24.toDouble() / qntDoses)
        val numDosesNumUnicoHorario = medicamento.num_doses_num_unico_horario
        dosesPorTomada = numDosesNumUnicoHorario
        val intervaloEntreDosesIsGreaterThanOne =
            checkIfIntervaloEntreDosesIsGreaterThanOne(intervaloEntreDoses.toDouble())
        val totalDoses = qntDoses * medicamento.totalDiasTratamento
        qntTotalDeDoses = totalDoses * numDosesNumUnicoHorario


        // faz todas as doses de uma unica tomada
        criarDosesNovasUnicoHorarioIntervaloEmHoras(numDosesNumUnicoHorario, hora, minInicial, qntDoses, nomeMed, intervalo)



        // codigo para criar a proxima dose somando hora ao intervaloDeDoses
        for (i in 1 until totalDoses) {
            qntDosesRemanescentes = qntDosesDia

            if (hora + intervaloEntreDoses > 24) {
                //checa se o intervalo é em horas ou em minutos
                if (intervaloEntreDosesIsGreaterThanOne) {
                    //o intervalo é maior que uma hora(intervalo em horas)
                    //pega o horario da proxima dose e atualiza a variavel hora
                    hora = hora + intervaloEntreDoses - 24

                    criarDosesNovasUnicoHorarioIntervaloEmHoras(numDosesNumUnicoHorario, hora, minInicial, qntDoses, nomeMed, intervalo)

                }else {

                    //o intervalo é menor que uma hora(intervalo em minutos)
                    val minutosSoma = intervalo * 60

                    //pega hora da dose atual e soma os minutos do intervalo
                    //depois atualiza as variaveis hora e minInicial
                    var time = LocalTime.of(hora, minInicial.toInt())
                    time = time.plusMinutes(minutosSoma.toLong())
                    hora = time.hour
                    minInicial = time.minute.toString()

                    criasDosesNovasUnicoHorarioIntervaloEmMinutos(numDosesNumUnicoHorario, time, nomeMed, intervalo, qntDoses)

                }
            }
            else if (hora + intervaloEntreDoses < 24) {
                //pega o horario da proxima dose e atualiza a variavel hora
                hora += intervaloEntreDoses
                if (intervaloEntreDosesIsGreaterThanOne) {
                    criarDosesNovasUnicoHorarioIntervaloEmHoras(numDosesNumUnicoHorario, hora, minInicial, qntDoses, nomeMed, intervalo)
                }else{
                    //o intervalo é menor que uma hora(intervalo em minutos)
                    val minutosSoma = intervalo * 60

                    //pega hora da dose atual e soma os minutos do intervalo
                    //depois atualiza as variaveis hora e minInicial
                    var time = LocalTime.of(hora, minInicial.toInt())
                    time = time.plusMinutes(minutosSoma.toLong())
                    hora = time.hour
                    minInicial = time.minute.toString()

                    criasDosesNovasUnicoHorarioIntervaloEmMinutos(numDosesNumUnicoHorario, time, nomeMed, intervalo, qntDoses)

                }

            }
            else if (hora + intervaloEntreDoses == 24) {
                //hora da proxima dose é meia noite
                hora = 0
                if (intervaloEntreDosesIsGreaterThanOne) {
                    criarDosesNovasUnicoHorarioIntervaloEmHoras(numDosesNumUnicoHorario, hora, minInicial, qntDoses, nomeMed, intervalo)

                }else{
                    //o intervalo é menor que uma hora(intervalo em minutos)
                    var minutosSoma = intervalo * 60
                    Log.d("entendendo", "${minutosSoma.toInt()}")

                    //pega hora da dose atual e soma os minutos do intervalo
                    //depois atualiza as variaveis hora e minInicial
                    var time = LocalTime.of(hora, minInicial.toInt())
                    time = time.plusMinutes(minutosSoma.toLong())
                    hora = time.hour
                    minInicial = time.minute.toString()

                    criasDosesNovasUnicoHorarioIntervaloEmMinutos(numDosesNumUnicoHorario, time, nomeMed, intervalo, qntDoses)




                }
            }

        }





        printListaAteAqui()
        insertDosesInTable()

        /*
        resultados do teste com dados fake:
        dose: Doses(idDose=0, nomeMedicamento=dipirona, horarioDose=02/06/2023 18:30, intervaloEntreDoses=24.0, dataHora=null, qntDosesPorHorario=1, jaTomouDose=false)
        dose: Doses(idDose=0, nomeMedicamento=dipirona, horarioDose=02/06/2023 18:30, intervaloEntreDoses=24.0, dataHora=null, qntDosesPorHorario=1, jaTomouDose=false)

        resultados do teste com dados reais
        dose: 02/06/2023 19:40
        dose: 03/06/2023 19:40
         */


    }

    private fun criasDosesNovasUnicoHorarioIntervaloEmMinutos(
        numDosesNumUnicoHorario: Int,
        time: LocalTime?,
        nomeMed: String,
        intervalo: Double,
        qntDoses: Int
    ) {
        for (i in 1..numDosesNumUnicoHorario) {

            criarDoseComDataCerta(time.toString(), qntDoses)
            val medicamentoDose = criarDose(time, nomeMed, intervalo)
            colocarDoseNaLista(medicamentoDose)
        }



    }

    private fun criarDose(hora: LocalTime?, nomeMed: String, intervaloEntreDoses: Double): Doses {
        Log.d("criardose1", "to no primeiro metodo de criar dose")
        return Doses(
            nomeMedicamento = nomeMed,
            horarioDose = diaAtual +" "+ hora.toString(),
            jaTomouDose = false,
            intervaloEntreDoses = intervaloEntreDoses
        )

    }

    private fun criarDosesNovasUnicoHorarioIntervaloEmHoras(
        numDosesNumUnicoHorario: Int,
        hora: Int,
        minInicial: String,
        qntDoses: Int,
        nomeMed: String,
        intervalo: Double
    ) {
        for (i in 1..numDosesNumUnicoHorario) {
            criarDoseComDataCertaDoisParametros(hora, minInicial, qntDoses)
            val medicamentoDose = criarDose(hora, minInicial, nomeMed, intervalo)
            colocarDoseNaLista(medicamentoDose)
        }



    }

    private fun ifQntDosesFeitasForemMaiorQueNumeroDeDosesPorTomada(){
        if(dosesTomadasNaHoraAtual > dosesPorTomada){
            //significa que todas as doses ja foram feitas para aquele horario
            dosesTomadasNaHoraAtual = 0
        }
    }


    private fun criarDoseComDataCerta(hora: String, qntDoses: Int){
        //dosesTomadasNaHoraAtual = 2                      dosesPorTomada = 2
        val horaFormatada = (hora.get(0).toString() + hora.get(1).toString()).toInt()
        if(horaFormatada < maiorHoraAteAgora){
            diaAtual = somarUmDiaNumaData(diaAtual)
            maiorHoraAteAgora = horaFormatada
            adicionarUmADosesTomadasNaHoraAtual()

            ifQntDosesFeitasForemMaiorQueNumeroDeDosesPorTomada()
            return
        }

        if(horaFormatada > maiorHoraAteAgora){
            maiorHoraAteAgora = horaFormatada
            adicionarUmADosesTomadasNaHoraAtual()

            ifQntDosesFeitasForemMaiorQueNumeroDeDosesPorTomada()
            return

        }
        if(horaFormatada == maiorHoraAteAgora && qntDoses == 125){
            maiorHoraAteAgora = horaFormatada

            return



        }
        //dosesTomadasNaHoraAtual = 1

        if(horaFormatada == maiorHoraAteAgora){
            Log.d("addlistafluxo29-2", "to bem fora dos ifs(inter em horas) hora $hora maiorHoraAteAgora $maiorHoraAteAgora dosesTomadasNaHoraAtual $dosesTomadasNaHoraAtual")

            diaAtual = somarUmDiaNumaData(diaAtual)


            /*

            val horaAux: Int = horaFormatada
            var jaEntrouEmAlgumaCondicaoEmAlgumIf = false
            //jaEntrouEmAlgumaCondicaoEmAlgumIf = intervalIsShorterThanOneHour(horaAux)
            if (jaEntrouEmAlgumaCondicaoEmAlgumIf){
                return
            }
            //jaEntrouEmAlgumaCondicaoEmAlgumIf = dosagesPerTakeGreaterThanOne(horaAux)
            if (jaEntrouEmAlgumaCondicaoEmAlgumIf){
                return
            }
            //jaEntrouEmAlgumaCondicaoEmAlgumIf = dosagesPerTakeGreaterThanOneButAlreadyCreated(horaAux)

             */








        }
    }
















    private fun printListaAteAqui() {
        listaHorarioDoses.forEach {
            Log.d("printateaqui", "dose: $it")
        }
    }

    private fun adicionarUmADosesTomadasNaHoraAtual(){
        dosesTomadasNaHoraAtual++
    }


    private fun criarDoseComDataCertaDoisParametros(hora: Int, minInicial: String, qntDoses: Int){
        //dosesTomadasNaHoraAtual = 1    dosesPorTomada = 2
        if(hora < maiorHoraAteAgora){
            diaAtual = somarUmDiaNumaData(diaAtual)

            maiorHoraAteAgora = hora

            return
        }
        if(hora > maiorHoraAteAgora){
            maiorHoraAteAgora = hora



            return

        }

        if(hora == maiorHoraAteAgora && qntDoses == 125){
            maiorHoraAteAgora = hora

            return



        }

        if(hora == maiorHoraAteAgora){
            Log.d("addlistafluxo29-2", "to bem fora dos ifs hora $hora maiorHoraAteAgora $maiorHoraAteAgora dosesTomadasNaHoraAtual $dosesTomadasNaHoraAtual")
            diaAtual = somarUmDiaNumaData(diaAtual)
            /*
            if(dosesTomadasNaHoraAtual <= dosesPorTomada){
                //ainda nao fiz todas as doses para esse horario
                if(qntDosesDia != 1){
                    dosesTomadasNaHoraAtual++

                }else{
                    if(dosesTomadasNaHoraAtual == dosesPorTomada){
                        diaAtual = somarUmDiaNumaData(diaAtual)
                        dosesTomadasNaHoraAtual = 0
                    }

                }

                Log.d("addlistafluxo29-2", "if hora $hora maiorHoraAteAgora $maiorHoraAteAgora dosesTomadasNaHoraAtual $dosesTomadasNaHoraAtual")

            }else{
                if(dosesTomadasNaHoraAtual == dosesPorTomada){
                    diaAtual = somarUmDiaNumaData(diaAtual)
                    dosesTomadasNaHoraAtual = 0
                }

                Log.d("addlistafluxo29-2", "else hora $hora maiorHoraAteAgora $maiorHoraAteAgora dosesTomadasNaHoraAtual $dosesTomadasNaHoraAtual")

                //ja fiz todas as doses nesse horario
                //pode passar de horario ou passar de dia
                //como eu vou saber se ainda tem doses no dia para continuar no dia ou acrescentar um ao dia?
            }

             */


//            var jaEntrouEmAlgumaCondicaoEmAlgumIf = false
//            //jaEntrouEmAlgumaCondicaoEmAlgumIf = intervalIsShorterThanOneHour(hora)
//            if (jaEntrouEmAlgumaCondicaoEmAlgumIf){
//                return
//            }
//            //jaEntrouEmAlgumaCondicaoEmAlgumIf = dosagesPerTakeGreaterThanOne(hora)
//            if (jaEntrouEmAlgumaCondicaoEmAlgumIf){
//                return
//            }
//            //jaEntrouEmAlgumaCondicaoEmAlgumIf = dosagesPerTakeGreaterThanOneButAlreadyCreated(hora)






        }
    }




    private fun criarDose(hora: Int, minInicial: String, nomeMed: String, intervaloEntreDoses: Double): Doses {


        return Doses(
            nomeMedicamento = nomeMed,
            horarioDose = diaAtual + " "+ hora.toString() + ":" + minInicial,
            jaTomouDose = false,
            intervaloEntreDoses = intervaloEntreDoses
        )
    }

    private fun checkIfIntervaloEntreDosesIsGreaterThanOne(intervaloEntreDoses: Double): Boolean {


        if (intervaloEntreDoses < 1) {
            intervaloEntreDosesMaiorQueUmaHora = false
            return false
        }

        intervaloEntreDosesMaiorQueUmaHora = true
        return true
    }

    private fun colocarDoseNaLista(medicamentoDose: Doses) {
        Log.d("addlistafluxo29", "to aqui no metodo que coloca na lista dose: ${medicamentoDose.horarioDose}")





        listaHorarioDoses.add(medicamentoDose)
    }

    fun somarUmDiaNumaData(data: String): String{
        var data = data
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd/MM/yyyy")


        val dt = formatter.parse(data)
        calendar.time = dt
        calendar.add(Calendar.DATE, 1)
        data = formatter.format(calendar.time)

        /*
        ele nao ta salvando todas as doses...
         */



        return data
    }
}


