package com.mckimquyen.binaryeye.view.actions.otpauth

import com.mckimquyen.binaryeye.R
import com.mckimquyen.binaryeye.view.actions.SchemeAction

object OtpauthAction : SchemeAction() {
    override val iconResId: Int = R.drawable.ic_action_otpauth
    override val titleResId: Int = R.string.otpauth_add
    override val scheme: String = "otpauth"
}
