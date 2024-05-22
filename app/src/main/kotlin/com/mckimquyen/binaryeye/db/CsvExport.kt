package com.mckimquyen.binaryeye.db

import android.content.Context
import android.database.Cursor
import com.mckimquyen.binaryeye.view.io.writeExternalFile
import java.io.ByteArrayOutputStream
import java.io.OutputStream

fun Context.exportCsv(
	name: String,
	cursor: Cursor,
	delimiter: String,
) = writeExternalFile(name, "text/csv") { outputStream ->
    exportCsv(outputStream, cursor, delimiter)
}

fun Cursor.exportCsv(delimiter: String): String {
    val outputStream = ByteArrayOutputStream()
    exportCsv(outputStream, this, delimiter)
    return outputStream.toString()
}

@OptIn(ExperimentalStdlibApi::class)
private fun exportCsv(
	outputStream: OutputStream,
	cursor: Cursor,
	delimiter: String,
) {
    if (!cursor.moveToFirst()) {
        return
    }
    val columns = arrayOf(
        Db.SCANS_DATETIME,
        Db.SCANS_FORMAT,
        Db.SCANS_CONTENT,
        Db.SCANS_ERROR_CORRECTION_LEVEL,
        Db.SCANS_VERSION,
        Db.SCANS_SEQUENCE_SIZE,
        Db.SCANS_SEQUENCE_INDEX,
        Db.SCANS_SEQUENCE_ID,
        Db.SCANS_GTIN_COUNTRY,
        Db.SCANS_GTIN_ADD_ON,
        Db.SCANS_GTIN_PRICE,
        Db.SCANS_GTIN_ISSUE_NUMBER
    )
    val indices = columns.map {
        cursor.getColumnIndex(it)
    }
    val contentIndex = cursor.getColumnIndex(Db.SCANS_CONTENT)
    val rawIndex = cursor.getColumnIndex(Db.SCANS_RAW)
    outputStream.write(
        columns.joinToString(
            delimiter,
            postfix = "\n"
        ).toByteArray()
    )
    do {
        var deviation: Pair<Int, String>? = null
        if (cursor.getString(contentIndex)?.isEmpty() == true) {
            deviation = Pair(
                contentIndex,
                cursor.getBlob(rawIndex).toHexString()
            )
        }
        outputStream.write(
            cursor.toCsvRecord(indices, delimiter, deviation)
        )
    } while (cursor.moveToNext())
}

private fun Cursor.toCsvRecord(
	indices: List<Int>,
	delimiter: String,
	deviation: Pair<Int, String>?,
): ByteArray {
    val sb = StringBuilder()
    indices.forEach {
        val value = if (deviation?.first == it) {
            deviation.second
        } else {
            this.getString(it)
        }
        sb.append(value?.quoteAndEscape() ?: "")
        sb.append(delimiter)
    }
    sb.append("\n")
    return sb.toString().toByteArray()
}

private fun String.quoteAndEscape() = "\"${
    this
        .replace("\n", " ")
        .replace("\"", "\"\"")
}\""
