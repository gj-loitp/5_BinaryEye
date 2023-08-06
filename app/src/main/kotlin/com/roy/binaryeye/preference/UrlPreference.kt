package com.roy.binaryeye.preference

import android.content.Context
import androidx.preference.DialogPreference
import android.util.AttributeSet
import com.roy.binaryeye.R

class UrlPreference(
    context: Context?,
    attrs: AttributeSet?
) : DialogPreference(context, attrs) {
    private var url: String? = null

    init {
        dialogLayoutResource = R.layout.dlg_url
    }

    fun getUrl() = url

    fun setUrl(url: String) {
        this.url = url
        persistString(url)
    }

    @Deprecated("Deprecated in Java")
    override fun onSetInitialValue(
        restorePersistedValue: Boolean,
        defaultValue: Any?
    ) {
        setUrl(
            if (restorePersistedValue) {
                getPersistedString(url)
            } else {
                defaultValue as String
            }
        )
    }
}
