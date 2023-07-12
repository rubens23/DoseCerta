package com.rubens.applembretemedicamento.presentation

import android.content.Context
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.SmallTest
import com.example.appmedicamentos.data.repository.AddMedicineRepositoryImpl
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.ToastMatcher
import com.rubens.applembretemedicamento.framework.data.managers.RoomAccess
import com.rubens.applembretemedicamento.framework.domain.MedicamentoManager
import com.rubens.applembretemedicamento.launchFragmentInHiltContainer
import com.rubens.applembretemedicamento.utils.AlarmUtilsInterface
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.CalendarHelper2
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.Pattern.matches
import javax.inject.Inject
import javax.inject.Named
import androidx.test.espresso.assertion.ViewAssertions.matches



@SmallTest
@HiltAndroidTest
class FragmentCadastrarNovoMedicamentoTest{
    lateinit var navController: TestNavHostController

    /**
     * para executar esses testes desative o uso da mainActivityInterface no fragmentCadastrarNovoMedicamento
     */

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





    }

    @Test
    fun testAddNewMedicine(){
        //formato de data MM/dd/yyyy
        //formato de hora: 12 horas
        //linguagem ingles
        //mude o nome do medicamento
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

        val medicineName = "Ibuprofeno"
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
        onView(withClassName(`is`("android.widget.TimePicker")))
            .perform(PickerActions.setTime(desiredHour, desiredMinute))
        onView(withText("OK")).perform(click())
        onView(withId(R.id.input_data_inicio_tratamento)).perform(typeText(medicineStartDate), closeSoftKeyboard())
        onView(withId(R.id.btn_confirm_new_medication)).perform(click())

        onView(withText("$medicineName registered successfully!"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))








    }

    //13
    @Test
    fun testToastShowsWhenMedicationFirstDoseHourAlreadyPassed(){
        //formato de data MM/dd/yyyy
        //formato de hora: 12 horas
        //linguagem ingles
        //mude o nome do medicamento
        //coloque uma hora que ja passou
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

        val medicineName = "Cataflam"
        val medicineQntPerDay = "4"
        val medicineStartDate = todaysDate
        val duracaoTratamento = "2"
        val desiredHour = 17
        val desiredMinute = 30



        onView(withId(R.id.til_medicine_name_child)).perform(typeText(medicineName), closeSoftKeyboard())
        onView(withId(R.id.til_medicine_qnt_per_day_child)).perform(typeText(medicineQntPerDay), closeSoftKeyboard())
        onView(withId(R.id.btn_duracao_dias)).perform(click())
        onView(withId(R.id.til_medicine_time_treatment_child)).perform(typeText(duracaoTratamento), closeSoftKeyboard())
        onView(withId(R.id.btn_open_time_picker)).perform(click())
        onView(withClassName(`is`("android.widget.TimePicker")))
            .perform(PickerActions.setTime(desiredHour, desiredMinute))
        onView(withText("OK")).perform(click())
        onView(withId(R.id.input_data_inicio_tratamento)).perform(typeText(medicineStartDate), closeSoftKeyboard())
        onView(withId(R.id.btn_confirm_new_medication)).perform(click())

        onView(withText("the date and time you chose for the first dose has already passed!"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))








    }

}