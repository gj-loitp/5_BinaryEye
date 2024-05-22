package com.mckimquyen.binaryeye.view.graphics

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build

fun Resources.getBitmapFromDrawable(
    resId: Int,
): Bitmap = getBitmapFromDrawable(getDrawableCompat(resId))

fun Resources.getDrawableCompat(
    resId: Int,
): Drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    getDrawable(resId, null)
} else {
    @Suppress("DEPRECATION")
    getDrawable(resId)
}

private fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
    if (drawable is BitmapDrawable) {
        return drawable.bitmap
    }
    val bitmap = Bitmap.createBitmap(
        /* width = */ drawable.intrinsicWidth,
        /* height = */ drawable.intrinsicHeight,
        /* config = */ Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(
        /* left = */ 0,
        /* top = */ 0,
        /* right = */ canvas.width,
        /* bottom = */ canvas.height
    )
    drawable.draw(canvas)
    return bitmap
}
