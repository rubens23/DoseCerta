package com.rubens.applembretemedicamento.framework.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Doses(
    @PrimaryKey(autoGenerate = true)
    val idDose: Int = 0,
    val nomeMedicamento: String,
    val horarioDose: String,
    val intervaloEntreDoses: Double,
    var dataHora: String? = null,
    val qntDosesPorHorario: Int = 1,
    val jaTomouDose: Boolean,
    val jaMostrouToast: Boolean = false
): Serializable{


}