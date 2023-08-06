package com.roy.binaryeye.widget

import android.content.Context
import android.widget.Toast

fun Context.toast(message: Int) = Toast.makeText(
	/* context = */ applicationContext,
	/* resId = */ message,
	/* duration = */ Toast.LENGTH_LONG
).show()

fun Context.toast(message: String) = Toast.makeText(
	/* context = */ applicationContext,
	/* text = */ message.ellipsize(128),
	/* duration = */ Toast.LENGTH_LONG
).show()

private fun String.ellipsize(max: Int) = if (length < max) {
    this
} else {
    "${take(max)}…"
}
