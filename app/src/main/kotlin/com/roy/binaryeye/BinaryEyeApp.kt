package com.roy.binaryeye

import android.app.Application
import com.roy.binaryeye.db.Db
import com.roy.binaryeye.preference.Preferences

//TODO ad applovin
//TODO firebase

//done
//ic_launcher
//ad id
//leak canary
//proguard
//keystore
//app version
//rate app
//more app
//share app
//policy

val db = Db()
val prefs = Preferences()

class BinaryEyeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        db.open(this)
        prefs.init(this)
    }
}
