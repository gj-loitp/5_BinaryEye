package com.roy.binaryeye

import android.app.Application
import com.roy.binaryeye.db.Db
import com.roy.binaryeye.preference.Preferences

//TODO ad applovin
//TODO firebase

//TODO rate app
//TODO more app
//TODO share app
//TODO policy

//done
//ic_launcher
//ad id
//leak canary
//proguard
//keystore
//app version

val db = Db()
val prefs = Preferences()

class BinaryEyeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        db.open(this)
        prefs.init(this)
    }
}
