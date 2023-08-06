package com.roy.binaryeye.app

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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
