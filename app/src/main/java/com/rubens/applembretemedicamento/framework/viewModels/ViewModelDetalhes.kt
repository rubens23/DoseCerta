package com.rubens.applembretemedicamento.framework.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmedicamentos.data.repository.MedicationRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelDetalhes @Inject constructor(
    private val medicationRepository: MedicationRepositoryImpl
): ViewModel() {

    fun pararAlarmeTocandoDeTodosMedicamentos() {
        viewModelScope.launch {
            medicationRepository.passarFalseParaAlarmeTocandoDeTodosMedicamentos()
        }
    }
}