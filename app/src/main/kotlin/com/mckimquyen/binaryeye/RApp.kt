package com.mckimquyen.binaryeye

import android.app.Application
import com.mckimquyen.binaryeye.database.Db
import com.mckimquyen.binaryeye.pref.Pref

//TODO ad applovin
//TODO firebase

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
//ui switch
//ic_launcher

val db = Db()
val prefs = Pref()

class RApp : Application() {
    override fun onCreate() {
        super.onCreate()
        db.open(this)
        prefs.init(this)
    }
}
