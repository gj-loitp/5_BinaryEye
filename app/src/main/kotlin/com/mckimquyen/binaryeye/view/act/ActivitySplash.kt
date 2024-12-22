package com.mckimquyen.binaryeye.view.act

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.mckimquyen.binaryeye.BaseActivity
import com.mckimquyen.binaryeye.R

class ActivitySplash : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.roy_a_splash)
        // It's important _not_ to inflate a layout file here
        // because that would happen after the app is fully
        // initialized what is too late.

        setupViews()
    }

    fun setupViews() {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                startActivity(Intent(applicationContext, CameraActivity::class.java))
                finish()
            },
            1000
        )
    }
}

