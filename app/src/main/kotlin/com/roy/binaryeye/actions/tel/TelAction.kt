package com.roy.binaryeye.actions.tel

import android.content.Context
import android.content.Intent
import com.roy.binaryeye.R
import com.roy.binaryeye.actions.IntentAction
import com.roy.binaryeye.content.parseAndNormalizeUri

object TelAction : IntentAction() {
    private val telRegex = """^tel:(\+?[0-9]+)$""".toRegex(
        RegexOption.IGNORE_CASE
    )

    override val iconResId: Int = R.drawable.ic_action_tel
    override val titleResId: Int = R.string.tel_dial
    override val errorMsg: Int = R.string.tel_error

    override fun canExecuteOn(data: ByteArray): Boolean {
        return String(data).matches(telRegex)
    }

    override suspend fun createIntent(
		context: Context,
		data: ByteArray,
	): Intent = Intent(
        Intent.ACTION_DIAL,
        String(data).parseAndNormalizeUri()
    )
}
