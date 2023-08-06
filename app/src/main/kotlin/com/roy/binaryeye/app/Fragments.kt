package com.roy.binaryeye.app

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.roy.binaryeye.R

fun FragmentManager.setFragment(fragment: Fragment) {
    getTransaction(fragment).commit()
}

fun FragmentManager.addFragment(fragment: Fragment) {
    getTransaction(fragment).addToBackStack(null).commit()
}

@SuppressLint("CommitTransaction")
private fun FragmentManager.getTransaction(
	fragment: Fragment,
) = beginTransaction().replace(R.id.contentFrame, fragment)
