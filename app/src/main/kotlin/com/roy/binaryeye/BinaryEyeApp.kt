package com.roy.binaryeye

import android.app.Application
import com.roy.binaryeye.db.Db
import com.roy.binaryeye.preference.Preferences

val db = Db()
val prefs = Preferences()

class BinaryEyeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        db.open(this)
        prefs.init(this)
    }
}
