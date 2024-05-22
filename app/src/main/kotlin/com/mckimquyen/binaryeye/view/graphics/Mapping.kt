package com.mckimquyen.binaryeye.view.graphics

import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import de.markusfisch.android.zxingcpp.ZxingCpp.Position
import kotlin.math.roundToInt

data class FrameMetrics(
    var width: Int = 0,
    var height: Int = 0,
    var orientation: Int = 0,
)

fun Rect.setFrameRoi(
    frameMetrics: FrameMetrics,
    viewRect: Rect,
    viewRoi: Rect,
) {
    Matrix().apply {
        // Map ROI from view coordinates to frame coordinates.
        setTranslate(
            /* dx = */ -viewRect.left.toFloat(),
            /* dy = */ -viewRect.top.toFloat()
        )
        postScale(
            /* sx = */ 1f / viewRect.width(),
            /* sy = */ 1f / viewRect.height()
        )
        postRotate(
            /* degrees = */ -frameMetrics.orientation.toFloat(),
            /* px = */ .5f,
            /* py = */ .5f
        )
        postScale(
            /* sx = */ frameMetrics.width.toFloat(),
            /* sy = */ frameMetrics.height.toFloat()
        )
        val frameRoiF = RectF()
        val viewRoiF = RectF(
            /* left = */ viewRoi.left.toFloat(),
            /* top = */ viewRoi.top.toFloat(),
            /* right = */ viewRoi.right.toFloat(),
            /* bottom = */ viewRoi.bottom.toFloat()
        )
        mapRect(frameRoiF, viewRoiF)
        set(
            /* left = */ frameRoiF.left.roundToInt(),
            /* top = */ frameRoiF.top.roundToInt(),
            /* right = */ frameRoiF.right.roundToInt(),
            /* bottom = */ frameRoiF.bottom.roundToInt()
        )
    }
}

fun Matrix.setFrameToView(
    frameMetrics: FrameMetrics,
    viewRect: Rect,
    viewRoi: Rect? = null,
) {
    // Configure this matrix to map points in frame coordinates to
    // view coordinates.
    val uprightWidth: Int
    val uprightHeight: Int
    when (frameMetrics.orientation) {
        90, 270 -> {
            uprightWidth = frameMetrics.height
            uprightHeight = frameMetrics.width
        }

        else -> {
            uprightWidth = frameMetrics.width
            uprightHeight = frameMetrics.height
        }
    }
    setScale(
        /* sx = */ viewRect.width().toFloat() / uprightWidth,
        /* sy = */ viewRect.height().toFloat() / uprightHeight
    )
    viewRoi?.let {
        postTranslate(/* dx = */ viewRoi.left.toFloat(), /* dy = */ viewRoi.top.toFloat())
    }
}

fun Matrix.mapPosition(
    position: Position,
    coords: FloatArray,
): Int {
    var i = 0
    setOf(
        position.topLeft,
        position.topRight,
        position.bottomRight,
        position.bottomLeft
    ).forEach {
        coords[i++] = it.x.toFloat()
        coords[i++] = it.y.toFloat()
    }
    mapPoints(coords)
    return i
}
