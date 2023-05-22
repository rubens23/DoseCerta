package com.rubens.applembretemedicamento.framework.data.datastore.interfaces

import androidx.datastore.preferences.core.Preferences

interface ThemeDataStoreInterface {
    suspend fun passThemeToUserChosenTheme(intDataStore: Preferences.Key<Int>, intThemeResource: Int)
    suspend fun getThemeChosenByUser(intDataStore: Preferences.Key<Int>): Int
}