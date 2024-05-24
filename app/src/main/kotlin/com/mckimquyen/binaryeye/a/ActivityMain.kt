package com.mckimquyen.binaryeye.a

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.mckimquyen.binaryeye.R
import com.mckimquyen.binaryeye.ext.app.PERMISSION_LOCATION
import com.mckimquyen.binaryeye.ext.app.PERMISSION_WRITE
import com.mckimquyen.binaryeye.ext.app.applyLocale
import com.mckimquyen.binaryeye.ext.app.permissionGrantedCallback
import com.mckimquyen.binaryeye.ext.app.setFragment
import com.mckimquyen.binaryeye.database.Scan
import com.mckimquyen.binaryeye.frm.FDecode
import com.mckimquyen.binaryeye.frm.FEncode
import com.mckimquyen.binaryeye.frm.FHistory
import com.mckimquyen.binaryeye.frm.FPreferences
import com.mckimquyen.binaryeye.prefs
import com.mckimquyen.binaryeye.view.colorSystemAndToolBars
import com.mckimquyen.binaryeye.view.initSystemBars
import com.mckimquyen.binaryeye.view.recordToolbarHeight

class ActivityMain : AppCompatActivity() {
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        when (requestCode) {
            PERMISSION_LOCATION, PERMISSION_WRITE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGrantedCallback?.invoke()
                permissionGrantedCallback = null
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val fm = supportFragmentManager
        if (fm != null && fm.backStackEntryCount > 0) {
            fm.popBackStack()
        } else {
            finish()
        }
        return true
    }

    override fun attachBaseContext(base: Context?) {
        base?.applyLocale(prefs.customLocale)
        super.attachBaseContext(base)
    }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(R.layout.roy_a_main)

        initSystemBars(this)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        recordToolbarHeight(toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.addOnBackStackChangedListener {
            colorSystemAndToolBars(this@ActivityMain)
        }

        if (state == null) {
            supportFragmentManager?.setFragment(getFragmentForIntent(intent))
        }
    }

    companion object {
        private const val PREFERENCES = "preferences"
        private const val HISTORY = "history"
        private const val ENCODE = "encode"
        const val DECODED = "decoded"

        private fun getFragmentForIntent(intent: Intent?): Fragment {
            intent ?: return FPreferences()
            return when {
                intent.hasExtra(PREFERENCES) -> FPreferences()
                intent.hasExtra(HISTORY) -> FHistory()
                intent.hasExtra(ENCODE) -> FEncode.newInstance(
                    intent.getStringExtra(ENCODE)
                )

                intent.hasExtra(DECODED) -> FDecode.newInstance(
                    intent.getParcelableExtra(DECODED)!!
                )

                else -> FPreferences()
            }
        }

        fun getPreferencesIntent(context: Context): Intent {
            val intent = Intent(context, ActivityMain::class.java)
            intent.putExtra(PREFERENCES, true)
            return intent
        }

        fun getHistoryIntent(context: Context): Intent {
            val intent = Intent(context, ActivityMain::class.java)
            intent.putExtra(HISTORY, true)
            return intent
        }

        fun getEncodeIntent(
            context: Context,
            text: String? = "",
            isExternal: Boolean = false,
        ): Intent {
            val intent = Intent(context, ActivityMain::class.java)
            intent.putExtra(ENCODE, text)
            if (isExternal) {
                val flagActivityClearTask =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.addFlags(
                    Intent.FLAG_ACTIVITY_NO_HISTORY or flagActivityClearTask or Intent.FLAG_ACTIVITY_NEW_TASK
                )
            }
            return intent
        }

        fun getDecodeIntent(
            context: Context,
            scan: Scan,
        ): Intent {
            val intent = Intent(context, ActivityMain::class.java)
            intent.putExtra(DECODED, scan)
            return intent
        }
    }
}
