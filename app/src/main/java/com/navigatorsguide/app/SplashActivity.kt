package com.navigatorsguide.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.navigatorsguide.app.managers.PreferenceManager
import com.navigatorsguide.app.ui.account.OnBoardingActivity

class SplashActivity : Activity() {
    private val mContext: Context = this
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent = if (PreferenceManager.getAuthenticationStatus(mContext)) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, OnBoardingActivity::class.java)
        }
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}