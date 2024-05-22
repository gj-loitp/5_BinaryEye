package com.mckimquyen.binaryeye.view.graphics

import android.content.Context
import android.graphics.DashPathEffect
import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.mckimquyen.binaryeye.R

fun Context.getDashedBorderPaint() = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    val dp = resources.displayMetrics.density
    color = ContextCompat.getColor(applicationContext, R.color.crop_bound)
    style = Paint.Style.STROKE
    strokeWidth = dp * 2f
    pathEffect = DashPathEffect(floatArrayOf(10f * dp, 10f * dp), 0f)
}
