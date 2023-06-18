package com.rubens.applembretemedicamento.framework.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActivityHostAndFragmentConfikguracoesSharedViewModel: ViewModel() {


    private val _getMudouTema: MutableLiveData<Boolean> = MutableLiveData()
    val getMudouTema: LiveData<Boolean> = _getMudouTema
    private val _temaAtual: MutableLiveData<String> = MutableLiveData()
    val temaAtual: LiveData<String> = _temaAtual

    fun mudouTema(mudou: Boolean) {
        _getMudouTema.value = mudou

    }

    fun atualizarTema(tema: String) {
        _temaAtual.value = tema
    }
}