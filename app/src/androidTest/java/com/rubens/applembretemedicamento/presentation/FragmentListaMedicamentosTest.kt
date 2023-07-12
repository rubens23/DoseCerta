package com.rubens.applembretemedicamento.presentation

import android.content.Context
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.appmedicamentos.data.repository.AddMedicineRepositoryImpl
import org.junit.Test
import com.rubens.applembretemedicamento.launchFragmentInHiltContainer
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.AdapterListaMedicamentos
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.framework.data.managers.RoomAccess
import com.rubens.applembretemedicamento.framework.domain.MedicamentoManager
import com.rubens.applembretemedicamento.presentation.interfaces.MainActivityInterface
import com.rubens.applembretemedicamento.utils.AlarmUtilsInterface
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.CalendarHelper2
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.allOf
import org.junit.Rule
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
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
                is24HourFormat
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

        /*
        //todo
        fazer um app com dois fragmentos com um botao com acoes de navigation
        nao vai ter injecao de dependencias, portanto da pra usar o launchFragmentInContainer normal

        vamos ver se a navegacao funciona, ou se da o mesmo erro que deu aqui
         */


        onView(withId(R.id.fab)).perform(click())

        assertThat(navController.currentDestination?.id, `is`(R.id.fragmentCadastrarNovoMedicamento))







        onView(withId(R.id.layout_cadastrar_medicamento))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))




        //onView(withId(R.id.btn_open_time_picker)).perform(click())

//        onView(allOf(withId(R.id.layout_cadastrar_medicamento),
//        isDescendantOfA(allOf(withId(R.id.container_cl),
//        isDescendantOfA(withId(R.id.btn_open_time_picker))
//        ))
//        )).perform(click())



    }



    @Test
    fun testNavigationToFragmentConfiguracoes(){


        onView(withId(R.id.btn_settings)).perform(click())
        assertThat(navController.currentDestination?.id, `is`(R.id.fragmentConfiguracoes))


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







}