package com.rubens.applembretemedicamento.framework.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ConfiguracoesEntity(
    @PrimaryKey
    val idConfiguracao: Int = 1,
    val podeTocarQuandoFechado: Boolean = true,
    val podeTocarDepoisDeReiniciar: Boolean = true
)
