package com.jaresinunez.booklet

import android.app.Application
import com.jaresinunez.booklet.databasestuff.AppDatabase

class ItemApplication: Application() {
    val db by lazy { AppDatabase.getInstance(this) }
}