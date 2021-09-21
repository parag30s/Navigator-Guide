package com.navigatorsguide.app.messaging

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener


class AppMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (!remoteMessage.data.isNullOrEmpty()) {
            val data = remoteMessage.data
            Log.d("AppMessagingService", "" + data)
            val jsonObject = JSONTokener(data.toString()).nextValue() as JSONObject
            val dbObject = jsonObject.getJSONObject("db")
            Log.d("AppMessagingService", "" + dbObject)

            val actionList = mutableListOf<NotificationModel>()
            val iterator: Iterator<String> = dbObject.keys()
            while (iterator.hasNext()) {
                val key = iterator.next()
                Log.d("AppMessagingService", "" + key)
                val jsonArray: JSONArray = dbObject.getJSONArray(key)
                Log.d("AppMessagingService", "" + jsonArray)
                val length: Int = jsonArray.length()
                for (i in 0 until length) {
                    val jsonObj = jsonArray.getJSONObject(i)
                    Log.d("action : ", "" + jsonObj.getString("action"))
                    Log.d("id : ", "" + jsonObj.getString("id"))
                    Log.d("value : ", "" + jsonObj.getString("value"))
                    val notificationModel =
                        NotificationModel(key.toInt(),
                            jsonObj.getInt("action"),
                            jsonObj.getInt("id"),
                            jsonObj.getString("value"))
                    actionList.add(notificationModel)
                }
            }

            val notificationRepository: NotificationRepository =
                NotificationRepository.getInstance()
            notificationRepository.init(this.application)
            notificationRepository.processNotification(actionList)
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }
}