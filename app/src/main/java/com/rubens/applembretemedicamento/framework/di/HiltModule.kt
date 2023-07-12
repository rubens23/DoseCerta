package com.rubens.applembretemedicamento.framework.di

import android.content.Context
import android.os.Parcel
import android.text.format.DateFormat
import com.example.appmedicamentos.data.repository.AddMedicineRepositoryImpl
import com.example.appmedicamentos.data.repository.MedicationRepositoryImpl
import com.rubens.applembretemedicamento.framework.broadcastreceivers.AlarmReceiver
import com.rubens.applembretemedicamento.framework.data.AppDatabase
import com.rubens.applembretemedicamento.framework.data.daos.MedicamentoDao
import com.rubens.applembretemedicamento.framework.data.managers.RoomAccess
import com.rubens.applembretemedicamento.framework.data.managers.RoomAccessImpl
import com.rubens.applembretemedicamento.framework.data.roomdatasourcemanager.DataSourceManager
import com.rubens.applembretemedicamento.framework.domain.MedicamentoManager
import com.rubens.applembretemedicamento.framework.domain.doses.DosesManagerFormato12Horas
import com.rubens.applembretemedicamento.framework.domain.doses.DosesManagerFormato12HorasImpl
import com.rubens.applembretemedicamento.framework.domain.doses.DosesManagerFormato24HorasImpl
import com.rubens.applembretemedicamento.framework.domain.doses.DosesManagerInterface
import com.rubens.applembretemedicamento.framework.helpers.AlarmHelper
import com.rubens.applembretemedicamento.framework.helpers.AlarmHelperImpl
import com.rubens.applembretemedicamento.presentation.HiltTestActivity
import com.rubens.applembretemedicamento.presentation.MainActivity
import com.rubens.applembretemedicamento.presentation.interfaces.MainActivityInterface
import com.rubens.applembretemedicamento.utils.AlarmUtilsInterface
import com.rubens.applembretemedicamento.utils.CalendarHelper
import com.rubens.applembretemedicamento.utils.CalendarHelper2
import com.rubens.applembretemedicamento.utils.CalendarHelperImpl
import com.rubens.applembretemedicamento.utils.CalendarHelperImpl2
import com.rubens.applembretemedicamento.utils.FuncoesDeTempo
import com.rubens.applembretemedicamento.utils.FuncoesDeTempoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

    @Provides
    @Singleton
    fun provideDB(@ApplicationContext context: Context): AppDatabase? {
        return AppDatabase.getAppDatabase(context)
    }

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
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
    fun providesDosesManagerFormato12HorasImplementation(addMedicineRepository: AddMedicineRepositoryImpl): DosesManagerFormato12Horas{
        return DosesManagerFormato12HorasImpl(addMedicineRepository)
    }

    @Provides
    @Singleton
    fun providesIs24HourFormat(@ApplicationContext context: Context): Boolean{
        return DateFormat.is24HourFormat(context)
    }

    @Provides
    @Singleton
    fun providesRoomAccess(dao: MedicamentoDao): RoomAccess{
        return RoomAccessImpl(dao)
    }





    @Provides
    @Singleton
    fun providesDataSourceManagerInstance(): DataSourceManager{
        return DataSourceManager()
    }

    @Provides
    fun providesDosesManagerImplementation(): DosesManagerInterface{
        return DosesManagerFormato24HorasImpl()
    }



    @Provides
    @Singleton
    fun providesFuncoesDeTempo(): FuncoesDeTempo{
        return FuncoesDeTempoImpl()
    }

    @Provides
    @Singleton
    fun providesCalendarHelper(@ApplicationContext context: Context): CalendarHelper{
        return CalendarHelperImpl(context)
    }













    @Provides
    @Singleton
    fun provideAlarmReceiver(): AlarmReceiver{
        return AlarmReceiver()
    }





    @Provides
    @Singleton
    fun providesCalendarHelper2(): CalendarHelper2 {
        return CalendarHelperImpl2()
    }


    @Provides
    fun providesDeviceDefaultDateFormat(@ApplicationContext context: Context, calendarHelper: CalendarHelper): String{
        return calendarHelper.pegarFormatoDeDataPadraoDoDispositivoDoUsuario(context)
    }

    @Provides
    @Singleton
    fun providesAlarmHelper(roomAccess: RoomAccess, funcoesDeTempo: FuncoesDeTempo, calendarHelper: CalendarHelper, calendarHelper2: CalendarHelper2, @ApplicationContext context: Context, is24HourFormat: Boolean, deviceDefaultDateFormat: String): AlarmHelper{
        return AlarmHelperImpl(roomAccess, funcoesDeTempo, calendarHelper2, calendarHelper, context, is24HourFormat, deviceDefaultDateFormat)
    }

    @Provides
    @Singleton
    fun provideAlarmUtilsInterface(roomAccess: RoomAccess, funcoesDeTempo: FuncoesDeTempo, calendarHelper2: CalendarHelper2, calendarHelper: CalendarHelper, @ApplicationContext context: Context, is24HourFormat: Boolean, deviceDefaultDateFormat: String): AlarmUtilsInterface {
        return AlarmHelperImpl(roomAccess, funcoesDeTempo, calendarHelper2, calendarHelper, context, is24HourFormat, deviceDefaultDateFormat)
    }

    @Provides
    @Singleton
    fun providesMedicamentoManager(alarmReceiver: AlarmReceiver, @ApplicationContext context: Context, alarmHelper: AlarmHelper, calendarHelper: CalendarHelper, calendarHelper2: CalendarHelper2, is24HourFormat: Boolean, defaultDeviceDateFormat: String): MedicamentoManager{
        val parcel = Parcel.obtain()
        return MedicamentoManager(parcel, alarmReceiver, context, alarmHelper, calendarHelper, calendarHelper2, is24HourFormat, defaultDeviceDateFormat)
    }



    @Provides
    fun providesMainActivityInstance(): MainActivity{
        return MainActivity()
    }

    @Provides
    fun providesHiltTestActivityInstance(): HiltTestActivity {
        return HiltTestActivity()
    }

    @Provides
    fun provideMainActivityInterface(activity: MainActivity): MainActivityInterface{
        return activity
    }




}