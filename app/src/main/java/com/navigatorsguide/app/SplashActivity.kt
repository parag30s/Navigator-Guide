package com.navigatorsguide.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.navigatorsguide.app.managers.PreferenceManager
import com.navigatorsguide.app.ui.account.OnBoardingActivity

class SplashActivity : Activity() {
    private val mContext: Context = this
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val splashImageView: ImageView = findViewById(R.id.splash_image_view)
        val splashTextView: TextView = findViewById(R.id.splash_text_view)
        val animation = AnimationUtils.loadAnimation(this, R.anim.splash)
        splashImageView.startAnimation(animation)
        splashTextView.startAnimation(animation)

        Handler().postDelayed({
            intent = if (PreferenceManager.getAuthenticationStatus(mContext)) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, OnBoardingActivity::class.java)
            }
            intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }, 2000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}