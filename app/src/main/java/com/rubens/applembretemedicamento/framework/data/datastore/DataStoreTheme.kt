package com.rubens.applembretemedicamento.framework.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.framework.data.datastore.interfaces.ThemeDataStoreInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "theme_data_store")

class DataStoreTheme(context: Context): ThemeDataStoreInterface {

    private val dataStore = context.dataStore

    override suspend fun passThemeToUserChosenTheme(intDataStore: Preferences.Key<Int>, intThemeResource: Int){
        dataStore.edit {
            preferences->
            preferences[intDataStore] = intThemeResource
        }
    }

    override suspend fun getThemeChosenByUser(intDataStore: Preferences.Key<Int>): Int{
        val preferences = dataStore.data.first()
        return preferences[intDataStore]?: R.style.Theme_AppLembreteMedicamento
    }



}