package com.rubens.applembretemedicamento.presentation.interfaces

import android.content.Context

interface AdapterListaMedicamentosInterface {
    fun getListaIdMedicamentosFromAdapterListaMedicamentos(): ArrayList<Int>

    fun removeFromListaIdMedicamentosFromListaAdapter(id: Int)


}