package com.rubens.applembretemedicamento.framework.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rubens.applembretemedicamento.presentation.FragmentListaMedicamentos
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class ActivityHostAndFragmentListaMedicamentosSharedViewModel: ViewModel() {

    private val _getFragmentListaInstance: MutableSharedFlow<FragmentListaMedicamentos> = MutableSharedFlow(replay = 0)
    val getFragmentListaInstance: SharedFlow<FragmentListaMedicamentos> = _getFragmentListaInstance


    fun getFragmentListaInstance(fragmentListaMedicamentos: FragmentListaMedicamentos) {
        viewModelScope.launch {
            _getFragmentListaInstance.emit(fragmentListaMedicamentos)
        }

    }
}