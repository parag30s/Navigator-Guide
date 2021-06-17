package com.navigatorsguide.app.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
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
                2 -> R.drawable.ic_engine_documentation
                3 -> R.drawable.ic_bridge
                4 -> R.drawable.ic_lsa_ffa
                5 -> R.drawable.ic_accommodation
                6 -> R.drawable.ic_galley
                7 -> R.drawable.ic_hospital
                8 -> R.drawable.ic_after_poop_deck
                9 -> R.drawable.ic_forecastle
                10 -> R.drawable.ic_deck
                11 -> R.drawable.ic_cargo_control_room
                12 -> R.drawable.ic_deck_documentation
                13 -> R.drawable.ic_emergency_headquarters
                14 -> R.drawable.ic_masters_documentation
                15 -> R.drawable.ic_pumproom
                16 -> R.drawable.ic_security
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
            return hashMapOf(0 to "High Risk",
                1 to "Medium Risk",
                2 to "Low Risk",
                -1 to "NA")[riskId]
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

        fun getActivity(context: Context?): Activity? {
            if (context == null) {
                return null
            } else if (context is ContextWrapper) {
                return if (context is Activity) {
                    context
                } else {
                    getActivity(context.baseContext)
                }
            }
            return null
        }

        @SuppressLint("MissingPermission")
        fun getDeviceId(context: Context): String? {
            val deviceId: String
            deviceId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID)
            } else {
                val mTelephony =
                    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                if (mTelephony.deviceId != null) {
                    mTelephony.deviceId
                } else {
                    Settings.Secure.getString(
                        context.contentResolver,
                        Settings.Secure.ANDROID_ID)
                }
            }
            return deviceId
        }

        fun openFilePreview(context: Context, uri: Uri, url: String) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                    // Word document
                    intent.setDataAndType(uri, "application/msword")
                } else if (url.toString().contains(".pdf")) {
                    // PDF file
                    intent.setDataAndType(uri, "application/pdf")
                } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                    // Powerpoint file
                    intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
                } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                    // Excel file
                    intent.setDataAndType(uri, "application/vnd.ms-excel")
                } else if (url.toString().contains(".zip")) {
                    // ZIP file
                    intent.setDataAndType(uri, "application/zip")
                } else if (url.toString().contains(".rar")) {
                    // RAR file
                    intent.setDataAndType(uri, "application/x-rar-compressed")
                } else if (url.toString().contains(".rtf")) {
                    // RTF file
                    intent.setDataAndType(uri, "application/rtf")
                } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                    // WAV audio file
                    intent.setDataAndType(uri, "audio/x-wav")
                } else if (url.toString().contains(".gif")) {
                    // GIF file
                    intent.setDataAndType(uri, "image/gif")
                } else if (url.toString().contains(".jpg") || url.toString()
                        .contains(".jpeg") || url.toString().contains(".png")
                ) {
                    // JPG file
                    intent.setDataAndType(uri, "image/jpeg")
                } else if (url.toString().contains(".txt")) {
                    // Text file
                    intent.setDataAndType(uri, "text/plain")
                } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") ||
                    url.toString().contains(".mpeg") || url.toString()
                        .contains(".mpe") || url.toString().contains(".mp4") || url.toString()
                        .contains(".avi")
                ) {
                    // Video files
                    intent.setDataAndType(uri, "video/*")
                } else {
                    intent.setDataAndType(uri, "*/*")
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context,
                    "No application found which can open the file",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}