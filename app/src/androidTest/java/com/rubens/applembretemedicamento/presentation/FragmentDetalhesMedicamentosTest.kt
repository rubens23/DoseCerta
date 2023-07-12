package com.rubens.applembretemedicamento.presentation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.appmedicamentos.data.repository.AddMedicineRepositoryImpl
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import com.rubens.applembretemedicamento.framework.data.managers.RoomAccess
import com.rubens.applembretemedicamento.framework.domain.MedicamentoManager
import com.rubens.applembretemedicamento.launchFragmentInHiltContainer
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.AdapterListaMedicamentos
import com.rubens.applembretemedicamento.utils.AlarmUtilsInterface
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.CalendarHelper2
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidTest
class FragmentDetalhesMedicamentosTest {

    lateinit var navController: TestNavHostController
    private val LIST_ITEM_IN_TEST = 1


    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @Named("alarmUtils")
    lateinit var alarmUtilsInterface: AlarmUtilsInterface

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
    @Named("calendarHelper2")
    lateinit var calendarHelper2: CalendarHelper2

    var is24HourFormat: Boolean = false

    @Inject
    @Named("addmedicamentosrepository")
    lateinit var addMedicineRepositoryImpl: AddMedicineRepositoryImpl


    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
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


            Navigation.setViewNavController(this.requireView(), navController)
        }


    }

}






