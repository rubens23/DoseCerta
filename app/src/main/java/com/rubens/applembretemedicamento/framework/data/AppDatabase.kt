package com.rubens.applembretemedicamento.framework.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.entities.HistoricoMedicamentos
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento

@Database(
    entities = [MedicamentoTratamento::class,
        Doses::class,
        HistoricoMedicamentos::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    //todo: resolver esse erro do n reconhecimento da entity


    abstract val medicamentoDao: MedicamentoDao

    companion object {

        private var INSTANCE: AppDatabase? = null


        fun getAppDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {

                INSTANCE = Room.databaseBuilder<AppDatabase>(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "banco-app-medicamentos"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration().build()
            }
            return INSTANCE
        }

    }
    }