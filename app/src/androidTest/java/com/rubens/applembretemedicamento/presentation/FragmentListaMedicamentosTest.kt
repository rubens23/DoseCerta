package com.rubens.applembretemedicamento.presentation

import android.content.Context
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.rubens.applembretemedicamento.HiltTestRunner
import org.junit.Test
import org.junit.runner.RunWith
import com.rubens.applembretemedicamento.launchFragmentInHiltContainer
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.AdapterListaMedicamentos
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.framework.data.managers.RoomAccess
import com.rubens.applembretemedicamento.framework.domain.MedicamentoManager
import com.rubens.applembretemedicamento.utils.AlarmUtilsInterface
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import dagger.hilt.android.testing.CustomTestApplication
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Rule
import javax.inject.Inject
import javax.inject.Named


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
                roomAccess
            )

        ) {
            navController.setGraph(R.navigation.navigation)


            Navigation.setViewNavController(this.requireView(), navController)
        }




    }



    @Test
    fun testHiltFragment(){


        assertThat(navController.currentDestination?.id, `is`(R.id.medicamentosFragment))
    }

    @Test
    fun testNavigationToFragmentConfiguracoes(){


        onView(withId(R.id.btn_settings)).perform(click())
        assertThat(navController.currentDestination?.id, `is`(R.id.fragmentConfiguracoes))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testHideToolbarTitleOnInterfaceMethodClick(){
        launchFragmentInHiltContainer<FragmentListaMedicamentos> {
            navController.setGraph(R.navigation.navigation)

            Navigation.setViewNavController(this.requireView(), navController)

            //this.mainActivityInterface.hideToolbarTitle()

        }
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

    }







}