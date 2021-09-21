package com.navigatorsguide.app.managers

import android.content.Context
import com.navigatorsguide.app.model.User
import com.navigatorsguide.app.utils.Security

class PreferenceManager {

    companion object {
        private const val REGISTRATION_PREFERENCES = "registration_preferences"
        private const val REGISTRATION_STATUS = "registration_status"
        private const val USER_NAME = "userName"
        private const val USER_ID = "userId"
        private const val EMAIL_ID = "emailId"
        private const val PASSWORD = "password"
        private const val POSITION = "position"
        private const val SHIP_TYPE = "shipType"
        private const val SHIP_NAME = "shipName"
        private const val PROFILE_IMAGE = "profileImage"
        private const val TOKEN = "token"
        private const val CREATED_AT = "createdAt"
        private const val POSITION_NAME = "positionName"
        private const val SHIP_TYPE_NAME = "shipTypeName"
        private const val INSPECTED_SHIP_TYPE = "inspectedShipType"

        fun saveAuthenticationStatus(context: Context, status: Boolean) {
            val sharedPref =
                context.getSharedPreferences(REGISTRATION_PREFERENCES, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean(REGISTRATION_STATUS, status)
            editor.commit()
        }

        fun getAuthenticationStatus(context: Context): Boolean {
            val sharedPref =
                context.getSharedPreferences(REGISTRATION_PREFERENCES, Context.MODE_PRIVATE)
            return sharedPref.getBoolean(REGISTRATION_STATUS, false)
        }

        fun saveRegistrationInfo(context: Context, user: User) {
            val sharedPref =
                context.getSharedPreferences(REGISTRATION_PREFERENCES, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString(USER_ID, user.getUserId())
            editor.putString(USER_NAME, user.getUserName())
            editor.putString(EMAIL_ID, user.getEmail())
            editor.putString(PASSWORD, Security.encrypt(user.getPassword()))
            editor.putInt(POSITION, user.getPosition())
            editor.putInt(SHIP_TYPE, user.getShipType())
            editor.putString(CREATED_AT, user.getCreatedAt())
            editor.apply()
        }

        fun getRegistrationInfo(context: Context): User? {
            val sharedPref =
                context.getSharedPreferences(REGISTRATION_PREFERENCES, Context.MODE_PRIVATE)
            val userId = sharedPref.getString(USER_ID, null)
            val userName = sharedPref.getString(USER_NAME, null)
            val emailId = sharedPref.getString(EMAIL_ID, null)
            val password = Security.decrypt(sharedPref.getString(PASSWORD, null).toString())
            val position = sharedPref.getInt(POSITION, 0)
            val shipType = sharedPref.getInt(SHIP_TYPE, 0)
            val token = sharedPref.getString(TOKEN, null)
            val createdAt = sharedPref.getString(CREATED_AT, null)
            return User(userId, userName, emailId, password, position, shipType, token, createdAt)
        }

            fun savePosition(context: Context, position: Int) {
            val sharedPref =
                context.getSharedPreferences(REGISTRATION_PREFERENCES, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putInt(POSITION, position)
            editor.apply()
        }

        fun saveShipType(context: Context, shipType: Int) {
            val sharedPref =
                context.getSharedPreferences(REGISTRATION_PREFERENCES, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putInt(SHIP_TYPE, shipType)
            editor.apply()
        }

        fun getShipTypeName(context: Context): String? {
            val sharedPref =
                context.getSharedPreferences(REGISTRATION_PREFERENCES, Context.MODE_PRIVATE)
            return sharedPref.getString(SHIP_TYPE_NAME, null)
        }

        fun saveShipTypeName(context: Context, shipName: String) {
            val sharedPref =
                context.getSharedPreferences(REGISTRATION_PREFERENCES, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString(SHIP_TYPE_NAME, shipName)
            editor.apply()
        }

        fun getPositionName(context: Context): String? {
            val sharedPref =
                context.getSharedPreferences(REGISTRATION_PREFERENCES, Context.MODE_PRIVATE)
            return sharedPref.getString(POSITION_NAME, null)
        }

        fun savePositionName(context: Context, positionName: String) {
            val sharedPref =
                context.getSharedPreferences(REGISTRATION_PREFERENCES, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString(POSITION_NAME, positionName)
            editor.apply()
        }

        fun saveShipName(context: Context, shipName: String) {
            val sharedPref =
                context.getSharedPreferences(REGISTRATION_PREFERENCES, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString(SHIP_NAME, shipName)
            editor.apply()
        }

        fun getShipName(context: Context): String? {
            val sharedPref =
                context.getSharedPreferences(REGISTRATION_PREFERENCES, Context.MODE_PRIVATE)
            return sharedPref.getString(SHIP_NAME, null)
        }

        fun saveProfileImage(context: Context, profileImage: String) {
            val sharedPref =
                context.getSharedPreferences(REGISTRATION_PREFERENCES, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString(PROFILE_IMAGE, profileImage)
            editor.apply()
        }

        fun getProfileImage(context: Context): String? {
            val sharedPref =
                context.getSharedPreferences(REGISTRATION_PREFERENCES, Context.MODE_PRIVATE)
            return sharedPref.getString(PROFILE_IMAGE, null)
        }

        fun deleteRegistartionInfo(context: Context) {
            val preferences =
                context.getSharedPreferences(REGISTRATION_PREFERENCES, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.clear()
            editor.apply()
        }
    }
}