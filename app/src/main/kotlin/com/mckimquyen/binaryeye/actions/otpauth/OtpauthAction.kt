package com.mckimquyen.binaryeye.actions.otpauth

import com.mckimquyen.binaryeye.R
import com.mckimquyen.binaryeye.actions.SchemeAction

object OtpauthAction : SchemeAction() {
    override val iconResId: Int = R.drawable.ic_action_otpauth
    override val titleResId: Int = R.string.otpauth_add
    override val scheme: String = "otpauth"
}
