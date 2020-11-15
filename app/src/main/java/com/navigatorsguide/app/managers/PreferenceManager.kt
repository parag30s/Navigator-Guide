package com.navigatorsguide.app.managers

import android.content.Context
import com.navigatorsguide.app.model.User

class PreferenceManager {

    companion object{
        private const val REGISTRATION_PREFERENCES = "registration_preferences"
        private const val REGISTRATION_STATUS = "registration_status"
        private const val USER_NAME = "userName"
        private const val USER_ID = "userId"
        private const val EMAIL_ID = "emailId"
        private const val POSITION = "position"
        private const val SHIP_TYPE = "shipType"
        private const val CREATED_AT = "createdAt"

        fun saveAuthenticationStatus(context: Context, status: Boolean) {
            val sharedPref = context.getSharedPreferences(REGISTRATION_PREFERENCES, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean(REGISTRATION_STATUS, status)
            editor.commit()
        }

        fun getAuthenticationStatus(context: Context): Boolean {
            val sharedPref = context.getSharedPreferences(REGISTRATION_PREFERENCES, Context.MODE_PRIVATE)
            return sharedPref.getBoolean(REGISTRATION_STATUS, false)
        }

        fun saveRegistrationInfo(context: Context, user: User) {
            val sharedPref = context.getSharedPreferences(REGISTRATION_PREFERENCES, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString(USER_ID, user.getUserId())
            editor.putString(USER_NAME, user.getUserName())
            editor.putString(EMAIL_ID, user.getEmail())
            editor.putString(POSITION, user.getPosition())
            editor.putString(SHIP_TYPE, user.getShipType())
            editor.putString(CREATED_AT, user.getCreatedAt())
            editor.apply()
        }

        fun getRegistrationInfo(context: Context): User? {
            val sharedPref = context.getSharedPreferences(REGISTRATION_PREFERENCES, Context.MODE_PRIVATE)
            val userId = sharedPref.getString(USER_ID, null)
            val userName = sharedPref.getString(USER_NAME, null)
            val emailId = sharedPref.getString(EMAIL_ID, null)
            val position = sharedPref.getString(POSITION, null)
            val shipType = sharedPref.getString(SHIP_TYPE, null)
            val createdAt = sharedPref.getString(CREATED_AT, null)
            return User(userId, userName, emailId, position,shipType, createdAt)
        }

        fun deleteRegistartionInfo(context: Context) {
            val preferences = context.getSharedPreferences(REGISTRATION_PREFERENCES, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.clear()
            editor.apply()
        }
    }
}