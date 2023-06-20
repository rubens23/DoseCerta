package com.rubens.applembretemedicamento.framework.data.roomconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rubens.applembretemedicamento.framework.data.entities.Doses

class DosesConverter {
    @TypeConverter
    fun fromDosesList(doses: List<Doses>): String{
        val gson = Gson()
        return gson.toJson(doses)
    }

    @TypeConverter
    fun toDosesList(dosesString: String): List<Doses>{
        val gson = Gson()
        val listType = object : TypeToken<List<Doses>>() {}.type
        return gson.fromJson(dosesString, listType)

    }
}