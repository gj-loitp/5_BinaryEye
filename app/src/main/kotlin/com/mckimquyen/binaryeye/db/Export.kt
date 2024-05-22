package com.mckimquyen.binaryeye.db

import android.app.Activity
import android.os.Environment
import com.mckimquyen.binaryeye.view.io.writeExternalFile
import java.io.File
import java.io.FileInputStream

fun Activity.exportDatabase(fileName: String): Boolean {
    val dbFile = File(
        Environment.getDataDirectory(),
        "//data//${packageName}//databases//${Db.FILE_NAME}"
    )
    if (!dbFile.exists()) {
        return false
    }
    return writeExternalFile(
        fileName,
        "application/vnd.sqlite3"
    ) {
        FileInputStream(dbFile).copyTo(it)
    }
}
