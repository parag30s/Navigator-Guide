package com.navigatorsguide.app.utils

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.text.TextUtils
import android.util.Patterns
import android.widget.EditText
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.navigatorsguide.app.R
import java.text.SimpleDateFormat
import java.util.*


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

        fun validateLoginCredentials(
            mContext: Context,
            mEmail: EditText,
            mPassword: EditText,
        ): Boolean {
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
            mShipType: EditText,
        ): Boolean {
            val userName: String = mName.getText().toString().trim { it <= ' ' }
            val userEmail: String = mEmail.getText().toString().trim { it <= ' ' }
            val userPassword: String = mPassword.getText().toString().trim { it <= ' ' }
            val position: String = mPosition.getText().toString().trim { it <= ' ' }
            val shipType: String = mShipType.getText().toString().trim { it <= ' ' }

            if (userName.isEmpty()) {
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
            } else if (position.isEmpty()) {
                mPosition.setError(mContext.getString(R.string.err_msg_position))
                return false
            } else if (shipType.isEmpty()) {
                mShipType.setError(mContext.getString(R.string.err_msg_position))
                return false
            }
            return true
        }

        fun checkUserId(userId: String): String {
            return userId.replace(".", "'")
        }

        fun getScreenWidth(): Int {
            return Resources.getSystem().displayMetrics.widthPixels
        }

        fun getSectionThumbnail(sectionId: Int): Int {
            return when (sectionId) {
                1 -> R.drawable.ic_engine
                2 -> R.drawable.ic_bridge
                3 -> R.drawable.ic_lsa_ffa
                else -> {
                    R.drawable.ic_no_image
                }
            }
        }

        fun showWebViewDialog(mContext: Context, mLink: String) {
            val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            val customTabsIntent: CustomTabsIntent = builder.build()
            customTabsIntent.launchUrl(mContext, Uri.parse(mLink))
        }

        fun getSitedValue(sitedId: Int): String? {
            return hashMapOf(0 to "No", 1 to "Yes")[sitedId]
        }

        fun getRiskValue(riskId: Int): String? {
            return hashMapOf(0 to "High Risk", 1 to "Medium Risk", 2 to "Low Risk", -1 to "NA")[riskId]
        }

        fun getInitials(fullname: String): StringBuilder {
            val initials = StringBuilder()
            for (s in fullname.split(" ")) {
                initials.append(s[0])
            }
            return initials
        }

        fun getDate(time: String): String {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val date: String = simpleDateFormat.format(time.toLong())
            return date
        }

        fun composeEmail(context: Context, addresses: Array<String>, subject: String) {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // only email apps should handle this
                putExtra(Intent.EXTRA_EMAIL, addresses)
                putExtra(Intent.EXTRA_SUBJECT, subject)
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
        }

        fun getBitmapFromDrawable(context: Context, drawableRes: Int): Bitmap? {
            val drawable: Drawable = ContextCompat.getDrawable(context, drawableRes)!!
            val canvas = Canvas()
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888)
            canvas.setBitmap(bitmap)
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            drawable.draw(canvas)
            return bitmap
        }
    }
}