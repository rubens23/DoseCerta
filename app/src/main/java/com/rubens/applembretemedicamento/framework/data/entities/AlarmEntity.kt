package com.rubens.applembretemedicamento.framework.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.rubens.applembretemedicamento.framework.data.roomconverters.DosesConverter

@Entity
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val idAlarme: Int = 0,
    val idMedicamento: Int,
    val horaProxDose: String,
    val nomeMedicamento: String,
    val alarmActive: Boolean,
    val intervaloEntreDoses: Double,
    @TypeConverters(DosesConverter::class)
    val listaDoses: List<Doses>
)
