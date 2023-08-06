package com.roy.binaryeye.actions.otpauth

import com.roy.binaryeye.R
import com.roy.binaryeye.actions.SchemeAction

object OtpauthAction : SchemeAction() {
	override val iconResId: Int = R.drawable.ic_action_otpauth
	override val titleResId: Int = R.string.otpauth_add
	override val scheme: String = "otpauth"
}
