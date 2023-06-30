package com.rubens.applembretemedicamento.framework.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.data.entities.AlarmEntity
import com.rubens.applembretemedicamento.framework.data.entities.BroadcastReceiverOnReceiveData
import com.rubens.applembretemedicamento.framework.data.entities.ConfiguracoesEntity
import com.rubens.applembretemedicamento.framework.data.entities.Doses
import com.rubens.applembretemedicamento.framework.data.entities.HistoricoMedicamentos
import com.rubens.applembretemedicamento.framework.data.entities.MedicamentoTratamento
import com.rubens.applembretemedicamento.framework.data.roomconverters.DosesConverter
import com.rubens.applembretemedicamento.framework.data.roommigrations.MigrationFrom1To2

@Database(
    entities = [MedicamentoTratamento::class,
        Doses::class,
        HistoricoMedicamentos::class,
        BroadcastReceiverOnReceiveData::class,
        AlarmEntity::class,
        ConfiguracoesEntity::class],
    version = 1
)
@TypeConverters(DosesConverter::class)
abstract class AppDatabase : RoomDatabase() {


    abstract val medicamentoDao: MedicamentoDao

    companion object {

        private var INSTANCE: AppDatabase? = null
        private var TEST_INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = MigrationFrom1To2()


        fun getAppDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {

                INSTANCE = Room.databaseBuilder<AppDatabase>(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "banco-app-medicamentos"
                )
                    .addMigrations(MIGRATION_1_2)
                    .allowMainThreadQueries().build()
            }
            return INSTANCE
        }

        fun getAppDatabaseTest(context: Context): AppDatabase {
            if (TEST_INSTANCE == null) {

                TEST_INSTANCE = Room.inMemoryDatabaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java
                )
                    .allowMainThreadQueries().build()
                Log.d("nullinstance", "o test instance recebeu a instancia do room")
            }
            return TEST_INSTANCE!!
        }



    }
}