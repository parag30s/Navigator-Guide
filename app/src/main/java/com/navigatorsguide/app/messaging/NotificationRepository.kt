package com.navigatorsguide.app.messaging

import android.app.Application
import android.util.Log
import com.navigatorsguide.app.database.AppDatabase
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.suspendCoroutine

class NotificationRepository{
    lateinit var appDatabase: AppDatabase

    companion object {
        private var obj: NotificationRepository? = null
        fun getInstance(): NotificationRepository {
            if (obj == null) {
                obj = NotificationRepository()
            }
            return obj as NotificationRepository
        }
    }

    fun init(application: Application?) {
        appDatabase = AppDatabase.invoke(application!!)
    }

    fun processNotification(notificationModel: NotificationModel?) {
        Log.d("", "" + notificationModel)

        GlobalScope.launch(Dispatchers.IO) {
//            suspendCoroutine<Unit> {
//            }
            withContext(Dispatchers.Main) {
//                val sectionList = appDatabase.getSectionDao().getAllSections()
            }
        }
    }
}