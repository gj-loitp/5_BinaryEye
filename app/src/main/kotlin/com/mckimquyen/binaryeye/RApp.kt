package com.mckimquyen.binaryeye

import android.app.Application
import com.mckimquyen.binaryeye.database.Db
import com.mckimquyen.binaryeye.pref.Pref

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
val prefs = Pref()

class BinaryEyeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        db.open(this)
        prefs.init(this)
    }
}
