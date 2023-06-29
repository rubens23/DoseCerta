package com.rubens.applembretemedicamento.framework.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rubens.applembretemedicamento.R

@Entity
data class ConfiguracoesEntity(
    @PrimaryKey
    val idConfiguracao: Int = 1,
    val podeTocarDepoisDeReiniciar: Boolean = true,
    val colorResource: Int = R.color.rosa_salmao
)
