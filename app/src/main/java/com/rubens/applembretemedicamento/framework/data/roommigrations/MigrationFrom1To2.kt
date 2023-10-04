package com.rubens.applembretemedicamento.framework.data.roommigrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class MigrationFrom1To2: Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE ConfiguracoesEntity ADD COLUMN podeTocarSom INTEGER NOT NULL DEFAULT 1")
    }
}