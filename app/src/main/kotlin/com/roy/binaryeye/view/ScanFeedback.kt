package com.roy.binaryeye.view

import android.content.Context
import android.media.AudioManager
import com.roy.binaryeye.app.prefs
import com.roy.binaryeye.media.beepConfirm
import com.roy.binaryeye.media.beepError
import com.roy.binaryeye.os.error
import com.roy.binaryeye.os.getVibrator
import com.roy.binaryeye.os.vibrate

fun Context.scanFeedback() {
	if (prefs.vibrate) {
		getVibrator().vibrate()
	}
	if (prefs.beep && !isSilent()) {
		beepConfirm()
	}
}

fun Context.errorFeedback() {
	if (prefs.vibrate) {
		getVibrator().error()
	}
	if (prefs.beep && !isSilent()) {
		beepError()
	}
}

private fun Context.isSilent(): Boolean {
	val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
	return when (am.ringerMode) {
		AudioManager.RINGER_MODE_SILENT,
		AudioManager.RINGER_MODE_VIBRATE -> true
		else -> false
	}
}
