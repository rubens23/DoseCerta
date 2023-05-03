package com.rubens.applembretemedicamento.presentation

import android.view.View
import android.widget.LinearLayout
import com.google.android.material.textfield.TextInputLayout
import com.rubens.applembretemedicamento.databinding.FragmentCadastrarNovoMedicamentoBinding
import com.rubens.applembretemedicamento.presentation.interfaceseimpl.FragmentCadastrarNovoMedicamentoBindingInterface
import com.rubens.applembretemedicamento.presentation.interfaceseimpl.FragmentCadastrarNovoMedicamentoBindingWrapper
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FragmentCadastrarNovoMedicamentoTest{

    @Mock
    private lateinit var mockBinding: FragmentCadastrarNovoMedicamentoBinding

    private lateinit var bindingWrapper: FragmentCadastrarNovoMedicamentoBindingWrapper
    private lateinit var fragment: FragmentCadastrarNovoMedicamento

    @Before
    fun setup(){
        fragment = FragmentCadastrarNovoMedicamento()
        bindingWrapper = FragmentCadastrarNovoMedicamentoBindingWrapper(mockBinding)
        fragment.binding = bindingWrapper.getBinding()
    }

    @Test
    fun `test mudarVisibilidadeDasViewRelacionadasADuracaoTratamento`(){
        // Mock views
        val mockContainerButtons = mock(LinearLayout::class.java)
        val mockTilMedicineTimeTreatment = mock(TextInputLayout::class.java)

       `when`(mockBinding.containerButtons).thenReturn(mockContainerButtons)
        `when`(mockBinding.tilMedicineTimeTreatment).thenReturn(mockTilMedicineTimeTreatment)

        // Call method to be tested
        fragment.mudarVisibilidadeDasViewsRelacionadasADuracaoTratamento()

        // Verify views state (to see if method called works)

        verify(mockContainerButtons).visibility = View.INVISIBLE
        verify(mockTilMedicineTimeTreatment).hint = "Quantos dias?"
        verify(mockTilMedicineTimeTreatment).visibility = View.VISIBLE



    }

}