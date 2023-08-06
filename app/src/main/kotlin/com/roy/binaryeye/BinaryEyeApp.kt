package com.roy.binaryeye

import android.app.Application
import com.roy.binaryeye.db.Db
import com.roy.binaryeye.preference.Preferences


//TODO firebase
//TODO animation
//TODO rate app
//TODO more app
//TODO share app
//TODO policy
//TODO proguard
//TODO app version
//TODO leak canary
//TODO keystore
//TODO ad id
//TODO ad applovin

//done
//ic_launcher

val db = Db()
val prefs = Preferences()

class BinaryEyeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        db.open(this)
        prefs.init(this)
    }
}
