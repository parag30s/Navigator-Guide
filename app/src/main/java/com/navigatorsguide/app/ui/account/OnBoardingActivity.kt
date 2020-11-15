package com.navigatorsguide.app.ui.account

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import com.navigatorsguide.app.R

class OnBoardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        coordinatorLayout = findViewById<View>(R.id.coordinatorLayout) as CoordinatorLayout
        if (savedInstanceState == null) {
            val loginFragment = LoginFragment()
            val bundle = Bundle()
            loginFragment.arguments = bundle
            supportFragmentManager
                .beginTransaction()
                .add(
                    R.id.container, loginFragment,
                    LoginFragment.FRAGMENT_TAG
                ).commit()
        }
    }

    companion object {
        private var coordinatorLayout: CoordinatorLayout? = null
        fun showSimpleSnackBar(content: String?) {
            val snackbar = Snackbar
                .make(coordinatorLayout!!, content!!, Snackbar.LENGTH_LONG)
            snackbar.show()
        }
    }
}