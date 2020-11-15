package com.navigatorsguide.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import com.navigatorsguide.app.managers.PreferenceManager
import com.navigatorsguide.app.model.User
import com.navigatorsguide.app.utils.CustomProgressDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

open class BaseFragment : Fragment(), CoroutineScope {
    private lateinit var job: Job
    private var mProgress: CustomProgressDialog? = null

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
            mProgress = CustomProgressDialog(context)
        }
        if (activity != null && !requireActivity().isFinishing && !mProgress!!.isShowing()) {
            mProgress!!.show()
        }
    }

    protected fun hideFullScreenProgress() {
        if (!isProgressBarShowing()) {
            return
        }
        Handler().post {
            val activity: Activity? = activity
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

    protected fun navigateToDashboard(user: User){
        PreferenceManager.saveRegistrationInfo(requireActivity(), user)
        PreferenceManager.saveAuthenticationStatus(requireActivity(), true);

        val intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        requireActivity().finish()
    }
}