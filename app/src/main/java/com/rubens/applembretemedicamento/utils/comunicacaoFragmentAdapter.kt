package com.rubens.applembretemedicamento.utils

interface comunicacaoFragmentAdapter {

    fun fecharFragment()

    fun mostrarToastExcluido(nome: String)

    fun verificarSeDataJaPassou(dataFinalizacao: String): Boolean
}