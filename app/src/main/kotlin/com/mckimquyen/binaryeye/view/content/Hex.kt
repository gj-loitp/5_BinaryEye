package com.mckimquyen.binaryeye.view.content

fun ByteArray.toHexString() = joinToString("") { "%02X".format(it) }
