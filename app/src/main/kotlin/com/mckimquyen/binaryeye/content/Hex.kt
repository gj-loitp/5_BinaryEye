package com.mckimquyen.binaryeye.content

fun ByteArray.toHexString() = joinToString("") { "%02X".format(it) }
