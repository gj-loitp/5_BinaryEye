package com.mckimquyen.binaryeye.sv

import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.mckimquyen.binaryeye.a.CameraActivity

@RequiresApi(Build.VERSION_CODES.N)
class ScanTileService : TileService() {
    override fun onClick() {
        super.onClick()
        val intent = Intent(applicationContext, CameraActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivityAndCollapse(intent)
    }
}
