package com.roy.binaryeye.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.roy.binaryeye.R
import com.roy.binaryeye.app.PERMISSION_LOCATION
import com.roy.binaryeye.app.PERMISSION_WRITE
import com.roy.binaryeye.app.applyLocale
import com.roy.binaryeye.app.permissionGrantedCallback
import com.roy.binaryeye.prefs
import com.roy.binaryeye.app.setFragment
import com.roy.binaryeye.db.Scan
import com.roy.binaryeye.fragment.FragmentDecode
import com.roy.binaryeye.fragment.FragmentEncode
import com.roy.binaryeye.fragment.FragmentHistory
import com.roy.binaryeye.fragment.FragmentPreferences
import com.roy.binaryeye.view.colorSystemAndToolBars
import com.roy.binaryeye.view.initSystemBars
import com.roy.binaryeye.view.recordToolbarHeight

class MainActivity : AppCompatActivity() {
	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<String>,
		grantResults: IntArray
	) {
		when (requestCode) {
			PERMISSION_LOCATION, PERMISSION_WRITE -> if (grantResults.isNotEmpty() &&
				grantResults[0] == PackageManager.PERMISSION_GRANTED
			) {
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
		setContentView(R.layout.a_main)

		initSystemBars(this)
		val toolbar = findViewById(R.id.toolbar) as Toolbar
		recordToolbarHeight(toolbar)
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		supportFragmentManager.addOnBackStackChangedListener {
			colorSystemAndToolBars(this@MainActivity)
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
			intent ?: return FragmentPreferences()
			return when {
				intent.hasExtra(PREFERENCES) -> FragmentPreferences()
				intent.hasExtra(HISTORY) -> FragmentHistory()
				intent.hasExtra(ENCODE) -> FragmentEncode.newInstance(
					intent.getStringExtra(ENCODE)
				)
				intent.hasExtra(DECODED) -> FragmentDecode.newInstance(
					intent.getParcelableExtra(DECODED)!!
				)
				else -> FragmentPreferences()
			}
		}

		fun getPreferencesIntent(context: Context): Intent {
			val intent = Intent(context, MainActivity::class.java)
			intent.putExtra(PREFERENCES, true)
			return intent
		}

		fun getHistoryIntent(context: Context): Intent {
			val intent = Intent(context, MainActivity::class.java)
			intent.putExtra(HISTORY, true)
			return intent
		}

		fun getEncodeIntent(
			context: Context,
			text: String? = "",
			isExternal: Boolean = false
		): Intent {
			val intent = Intent(context, MainActivity::class.java)
			intent.putExtra(ENCODE, text)
			if (isExternal) {
				val flagActivityClearTask =
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						Intent.FLAG_ACTIVITY_CLEAR_TASK
					} else 0
				intent.addFlags(
					Intent.FLAG_ACTIVITY_NO_HISTORY or
							flagActivityClearTask or
							Intent.FLAG_ACTIVITY_NEW_TASK
				)
			}
			return intent
		}

		fun getDecodeIntent(context: Context, scan: Scan): Intent {
			val intent = Intent(context, MainActivity::class.java)
			intent.putExtra(DECODED, scan)
			return intent
		}
	}
}
