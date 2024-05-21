package com.mckimquyen.binaryeye.actions

import android.content.Context
import android.content.Intent
import com.mckimquyen.binaryeye.content.execShareIntent
import com.mckimquyen.binaryeye.content.openUrl
import com.mckimquyen.binaryeye.widget.toast

interface IAction {
    val iconResId: Int
    val titleResId: Int

    fun canExecuteOn(data: ByteArray): Boolean
    suspend fun execute(context: Context, data: ByteArray)
}

abstract class IntentAction : IAction {
    abstract val errorMsg: Int

    final override suspend fun execute(
        context: Context,
        data: ByteArray,
    ) {
        val intent = createIntent(context, data)
        if (intent == null) {
            context.toast(errorMsg)
        } else {
            context.execShareIntent(intent)
        }
    }

    abstract suspend fun createIntent(
        context: Context,
        data: ByteArray,
    ): Intent?
}

abstract class SchemeAction : IAction {
    abstract val scheme: String
    open val buildRegex: Boolean = false

    final override fun canExecuteOn(data: ByteArray): Boolean {
        val content = String(data)
        return if (buildRegex) {
            content.matches(
                """^$scheme://[\w\W]+$""".toRegex(
                    RegexOption.IGNORE_CASE
                )
            )
        } else {
            content.startsWith("$scheme://", ignoreCase = true)
        }
    }

    final override suspend fun execute(
        context: Context,
        data: ByteArray,
    ) {
        context.openUrl(String(data))
    }
}
