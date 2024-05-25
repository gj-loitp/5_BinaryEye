package com.mckimquyen.binaryeye

import android.app.Application
import com.mckimquyen.binaryeye.database.Db
import com.mckimquyen.binaryeye.pref.Pref

//TODO ad applovin
//TODO firebase

//TODO ic_launcher
//TODO keystore
//TODO app version
//TODO rate app
//TODO more app
//TODO share app
//TODO policy

//done mckimquyen
//ad id
//leak canary
//proguard

val db = Db()
val prefs = Pref()

class RApp : Application() {
    override fun onCreate() {
        super.onCreate()
        db.open(this)
        prefs.init(this)
    }
}
