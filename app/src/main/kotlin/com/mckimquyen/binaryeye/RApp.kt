package com.mckimquyen.binaryeye

import android.app.Application
import com.mckimquyen.binaryeye.database.Db
import com.mckimquyen.binaryeye.pref.Pref

//TODO ad applovin
//TODO firebase

//TODO keystore
//TODO policy
//TODO double tap to exit

//done mckimquyen
//ad id
//leak canary
//proguard
//ui switch
//ic_launcher
//app version
//rate app
//more app
//share app

val db = Db()
val prefs = Pref()

class RApp : Application() {
    override fun onCreate() {
        super.onCreate()
        db.open(this)
        prefs.init(this)
    }
}
