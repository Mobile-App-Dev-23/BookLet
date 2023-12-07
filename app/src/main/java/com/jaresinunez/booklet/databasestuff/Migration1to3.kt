package com.jaresinunez.booklet.databasestuff

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val Migration1to3 = object : Migration(1, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Migration logic here
    }
}
