package com.rubens.applembretemedicamento.framework.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class MedicamentoTratamento(
    val nomeMedicamento: String,
    val horaPrimeiraDose: String,
    val qntDoses: Int,
    @PrimaryKey(autoGenerate = true)
    val idMedicamento: Int = 0,
    var alarmeAtivado: Boolean = false,
    val num_doses_num_unico_horario: Int = 1,
    val totalDiasTratamento: Int,
    val diasRestantesDeTratamento: Int,
    val tratamentoFinalizado: Boolean,
    val dataInicioTratamento: String,
    val dataTerminoTratamento: String,
    val stringDataStore: String,
    var alarmeTocando: Boolean = false,
    var colunaTeste: String = ""
): Serializable