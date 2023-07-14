package com.rubens.applembretemedicamento.di

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
object TestAppModule {

    @Provides
    @Named("db")
    fun provideDB(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getAppDatabaseTest(context)
    }

    @Provides
    @Named("context")
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }



    @Provides
    @Named("medicamentoDao")
    fun provideMedicamentosDao(db: AppDatabase?): MedicamentoDao {
        return db!!.medicamentoDao
    }



    @Provides
    @Named("medicamentoRepository")
    fun providesMedicamentoRepository(dao: MedicamentoDao): MedicationRepositoryImpl {
        return MedicationRepositoryImpl(dao)
    }

    @Provides
    @Named("addmedicamentosrepository")
    fun providesAddMedicamentosRepository(dao: MedicamentoDao): AddMedicineRepositoryImpl{
        return AddMedicineRepositoryImpl(dao)
    }

    @Provides
    @Named("dosesManager12horas")
    fun providesDosesManagerFormato12HorasImplementation(addMedicamentosRepositoryImpl: AddMedicineRepositoryImpl): DosesManagerFormato12Horas {
        return DosesManagerFormato12HorasImpl(addMedicamentosRepositoryImpl)
    }



    @Provides
    @Named("is24hour")
    fun providesIs24HourFormat(@ApplicationContext context: Context): Boolean{
        return DateFormat.is24HourFormat(context)
    }






    @Provides
    @Named("roomAccess")
    fun providesRoomAccess(dao: MedicamentoDao): RoomAccess {
        return RoomAccessImpl(dao)
    }





    @Provides
    @Named("dataSourceManager")
    fun providesDataSourceManagerInstance(): DataSourceManager {
        return DataSourceManager()
    }


    @Provides
    @Named("dosesManager")
    fun providesDosesManagerImplementation(): DosesManagerInterface {
        return DosesManagerFormato24HorasImpl()
    }







    @Provides
    @Named("funcoesTempo")
    fun providesFuncoesDeTempo(): FuncoesDeTempo {
        return FuncoesDeTempoImpl()
    }



    @Provides
    @Named("calendarHelper")
    fun providesCalendarHelper(@ApplicationContext context: Context): CalendarHelper {
        return CalendarHelperImpl(context)
    }










    @Provides
    @Named("alarmReceiver")
    fun provideAlarmReceiver(): AlarmReceiver {
        return AlarmReceiver()
    }

    @Provides
    @Named("defaultdate")
    fun providesDeviceDefaultDateFormat(@ApplicationContext context: Context, calendarHelper: CalendarHelper): String{
        return calendarHelper.pegarFormatoDeDataPadraoDoDispositivoDoUsuario(context)
    }

    @Provides
    @Named("alarmHelper")
    fun providesAlarmHelper(roomAccess: RoomAccess, funcoesDeTempo: FuncoesDeTempo, calendarHelper: CalendarHelper, calendarHelper2: CalendarHelper2, @ApplicationContext context: Context, is24HourFormat: Boolean, deviceDefaultDateFormat: String): AlarmHelper {
        return AlarmHelperImpl(roomAccess, funcoesDeTempo, calendarHelper2, calendarHelper, context, is24HourFormat, deviceDefaultDateFormat)
    }

    @Provides
    @Named("alarmUtils")
    fun provideAlarmUtilsInterface(roomAccess: RoomAccess, funcoesDeTempo: FuncoesDeTempo, calendarHelper2: CalendarHelper2, calendarHelper: CalendarHelper, @ApplicationContext context: Context, is24HourFormat: Boolean, deviceDefaultDateFormat: String): AlarmUtilsInterface {
        return AlarmHelperImpl(roomAccess, funcoesDeTempo, calendarHelper2, calendarHelper, context, is24HourFormat, deviceDefaultDateFormat)
    }

    @Provides
    @Named("calendarHelper2")
    fun providesCalendarHelper2(): CalendarHelper2 {
        return CalendarHelperImpl2()
    }


    @Provides
    @Named("medicamentoManager")
    fun providesMedicamentoManager(alarmReceiver: AlarmReceiver, @ApplicationContext context: Context, alarmHelper: AlarmHelper, calendarHelper: CalendarHelper, calendarHelper2: CalendarHelper2, is24HourFormat: Boolean, deviceDefaultDateFormat: String): MedicamentoManager {
        val parcel = Parcel.obtain()
        return MedicamentoManager(parcel, alarmReceiver, context, alarmHelper, calendarHelper, calendarHelper2, is24HourFormat, deviceDefaultDateFormat)
    }

    @Provides
    @Named("hilttestactivity")
    fun providesHiltTestActivityInstance(): HiltTestActivity{
        return HiltTestActivity()
    }





}