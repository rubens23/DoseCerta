package com.rubens.applembretemedicamento.framework.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BroadcastReceiverOnReceiveData(
    @PrimaryKey(autoGenerate = true)
    val idData: Int = 0,
    val idMedicamento: Int,
    val nomeMedicamento: String,
    val horaDose: String
)
