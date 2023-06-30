package com.rubens.applembretemedicamento.framework.data.daos

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.rubens.applembretemedicamento.framework.data.AppDatabase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named
import androidx.test.ext.junit.runners.AndroidJUnit4.*
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import com.google.common.truth.Truth.assertThat



@HiltAndroidTest
@SmallTest
class MedicamentoDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @Named("db")
    lateinit var database: AppDatabase
    @Inject
    @Named("medicamentoDao")
    lateinit var medicamentoDao: MedicamentoDao

    @Before
    fun setup() {
        hiltRule.inject()

    }

    @After
    fun teardown(){
        database.close()
    }

    /*
    use esse codigo para quando voce estiver usando suspend functions na dao



        @Test
    fun insertMedicamento() = runBlockingTest{
        val medicamento = MedicamentoTratamento(
            nomeMedicamento = "Paracetamol",
            horaPrimeiraDose = "08:00",
            qntDoses = 3,
            totalDiasTratamento = 7,
            diasRestantesDeTratamento = 5,
            tratamentoFinalizado = false,
            dataInicioTratamento = "2023-06-01",
            dataTerminoTratamento = "2023-06-07",
            stringDataStore = "2023-06-01",
            colunaTeste = "Teste"
        )
        medicamentoDao.insertMedicamento(medicamento)

        val listaMedicamentos = medicamentoDao.getMedicamentos()

        assertThat(listaMedicamentos).contains(medicamento)



    }

     */

    /*

    @Test
    fun insertMedicamento(){
        //esse teste nao funciona com um medicamento que eu invento na hora
        //pois como um metodo é executado depois do outro de forma sincrona
        //talvez nao de tempo de salvar e depois ler
        //para esse teste ficar mais confiavel tenho que suspender as funcoes la na minha
        //dao
        val medicamento = MedicamentoTratamento(nomeMedicamento="losartana",
            horaPrimeiraDose="14:29",
        qntDoses=4,
        idMedicamento=1,
        alarmeAtivado=true,
        num_doses_num_unico_horario=1,
        totalDiasTratamento=2,
        diasRestantesDeTratamento=2,
        tratamentoFinalizado=false,
        dataInicioTratamento="29/06/2023",
        dataTerminoTratamento="01/07/2023",
        stringDataStore="toast_already_shown_losartana",
        alarmeTocando=false,
        colunaTeste="")
        medicamentoDao.insertMedicamento(medicamento)

        val listaMedicamentos = medicamentoDao.getMedicamentos()

        assertThat(listaMedicamentos).contains(medicamento)


    }

     */

    @Test
    fun insertMedicamento() = runBlockingTest{
        val medicamento = MedicamentoTratamento(
            nomeMedicamento = "Paracetamol",
            horaPrimeiraDose = "08:00",
            qntDoses = 3,
            totalDiasTratamento = 7,
            diasRestantesDeTratamento = 5,
            tratamentoFinalizado = false,
            dataInicioTratamento = "2023-06-01",
            dataTerminoTratamento = "2023-06-07",
            stringDataStore = "2023-06-01",
            colunaTeste = "Teste"
        )
        medicamentoDao.insertMedicamento(medicamento)

        val listaMedicamentos = medicamentoDao.getMedicamentos()

        assertThat(listaMedicamentos).contains(medicamento)



    }




}