package com.rubens.applembretemedicamento.framework.di

import android.content.Context
import com.example.appmedicamentos.data.repository.AddMedicineRepositoryImpl
import com.example.appmedicamentos.data.repository.MedicationRepositoryImpl
import com.rubens.applembretemedicamento.framework.data.AppDatabase
import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.data.roomdatasourcemanager.DataSourceManager
import com.rubens.applembretemedicamento.framework.domain.doses.DosesManager
import com.rubens.applembretemedicamento.framework.domain.doses.DosesManagerInterface
import com.rubens.applembretemedicamento.presentation.FragmentListaMedicamentos
import com.rubens.applembretemedicamento.presentation.MainActivity
import com.rubens.applembretemedicamento.presentation.interfaces.MainActivityInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

    @Provides
    @Singleton
    fun provideDB(@ApplicationContext context: Context): AppDatabase? {
        return AppDatabase.getAppDatabase(context)
    }

    @Singleton
    @Provides
    fun provideMedicamentosDao(db: AppDatabase?): MedicamentoDao {
        return db!!.medicamentoDao
    }

    @Provides
    @Singleton
    fun providesMedicamentoRepository(dao: MedicamentoDao): MedicationRepositoryImpl {
        return MedicationRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun providesAddMedicamentosRepository(dao: MedicamentoDao): AddMedicineRepositoryImpl{
        return AddMedicineRepositoryImpl(dao)
    }





    @Provides
    @Singleton
    fun providesDataSourceManagerInstance(): DataSourceManager{
        return DataSourceManager()
    }

    @Provides
    fun providesDosesManagerImplementation(): DosesManagerInterface{
        return DosesManager()
    }



}