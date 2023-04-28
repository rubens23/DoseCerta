package com.rubens.applembretemedicamento.framework.viewModels

import androidx.lifecycle.ViewModel
import com.example.appmedicamentos.data.repository.MedicationRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val medicationRepository: MedicationRepositoryImpl
): ViewModel() {

    fun desativarOAlarmeDeTodosMedicamentos() {
        medicationRepository.desativarTodosOsAlarmes()
    }
}