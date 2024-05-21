package com.mckimquyen.binaryeye.actions

import com.mckimquyen.binaryeye.actions.mail.MailAction
import com.mckimquyen.binaryeye.actions.mail.MatMsgAction
import com.mckimquyen.binaryeye.actions.otpauth.OtpauthAction
import com.mckimquyen.binaryeye.actions.search.OpenOrSearchAction
import com.mckimquyen.binaryeye.actions.sms.SmsAction
import com.mckimquyen.binaryeye.actions.tel.TelAction
import com.mckimquyen.binaryeye.actions.vtype.vcard.VCardAction
import com.mckimquyen.binaryeye.actions.vtype.vevent.VEventAction
import com.mckimquyen.binaryeye.actions.web.WebAction
import com.mckimquyen.binaryeye.actions.wifi.WifiAction

object ActionRegistry {
    val DEFAULT_ACTION: IAction = OpenOrSearchAction

    private val REGISTRY: Set<IAction> = setOf(
        MailAction,
        MatMsgAction,
        OtpauthAction,
        SmsAction,
        TelAction,
        VCardAction,
        VEventAction,
        WifiAction,
        // Try WebAction last because recognizing colloquial URLs is
        // very aggressive.
        WebAction
    )

    fun getAction(data: ByteArray): IAction = REGISTRY.find {
        it.canExecuteOn(data)
    } ?: DEFAULT_ACTION
}
