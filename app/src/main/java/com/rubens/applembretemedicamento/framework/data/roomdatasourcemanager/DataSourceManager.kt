package com.rubens.applembretemedicamento.framework.data.roomdatasourcemanager

import android.content.Context
import com.example.appmedicamentos.data.repository.MedicationRepositoryImpl
import com.rubens.applembretemedicamento.BuildConfig
import com.rubens.applembretemedicamento.framework.data.dbrelations.MedicamentoComDoses
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataSourceManager {


    /*


        fun getDataSource(medicationRepository: MedicationRepositoryImpl): List<MedicamentoComDoses>? {
            // Verifica se a build variant é debug
            if(BuildConfig.DEBUG){
                // Retorna dados fake
                return FakeMedicamentoData.medicamentosComDoses
            }
            else{
                // Retorna dados do Room
                return medicationRepository.getMedicamentos()
            }

    }

     */
}