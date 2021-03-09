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

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    fun processNotification(notificationModel: NotificationModel?) {
        Log.d("NotificationRepository", "" + notificationModel)

        scope.launch {
            val sectionList = appDatabase.getSectionDao().getAllSections()
            Log.d("NotificationRepository", "sectionList size" + sectionList.size)
        }
    }
}