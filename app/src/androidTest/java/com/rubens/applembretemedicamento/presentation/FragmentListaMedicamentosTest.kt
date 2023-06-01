package com.rubens.applembretemedicamento.presentation

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
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Test
import org.junit.runner.RunWith
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.launchFragmentInHiltContainer
import com.rubens.applembretemedicamento.presentation.recyclerviewadapters.AdapterListaMedicamentos
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before


@RunWith(AndroidJUnit4ClassRunner::class)
class FragmentListaMedicamentosTest{
    lateinit var navController: TestNavHostController
    private val LIST_ITEM_IN_TEST = 1

    @Before
    fun setup(){
        navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )


    }



    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testHiltFragment(){

        launchFragmentInHiltContainer<FragmentListaMedicamentos> {
            navController.setGraph(R.navigation.navigation)


            Navigation.setViewNavController(this.requireView(), navController)
        }

        assertThat(navController.currentDestination?.id, `is`(R.id.medicamentosFragment))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testNavigationToFragmentConfiguracoes(){

        launchFragmentInHiltContainer<FragmentListaMedicamentos> {
            navController.setGraph(R.navigation.navigation)

            Navigation.setViewNavController(this.requireView(), navController)

        }


        onView(withId(R.id.btn_settings)).perform(click())
        assertThat(navController.currentDestination?.id, `is`(R.id.fragmentConfiguracoes))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testHideToolbarTitleOnInterfaceMethodClick(){
        launchFragmentInHiltContainer<FragmentListaMedicamentos> {
            navController.setGraph(R.navigation.navigation)

            Navigation.setViewNavController(this.requireView(), navController)

            this.mainActivityInterface.hideToolbarTitle()

        }
    }

    /**
     * começar a testar a recycler view
     */

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun isRecyclerViewVisible(){
        launchFragmentInHiltContainer<FragmentListaMedicamentos> {
            navController.setGraph(R.navigation.navigation)

            Navigation.setViewNavController(this.requireView(), navController)


        }

        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun checkIfRecyclerViewItemClickIsOpeningDetalhesFragment(){
        launchFragmentInHiltContainer<FragmentListaMedicamentos> {
            navController.setGraph(R.navigation.navigation)

            Navigation.setViewNavController(this.requireView(), navController)


        }
        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition<AdapterListaMedicamentos.ViewHolder>(LIST_ITEM_IN_TEST, click()))

        assertThat(navController.currentDestination?.id, `is`(R.id.fragmentDetalhesMedicamentos))

    }





}