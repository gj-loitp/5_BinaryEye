package com.roy.binaryeye.content

fun ByteArray.toHexString() = joinToString("") { "%02X".format(it) }
