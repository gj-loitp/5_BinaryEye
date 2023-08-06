package com.roy.binaryeye.actions

import com.roy.binaryeye.actions.mail.MailAction
import com.roy.binaryeye.actions.mail.MatMsgAction
import com.roy.binaryeye.actions.otpauth.OtpauthAction
import com.roy.binaryeye.actions.search.OpenOrSearchAction
import com.roy.binaryeye.actions.sms.SmsAction
import com.roy.binaryeye.actions.tel.TelAction
import com.roy.binaryeye.actions.vtype.vcard.VCardAction
import com.roy.binaryeye.actions.vtype.vevent.VEventAction
import com.roy.binaryeye.actions.web.WebAction
import com.roy.binaryeye.actions.wifi.WifiAction

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
