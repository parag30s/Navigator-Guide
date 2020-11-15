package com.navigatorsguide.app

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.navigatorsguide.app.utils.CustomProgressDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

open class BaseActivity: AppCompatActivity(), CoroutineScope {
    private var mProgress: CustomProgressDialog? = null

    protected lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    protected fun showFullScreenProgress() {
        if (mProgress == null) {
            mProgress = CustomProgressDialog(this)
        }
        if (!this.isFinishing && !mProgress!!.isShowing()) {
            mProgress!!.show()
        }
    }

    protected fun hideFullScreenProgress() {
        if (!isProgressBarShowing()) {
            return
        }
        Handler().post {
            val activity: Activity? = this
            if (activity != null && isProgressBarShowing()) {
                dismissProgressDialog()
            }
        }
    }

    protected fun dismissProgressDialog() {
        mProgress!!.dismiss()
    }

    protected fun isProgressBarShowing(): Boolean {
        return mProgress != null && mProgress!!.isShowing()
    }
}