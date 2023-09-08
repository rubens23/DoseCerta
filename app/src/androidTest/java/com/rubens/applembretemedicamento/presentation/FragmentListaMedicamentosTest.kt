package com.rubens.applembretemedicamento.presentation

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.example.appmedicamentos.data.repository.AddMedicineRepositoryImpl
import org.junit.Test
import com.rubens.applembretemedicamento.launchFragmentInHiltContainer
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.AdapterListaMedicamentos
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.ToastMatcher
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import com.rubens.applembretemedicamento.framework.data.managers.RoomAccess
import com.rubens.applembretemedicamento.framework.domain.MedicamentoManager
import com.rubens.applembretemedicamento.utils.AlarmUtilsInterface
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.CalendarHelper2
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named



@MediumTest
@HiltAndroidTest
class FragmentListaMedicamentosTest{
    lateinit var navController: TestNavHostController
    private val LIST_ITEM_IN_TEST = 1

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @Named("alarmUtils")
    lateinit  var alarmUtilsInterface: AlarmUtilsInterface
    @Inject
    @Named("medicamentoManager")
    lateinit var medicamentoManager: MedicamentoManager
    @Inject
    @Named("funcoesTempo")
    lateinit var funcoesDeTempo: FuncoesDeTempo
    @Inject
    @Named("calendarHelper")
    lateinit var calendarHelper: CalendarHelper
    @Inject
    @Named("context")
    lateinit var context: Context
    @Inject
    @Named("roomAccess")
    lateinit var roomAccess: RoomAccess
    @Inject
    @Named("addmedicamentosrepository")
    lateinit var addMedicineRepositoryImpl: AddMedicineRepositoryImpl

    var is24HourFormat: Boolean = false

    @Inject
    @Named("calendarHelper2")
    lateinit var calendarHelper2: CalendarHelper2

    @Inject
    @Named("defaultdate")
    lateinit var defaultDeviceDateFormat: String




    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup(){
        hiltRule.inject()

        navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        launchFragmentInHiltContainer<FragmentListaMedicamentos>(
            fragmentFactory = MainFragmentFactory(
                alarmUtilsInterface,
                medicamentoManager,
                funcoesDeTempo,
                calendarHelper,
                context,
                roomAccess,
                calendarHelper2,
                is24HourFormat,
                defaultDeviceDateFormat
            )

        ) {
            navController.setGraph(R.navigation.navigation)


            Navigation.setViewNavController(requireView(), navController)
        }





    }



    @Test
    fun testHiltFragment(){


        assertThat(navController.currentDestination?.id, `is`(R.id.medicamentosFragment))
    }

    @Test
    fun testClick(){



        onView(withId(R.id.fab)).perform(click())



        assertThat(navController.currentDestination?.id, `is`(R.id.fragmentCadastrarNovoMedicamento))

        launchFragmentInHiltContainer<FragmentCadastrarNovoMedicamento>(
            fragmentFactory = MainFragmentFactory(
                alarmUtilsInterface,
                medicamentoManager,
                funcoesDeTempo,
                calendarHelper,
                context,
                roomAccess,
                calendarHelper2,
                is24HourFormat,
                defaultDeviceDateFormat
            )

        ) {
            navController.setGraph(R.navigation.navigation)


            Navigation.setViewNavController(this.requireView(), navController)
        }

        val expectedFormat = SimpleDateFormat("MM/dd/yyyy")
        val currentDate = Date()
        val todaysDate = expectedFormat.format(currentDate)


        //esse metodo abaixo retorna resultados errados
        ViewMatchers.assertThat(
            navController.currentDestination?.id, `is`(R.id.medicamentosFragment)
        )

        onView(withId(R.id.layout_cadastrar_medicamento))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))

        val medicineName = "Omeprazol"
        val medicineQntPerDay = "4"
        val medicineStartDate = calendarHelper2.somarUmDiaNumaData(todaysDate)
        val duracaoTratamento = "2"
        val desiredHour = 21
        val desiredMinute = 30



        onView(withId(R.id.til_medicine_name_child)).perform(typeText(medicineName), closeSoftKeyboard())
        onView(withId(R.id.til_medicine_qnt_per_day_child)).perform(typeText(medicineQntPerDay), closeSoftKeyboard())
        onView(withId(R.id.btn_duracao_dias)).perform(click())
        onView(withId(R.id.til_medicine_time_treatment_child)).perform(typeText(duracaoTratamento), closeSoftKeyboard())
        onView(withId(R.id.btn_open_time_picker)).perform(click())
        onView(ViewMatchers.withClassName(`is`("android.widget.TimePicker")))
            .perform(PickerActions.setTime(desiredHour, desiredMinute))
        onView(ViewMatchers.withText("OK")).perform(click())
        onView(withId(R.id.input_data_inicio_tratamento)).perform(scrollTo())

        onView(withId(R.id.input_data_inicio_tratamento)).perform(typeText(medicineStartDate), closeSoftKeyboard())
        onView(withId(R.id.btn_confirm_new_medication)).perform(click())

        onView(ViewMatchers.withText("$medicineName registered successfully!"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))


    }







    /**
     * começar a testar a recycler view
     */

    @Test
    fun isRecyclerViewVisible(){


        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()))
    }

    @Test
    fun checkIfRecyclerViewItemClickIsOpeningDetalhesFragment(){

        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition<AdapterListaMedicamentos.ViewHolder>(LIST_ITEM_IN_TEST, click()))

        assertThat(navController.currentDestination?.id, `is`(R.id.fragmentDetalhesMedicamentos))

        onView(withId(R.id.fab)).check(matches(isDisplayed()))

        //todo fazer esse teste funcionar


    }


    //01
    @Test
    fun addNewMedicineAndSeeIfDetailsFragmentOpens(){
        val expectedFormat = SimpleDateFormat("MM/dd/yyyy")
        val currentDate = Date()
        val todaysDate = expectedFormat.format(currentDate)



        onView(withId(R.id.fab)).perform(click())
        //navController.navigate(FragmentListaMedicamentosDirections.actionMedicamentosFragmentToFragmentCadastrarNovoMedicamento())

        assertThat(navController.currentDestination?.id, `is`(R.id.fragmentCadastrarNovoMedicamento))



        val medicineName = "Dipirona"
        val medicineQntPerDay = "4"
        val medicineStartDate = todaysDate

        onView(withId(R.id.til_medicine_name_child))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .perform(replaceText(medicineName), closeSoftKeyboard())


        //onView(withId(R.id.til_medicine_name)).perform(scrollTo(), replaceText(medicineName), closeSoftKeyboard())


        //onView(withId(R.id.layout_cadastrar_medicamento)).check(matches(isDisplayed()))


        //onView(withId(R.id.til_medicine_name_child)).perform(click())
        //onView(withId(R.id.til_medicine_name)).perform(replaceText(medicineName), closeSoftKeyboard())


    }


    //01
    @Test
    fun testClickInMedicine_opensDetailFragment(){
        onView(withId(R.id.fab)).perform(click())



        assertThat(navController.currentDestination?.id, `is`(R.id.fragmentCadastrarNovoMedicamento))

        launchFragmentInHiltContainer<FragmentCadastrarNovoMedicamento>(
            fragmentFactory = MainFragmentFactory(
                alarmUtilsInterface,
                medicamentoManager,
                funcoesDeTempo,
                calendarHelper,
                context,
                roomAccess,
                calendarHelper2,
                is24HourFormat,
                defaultDeviceDateFormat
            )

        ) {
            navController.setGraph(R.navigation.navigation)


            Navigation.setViewNavController(this.requireView(), navController)
        }

        val expectedFormat = SimpleDateFormat("MM/dd/yyyy")
        val currentDate = Date()
        val todaysDate = expectedFormat.format(currentDate)




        val medicineName = "dipirona"
        val medicineQntPerDay = "4"
        val medicineStartDate = calendarHelper2.somarUmDiaNumaData(todaysDate)
        val duracaoTratamento = "2"
        val desiredHour = 21
        val desiredMinute = 30



        onView(withId(R.id.til_medicine_name_child)).perform(typeText(medicineName), closeSoftKeyboard())
        onView(withId(R.id.til_medicine_qnt_per_day_child)).perform(typeText(medicineQntPerDay), closeSoftKeyboard())
        onView(withId(R.id.btn_duracao_dias)).perform(click())
        onView(withId(R.id.til_medicine_time_treatment_child)).perform(typeText(duracaoTratamento), closeSoftKeyboard())
        onView(withId(R.id.btn_open_time_picker)).perform(click())
        onView(ViewMatchers.withClassName(`is`("android.widget.TimePicker")))
            .perform(PickerActions.setTime(desiredHour, desiredMinute))
        onView(ViewMatchers.withText("OK")).perform(click())
        onView(withId(R.id.input_data_inicio_tratamento)).perform(scrollTo())

        onView(withId(R.id.input_data_inicio_tratamento)).perform(typeText(medicineStartDate), closeSoftKeyboard())
        onView(withId(R.id.btn_confirm_new_medication)).perform(click())

        onView(ViewMatchers.withText("$medicineName registered successfully!"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))

        launchFragmentInHiltContainer<FragmentListaMedicamentos>(
            fragmentFactory = MainFragmentFactory(
                alarmUtilsInterface,
                medicamentoManager,
                funcoesDeTempo,
                calendarHelper,
                context,
                roomAccess,
                calendarHelper2,
                is24HourFormat,
                defaultDeviceDateFormat
            )

        ) {
            navController.setGraph(R.navigation.navigation)


            Navigation.setViewNavController(this.requireView(), navController)
        }


        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition<AdapterListaMedicamentos.ViewHolder>(0, click()))


        val medicamentoComDoses = MedicamentoComDoses(
            medicamentoTratamento = MedicamentoTratamento(
                nomeMedicamento = "dipirona",
                horaPrimeiraDose = "9:30 PM",
                qntDoses = 4,
                totalDiasTratamento = 2,
                diasRestantesDeTratamento = 2,
                tratamentoFinalizado = false,
                dataInicioTratamento = "07/18/2023",
                dataTerminoTratamento = "07/20/2023",
                stringDataStore = ""
            ),
            listaDoses = listOf(
                Doses(
                    idDose = 0,
                    nomeMedicamento = "dipirona",
                    horarioDose = "07/18/2023 09:30 PM",
                    intervaloEntreDoses = 6.0,
                    dataHora = null,
                    qntDosesPorHorario = 4,
                    jaTomouDose = false,
                    jaMostrouToast = false
                ),
                Doses(
                    idDose = 0,
                    nomeMedicamento = "dipirona",
                    horarioDose = "07/18/2023 10:30 PM",
                    intervaloEntreDoses = 6.0,
                    dataHora = null,
                    qntDosesPorHorario = 4,
                    jaTomouDose = false,
                    jaMostrouToast = false
                ),
                Doses(
                    idDose = 0,
                    nomeMedicamento = "dipirona",
                    horarioDose = "07/18/2023 11:30 PM",
                    intervaloEntreDoses = 6.0,
                    dataHora = null,
                    qntDosesPorHorario = 4,
                    jaTomouDose = false,
                    jaMostrouToast = false
                ),
                Doses(
                    idDose = 0,
                    nomeMedicamento = "dipirona",
                    horarioDose = "07/19/2023 12:30 AM",
                    intervaloEntreDoses = 6.0,
                    dataHora = null,
                    qntDosesPorHorario = 4,
                    jaTomouDose = false,
                    jaMostrouToast = false
                ),
                Doses(
                    idDose = 0,
                    nomeMedicamento = "dipirona",
                    horarioDose = "07/19/2023 09:30 PM",
                    intervaloEntreDoses = 6.0,
                    dataHora = null,
                    qntDosesPorHorario = 4,
                    jaTomouDose = false,
                    jaMostrouToast = false
                ),
                Doses(
                    idDose = 0,
                    nomeMedicamento = "dipirona",
                    horarioDose = "07/19/2023 10:30 PM",
                    intervaloEntreDoses = 6.0,
                    dataHora = null,
                    qntDosesPorHorario = 4,
                    jaTomouDose = false,
                    jaMostrouToast = false
                ),
                Doses(
                    idDose = 0,
                    nomeMedicamento = "dipirona",
                    horarioDose = "07/19/2023 11:30 PM",
                    intervaloEntreDoses = 6.0,
                    dataHora = null,
                    qntDosesPorHorario = 4,
                    jaTomouDose = false,
                    jaMostrouToast = false
                ),
                Doses(
                    idDose = 0,
                    nomeMedicamento = "dipirona",
                    horarioDose = "07/20/2023 12:30 AM",
                    intervaloEntreDoses = 6.0,
                    dataHora = null,
                    qntDosesPorHorario = 4,
                    jaTomouDose = false,
                    jaMostrouToast = false
                )
            )
        )

        val horaproximadose = "9:30 PM"
        val intervaloentredoses = "6.0"



        launchFragmentInHiltContainer<FragmentDetalhesMedicamentos>(
            fragmentFactory = MainFragmentFactory(
                alarmUtilsInterface,
                medicamentoManager,
                funcoesDeTempo,
                calendarHelper,
                context,
                roomAccess,
                calendarHelper2,
                is24HourFormat,
                defaultDeviceDateFormat
            ),
            fragmentArgs = bundleOf("medicamento" to medicamentoComDoses,
                "horaproximadose" to horaproximadose,
                "intervaloentredoses" to intervaloentredoses,
                "medicamentoManager" to medicamentoManager)

        ) {
            navController.setGraph(R.navigation.navigation)


            Navigation.setViewNavController(this.requireView(), navController)
        }
        onView(withId(R.id.btn_armar_alarme)).check(matches(isDisplayed()))





    }

    //02
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun seeIfAlarmPlays(){
        onView(withId(R.id.fab)).perform(click())



        assertThat(navController.currentDestination?.id, `is`(R.id.fragmentCadastrarNovoMedicamento))

        launchFragmentInHiltContainer<FragmentCadastrarNovoMedicamento>(
            fragmentFactory = MainFragmentFactory(
                alarmUtilsInterface,
                medicamentoManager,
                funcoesDeTempo,
                calendarHelper,
                context,
                roomAccess,
                calendarHelper2,
                is24HourFormat,
                defaultDeviceDateFormat
            )

        ) {
            navController.setGraph(R.navigation.navigation)


            Navigation.setViewNavController(this.requireView(), navController)
        }

        val expectedFormat = SimpleDateFormat("MM/dd/yyyy")
        val currentDate = Date()
        val todaysDate = expectedFormat.format(currentDate)


        //coloque o alarme daqui a dois minutos

        val medicineName = "losartana"
        val medicineQntPerDay = "4"
        val medicineStartDate = todaysDate
        val duracaoTratamento = "2"
        val desiredHour = 1
        val desiredMinute = 25
        val horaPrimeiraDose = "01:25 AM"

        onView(withId(R.id.til_medicine_name_child)).perform(typeText(medicineName), closeSoftKeyboard())
        onView(withId(R.id.til_medicine_qnt_per_day_child)).perform(typeText(medicineQntPerDay), closeSoftKeyboard())
        onView(withId(R.id.btn_duracao_dias)).perform(click())
        onView(withId(R.id.til_medicine_time_treatment_child)).perform(typeText(duracaoTratamento), closeSoftKeyboard())
        onView(withId(R.id.btn_open_time_picker)).perform(click())
        onView(ViewMatchers.withClassName(`is`("android.widget.TimePicker")))
            .perform(PickerActions.setTime(desiredHour, desiredMinute))
        onView(ViewMatchers.withText("OK")).perform(click())
        onView(withId(R.id.input_data_inicio_tratamento)).perform(scrollTo())

        onView(withId(R.id.input_data_inicio_tratamento)).perform(typeText(medicineStartDate), closeSoftKeyboard())
        onView(withId(R.id.btn_confirm_new_medication)).perform(click())

        onView(ViewMatchers.withText("$medicineName registered successfully!"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))

        launchFragmentInHiltContainer<FragmentListaMedicamentos>(
            fragmentFactory = MainFragmentFactory(
                alarmUtilsInterface,
                medicamentoManager,
                funcoesDeTempo,
                calendarHelper,
                context,
                roomAccess,
                calendarHelper2,
                is24HourFormat,
                defaultDeviceDateFormat
            )

        ) {
            navController.setGraph(R.navigation.navigation)


            Navigation.setViewNavController(this.requireView(), navController)
        }


        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition<AdapterListaMedicamentos.ViewHolder>(0, click()))

        val newBundle = makeNewBundle(medicineName, medicineQntPerDay, medicineStartDate, duracaoTratamento, desiredHour, desiredMinute, horaPrimeiraDose, "6.0")


        launchFragmentInHiltContainer<FragmentDetalhesMedicamentos>(
            fragmentFactory = MainFragmentFactory(
                alarmUtilsInterface,
                medicamentoManager,
                funcoesDeTempo,
                calendarHelper,
                context,
                roomAccess,
                calendarHelper2,
                is24HourFormat,
                defaultDeviceDateFormat
            ),
            fragmentArgs = newBundle

        ) {
            navController.setGraph(R.navigation.navigation)


            Navigation.setViewNavController(this.requireView(), navController)
        }
        onView(withId(R.id.btn_armar_alarme)).check(matches(isDisplayed()))

        onView(withId(R.id.btn_armar_alarme)).perform(click())


        val latch = CountDownLatch(1)

        val timeoutSeconds = 360


        val alarmTriggered = latch.await(timeoutSeconds.toLong(), TimeUnit.SECONDS)

        onView(withId(R.id.btn_parar_som)).check(matches(isDisplayed()))


    }

    private fun makeNewBundle(
        medicineName: String,
        medicineQntPerDay: String,
        medicineStartDate: String,
        duracaoTratamento: String,
        desiredHour: Int,
        desiredMinute: Int,
        horaPrimeiraDose: String,
        intervaloentredoses: String
    ): Bundle {


        val medicamentoComDoses = MedicamentoComDoses(
            medicamentoTratamento = MedicamentoTratamento(
                nomeMedicamento = medicineName,
                horaPrimeiraDose = horaPrimeiraDose,
                qntDoses = medicineQntPerDay.toInt(),
                totalDiasTratamento = duracaoTratamento.toInt(),
                diasRestantesDeTratamento = 2,
                tratamentoFinalizado = false,
                dataInicioTratamento = medicineStartDate,
                dataTerminoTratamento = "12/20/2023",
                stringDataStore = ""
            ),
            listaDoses = listOf(
                Doses(
                    idDose = 0,
                    nomeMedicamento = medicineName,
                    horarioDose = "07/19/2023 01:25 AM",
                    intervaloEntreDoses = intervaloentredoses.toDouble(),
                    dataHora = null,
                    qntDosesPorHorario = 1,
                    jaTomouDose = false,
                    jaMostrouToast = false
                ),
                Doses(
                    idDose = 1,
                    nomeMedicamento = medicineName,
                    horarioDose = "07/19/2023 07:00 AM",
                    intervaloEntreDoses = intervaloentredoses.toDouble(),
                    dataHora = null,
                    qntDosesPorHorario = 1,
                    jaTomouDose = false,
                    jaMostrouToast = false
                ),
                Doses(
                    idDose = 2,
                    nomeMedicamento = medicineName,
                    horarioDose = "07/19/2023 14:00 PM",
                    intervaloEntreDoses = intervaloentredoses.toDouble(),
                    dataHora = null,
                    qntDosesPorHorario = 1,
                    jaTomouDose = false,
                    jaMostrouToast = false
                ),
                Doses(
                    idDose = 3,
                    nomeMedicamento = medicineName,
                    horarioDose = "07/19/2023 20:00 PM",
                    intervaloEntreDoses = intervaloentredoses.toDouble(),
                    dataHora = null,
                    qntDosesPorHorario = 1,
                    jaTomouDose = false,
                    jaMostrouToast = false
                ),
                Doses(
                    idDose = 4,
                    nomeMedicamento = medicineName,
                    horarioDose = "07/20/2023 02:00 AM",
                    intervaloEntreDoses = intervaloentredoses.toDouble(),
                    dataHora = null,
                    qntDosesPorHorario = 1,
                    jaTomouDose = false,
                    jaMostrouToast = false
                ),
                Doses(
                    idDose = 5,
                    nomeMedicamento = medicineName,
                    horarioDose = "07/20/2023 08:00 AM",
                    intervaloEntreDoses = intervaloentredoses.toDouble(),
                    dataHora = null,
                    qntDosesPorHorario = 1,
                    jaTomouDose = false,
                    jaMostrouToast = false
                ),
                Doses(
                    idDose = 6,
                    nomeMedicamento = medicineName,
                    horarioDose = "07/20/2023 14:00 PM",
                    intervaloEntreDoses = intervaloentredoses.toDouble(),
                    dataHora = null,
                    qntDosesPorHorario = 1,
                    jaTomouDose = false,
                    jaMostrouToast = false
                ),
                Doses(
                    idDose = 7,
                    nomeMedicamento = medicineName,
                    horarioDose = "07/20/2023 20:00 PM",
                    intervaloEntreDoses = intervaloentredoses.toDouble(),
                    dataHora = null,
                    qntDosesPorHorario = 1,
                    jaTomouDose = false,
                    jaMostrouToast = false
                )
            )
        )

        val horaproximadose = "07/19/2023 01:25 AM"
        val intervaloentredoses = "6.0"

        return bundleOf("medicamento" to medicamentoComDoses,
            "horaproximadose" to horaproximadose,
            "intervaloentredoses" to intervaloentredoses,
            "medicamentoManager" to medicamentoManager)



    }


}

data class MockBundle(
    val medicamento: MedicamentoComDoses,
    val horaproxdose: String,
    val intervaloentredoses: String,
    val medicamentoManager: MedicamentoManager
)