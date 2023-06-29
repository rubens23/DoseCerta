package com.rubens.applembretemedicamento.presentation

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import com.rubens.applembretemedicamento.framework.domain.MedicamentoManager

@RunWith(AndroidJUnit4ClassRunner::class)
class FragmentDetalhesMedicamentosTest{


    @Test
    fun test_openFragmentDetalhes() {
        /*
        val mm = MedicamentoManager()
        val bundle = Bundle()
        bundle.putSerializable("medicamento", MedicamentoComDoses(MedicamentoTratamento("dipirona", "26/05/2023 17:00", 4, 1, false, 1,
        2, 2, false, "26/05/2023", "28/05/2023", "", false, ""), listOf(
            Doses(1, "dipirona", "26/05/2023 17:00", 4.0, "26/05/2023 17:00", 1, false)
        )))
        bundle.putString("horaproximadose", "26/05/2023 17:00")
        bundle.putString("intervaloentredoses", "4")
        bundle.putParcelable("medicamentomanager", mm)
        val scenario = launchFragmentInContainer<FragmentDetalhesMedicamentos>(
            fragmentArgs = bundle
        )

        onView(withId(R.id.label_data_inicio_tratamento)).check(matches(isDisplayed()))


         */
    }


}