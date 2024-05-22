package com.mckimquyen.binaryeye

import android.app.Application
import com.mckimquyen.binaryeye.db.Db
import com.mckimquyen.binaryeye.preference.Preferences

//TODO ad applovin
//TODO firebase
//TODO ic_launcher
//TODO ad id
//TODO leak canary
//TODO proguard
//TODO keystore
//TODO app version
//TODO rate app
//TODO more app
//TODO share app
//TODO policy

//done mckimquyen

val db = Db()
val prefs = Preferences()

class BinaryEyeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        db.open(this)
        prefs.init(this)
    }
}
