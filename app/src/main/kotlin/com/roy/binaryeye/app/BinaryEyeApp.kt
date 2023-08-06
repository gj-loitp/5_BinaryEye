package com.roy.binaryeye.app

import android.app.Application
import com.roy.binaryeye.database.Database
import com.roy.binaryeye.preference.Preferences

val db = Database()
val prefs = Preferences()

class BinaryEyeApp : Application() {
	override fun onCreate() {
		super.onCreate()
		db.open(this)
		prefs.init(this)
	}
}
