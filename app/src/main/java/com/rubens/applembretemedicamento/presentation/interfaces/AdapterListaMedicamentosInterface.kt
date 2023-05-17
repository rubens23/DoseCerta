package com.rubens.applembretemedicamento.presentation.interfaces

import android.content.Context
import android.media.MediaPlayer

interface AdapterListaMedicamentosInterface {
    fun getListaIdMedicamentosFromAdapterListaMedicamentos(): ArrayList<Int>

    fun removeFromListaIdMedicamentosFromListaAdapter(id: Int)
    fun setMediaPlayerInstance(mp: MediaPlayer)


}