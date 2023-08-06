package com.roy.binaryeye.graphics

import android.graphics.Bitmap
import android.graphics.Canvas

fun Bitmap.fixTransparency(): Bitmap {
    val result = analyzeTransparency()
    return if (result.hasTransparentPixels) {
        val copy = Bitmap.createBitmap(
            /* width = */ width,
            /* height = */ height,
            /* config = */ config
        )
        Canvas(copy).apply {
            drawColor(result.backgroundColor)
            drawBitmap(
                /* bitmap = */ this@fixTransparency,
                /* left = */ 0f,
                /* top = */ 0f,
                /* paint = */ null
            )
        }
        copy
    } else {
        this
    }
}

private fun Bitmap.analyzeTransparency(): Result {
    val pixels = IntArray(width * height)
    getPixels(pixels, 0, width, 0, 0, width, height)
    var transparent = 0
    var visible = 0
    var bright = 0
    for (pixel in pixels) {
        val alpha = (pixel shr 24) and 0xff
        if (alpha < 0xff) {
            ++transparent
        }
        if (alpha > 0) {
            ++visible
            if (
                ((pixel shr 16) and 0xff) +
                ((pixel shr 8) and 0xff) +
                (pixel and 0xff) > 128
            ) ++bright
        }
    }
    return Result(
        transparent > 0,
        if (bright > 0 && visible / bright < 2) {
            COLOR_BLACK
        } else {
            COLOR_WHITE
        }
    )
}

private data class Result(
    val hasTransparentPixels: Boolean,
    val backgroundColor: Int
)
