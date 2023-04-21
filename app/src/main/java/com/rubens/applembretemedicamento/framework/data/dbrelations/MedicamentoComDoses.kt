package com.rubens.applembretemedicamento.framework.data.dbrelations

import androidx.room.Embedded
import androidx.room.Relation
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import java.io.Serializable

data class MedicamentoComDoses(
    @Embedded val medicamentoTratamento: MedicamentoTratamento,
    @Relation(
        parentColumn = "nomeMedicamento",
        entityColumn = "nomeMedicamento"
    )
    val listaDoses: List<Doses>
): Serializable
