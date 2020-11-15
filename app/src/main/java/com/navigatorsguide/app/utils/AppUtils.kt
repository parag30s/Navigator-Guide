package com.navigatorsguide.app.utils

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.text.TextUtils
import android.util.Patterns
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import com.navigatorsguide.app.R


class AppUtils {
    companion object {
        fun isInternetAvailable(context: FragmentActivity?): Boolean {
            val connectivityManager =
                context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            return (connectivityManager!!.activeNetworkInfo != null
                    && connectivityManager!!.activeNetworkInfo!!.isConnected)
        }

        fun isValidEmail(email: String?): Boolean {
            return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun validateLoginCredentials(mContext: Context, mEmail: EditText, mPassword: EditText): Boolean {
            val userEmail: String = mEmail.getText().toString().trim { it <= ' ' }
            val userPassword: String = mPassword.getText().toString().trim { it <= ' ' }
            if (userEmail.isEmpty()) {
                mEmail.setError(mContext.getString(R.string.err_msg_email))
                return false
            } else if (!isValidEmail(userEmail)) {
                mEmail.setError(mContext.getString(R.string.err_msg_email))
                return false
            } else if (userPassword.isEmpty()) {
                mPassword.setError(mContext.getString(R.string.err_msg_password))
                return false
            }
            return true
        }

        fun validateRegistrationCredentials(
            mContext: Context,
            mName: EditText,
            mEmail: EditText,
            mPassword: EditText,
            mPosition: EditText,
            mShipType: EditText
        ): Boolean {
            val userName: String = mName.getText().toString().trim { it <= ' ' }
            val userEmail: String = mEmail.getText().toString().trim { it <= ' ' }
            val userPassword: String = mPassword.getText().toString().trim { it <= ' ' }
            val position: String = mPosition.getText().toString().trim { it <= ' ' }
            val shipType: String = mShipType.getText().toString().trim { it <= ' ' }

            if(userName.isEmpty()) {
                mName.setError(mContext.getString(R.string.err_msg_name))
                return false
            } else if (userEmail.isEmpty()) {
                mEmail.setError(mContext.getString(R.string.err_msg_email))
                return false
            } else if (!isValidEmail(userEmail)) {
                mEmail.setError(mContext.getString(R.string.err_msg_email))
                return false
            } else if (userPassword.isEmpty()) {
                mPassword.setError(mContext.getString(R.string.err_msg_password))
                return false
            } else if (userPassword.length < 8) {
                mPassword.setError(mContext.getString(R.string.err_msg_password_length))
                return false
            } else if (position.isEmpty()){
                mPosition.setError(mContext.getString(R.string.err_msg_position))
                return false
            } else if (shipType.isEmpty()){
                mShipType.setError(mContext.getString(R.string.err_msg_position))
                return false
            }
            return true
        }

        fun checkUserId(userId: String): String{
            return userId.replace(".", "'")
        }

        fun getScreenWidth(): Int {
            return Resources.getSystem().displayMetrics.widthPixels
        }

        fun getSectionThumbnail(sectionId: Int): Int {
            when (sectionId) {
                1 -> return R.drawable.ic_engine
                2 -> return R.drawable.ic_bridge
                3 -> return R.drawable.ic_lsa_ffa
                else -> {
                    return R.drawable.ic_no_image
                }
            }
        }
    }
}