package com.rubens.applembretemedicamento.framework.data.roommigrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class MigrationFrom1To2: Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE MedicamentoTratamento ADD COLUMN colunaTeste TEXT DEFAULT '' NOT NULL")
    }
}