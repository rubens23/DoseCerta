package com.rubens.applembretemedicamento.framework.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore(name = "my_data_store")

class MyDataStore(context: Context) {

    private val dataStore = context.dataStore


    companion object{
        private val HAS_CLOSED_BEFORE = booleanPreferencesKey("has_closed_before")
    }

    suspend fun hasToastAlreadyShown(stringDataStore: Preferences.Key<Boolean>): Boolean {
        val preferences = dataStore.data.first()
        return preferences[stringDataStore]?: false
    }

    suspend fun markToastAsShown(stringDataStore: Preferences.Key<Boolean>){
        dataStore.edit {
            preferences->
            preferences[stringDataStore] = true
        }
    }

    suspend fun markToastAsNotShown(stringDataStore: Preferences.Key<Boolean>){
        dataStore.edit {
            preferences->
            preferences[stringDataStore] = false
        }
    }

    suspend fun clearToastShownFlagIfNeeded(){
        val preferences = dataStore.data.first()
        val hasClosedBefore = preferences[HAS_CLOSED_BEFORE]?: false

        if(hasClosedBefore){
            dataStore.edit {
                //it.remove(TOAST_ALREADY_SHOWN)
            }
        }
        dataStore.edit { it[HAS_CLOSED_BEFORE] = true }
    }

    suspend fun deleteDataStoreByKey(stringDataStore: Preferences.Key<Boolean>){
        dataStore.edit {
            it.remove(stringDataStore)
        }
    }



}