package com.roy.binaryeye.net

import java.io.UnsupportedEncodingException
import java.net.URLEncoder

fun String.urlEncode(): String = try {
	URLEncoder.encode(this, "UTF-8")
} catch (e: UnsupportedEncodingException) {
	this
}