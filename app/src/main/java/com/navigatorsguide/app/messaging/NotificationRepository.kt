package com.navigatorsguide.app.messaging

import android.app.Application
import android.util.Log
import com.navigatorsguide.app.database.AppDatabase
import com.navigatorsguide.app.database.entities.*
import com.navigatorsguide.app.model.ModelRepository
import com.navigatorsguide.app.utils.DatabaseActionUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.json.JSONTokener

class NotificationRepository {
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

    fun processNotification(notificationList: MutableList<NotificationModel>) {
        Log.d("NotificationRepository", "" + notificationList)

        for (i in notificationList) {
            if (i.mTable == DatabaseActionUtils.TABLE_QUESTIONS) {
                val jsonObject = JSONTokener(i.mValue.toString()).nextValue() as JSONObject
                var model: Questions = ModelRepository.getQuestionModel()
                var count: Int = 0

                when (i.mAction) {
                    DatabaseActionUtils.DB_ROW_ADD -> {
                        scope.launch {
                            count = appDatabase.getQuestionsDao().getMaxCount()
                            val question =
                                DatabaseActionUtils.getQuestion(jsonObject,
                                    model,
                                    (count + 1),
                                    i.mAction)
                            question.attachment = null
                            val qInsert = appDatabase.getQuestionsDao().insertQuestion(question)
                        }
                    }
                    DatabaseActionUtils.DB_ROW_UPDATE -> {
                        scope.launch {
                            model = appDatabase.getQuestionsDao()
                                .getQuestion(DatabaseActionUtils.getKeyValue(jsonObject, "qid"))
                            val question =
                                DatabaseActionUtils.getQuestion(jsonObject,
                                    model,
                                    count,
                                    i.mAction)
                            val qUpdate = appDatabase.getQuestionsDao().updateQuestion(question)
                        }
                    }
                    DatabaseActionUtils.DB_ROW_DELETE -> {
                        scope.launch {
                            val qDelete = appDatabase.getQuestionsDao().deleteQuestion(i.mId)
                        }
                    }
                }
            } else if (i.mTable == DatabaseActionUtils.TABLE_SECTIONS) {
                val jsonObject = JSONTokener(i.mValue.toString()).nextValue() as JSONObject
                var model: Section = ModelRepository.getSectionModel()
                var count: Int = 0

                when (i.mAction) {
                    DatabaseActionUtils.DB_ROW_ADD -> {
                        scope.launch {
                            count = appDatabase.getSectionDao().getMaxCount()
                            val section = DatabaseActionUtils.getSection(jsonObject,
                                model,
                                (count + 1),
                                i.mAction)
                            val qInsert = appDatabase.getSectionDao().insertSection(section)
                        }
                    }
                    DatabaseActionUtils.DB_ROW_UPDATE -> {
                        scope.launch {
                            model = appDatabase.getSectionDao()
                                .getSections(DatabaseActionUtils.getKeyValue(jsonObject,
                                    "sectionid"))
                            val section = DatabaseActionUtils.getSection(jsonObject,
                                model,
                                count,
                                i.mAction)
                            val qUpdate = appDatabase.getSectionDao().updateSection(section)
                        }
                    }
                    DatabaseActionUtils.DB_ROW_DELETE -> {
                        scope.launch {
                            val qDelete = appDatabase.getSectionDao().deleteSection(i.mId)
                        }
                    }
                }
            } else if (i.mTable == DatabaseActionUtils.TABLE_SUB_SECTIONS) {
                val jsonObject = JSONTokener(i.mValue.toString()).nextValue() as JSONObject
                var model: SubSection = ModelRepository.getSubSectionModel()
                var count: Int = 0

                when (i.mAction) {
                    DatabaseActionUtils.DB_ROW_ADD -> {
                        scope.launch {
                            count = appDatabase.getSubSectionDao().getMaxCount()
                            val subSection = DatabaseActionUtils.getSubSection(jsonObject,
                                model,
                                (count + 1),
                                i.mAction)
                            val qInsert =
                                appDatabase.getSubSectionDao().insertSubSection(subSection)
                        }
                    }
                    DatabaseActionUtils.DB_ROW_UPDATE -> {
                        scope.launch {
                            model = appDatabase.getSubSectionDao()
                                .getParentIdFromSubId(DatabaseActionUtils.getKeyValue(jsonObject,
                                    "subsid"))
                            val subSection = DatabaseActionUtils.getSubSection(jsonObject,
                                model,
                                count,
                                i.mAction)
                            val qUpdate =
                                appDatabase.getSubSectionDao().updateSubSection(subSection)
                        }
                    }
                    DatabaseActionUtils.DB_ROW_DELETE -> {
                        scope.launch {
                            val qDelete = appDatabase.getSubSectionDao().deleteSubSection(i.mId)
                        }
                    }
                }
            } else if (i.mTable == DatabaseActionUtils.TABLE_RANK) {
                val jsonObject = JSONTokener(i.mValue.toString()).nextValue() as JSONObject
                var model: Rank = ModelRepository.getRankModel()
                var count: Int = 0

                when (i.mAction) {
                    DatabaseActionUtils.DB_ROW_ADD -> {
                        scope.launch {
                            count = appDatabase.getRankDao().getMaxCount()
                            val rank = DatabaseActionUtils.getRank(jsonObject,
                                model,
                                (count + 1),
                                i.mAction)
                            val qInsert = appDatabase.getRankDao().insertRank(rank)
                        }
                    }
                    DatabaseActionUtils.DB_ROW_UPDATE -> {
                        scope.launch {
                            model = appDatabase.getRankDao()
                                .getRank(DatabaseActionUtils.getKeyValue(jsonObject,
                                    "rankid"))
                            val rank = DatabaseActionUtils.getRank(jsonObject,
                                model,
                                count,
                                i.mAction)
                            val qUpdate = appDatabase.getRankDao().updateRank(rank)
                        }
                    }
                    DatabaseActionUtils.DB_ROW_DELETE -> {
                        scope.launch {
                            val qDelete = appDatabase.getRankDao().deleteRank(i.mId)
                        }
                    }
                }
            } else if (i.mTable == DatabaseActionUtils.TABLE_SHIP_TYPE) {
                val jsonObject = JSONTokener(i.mValue.toString()).nextValue() as JSONObject
                var model: ShipType = ModelRepository.getShipTypeModel()
                var count: Int = 0

                when (i.mAction) {
                    DatabaseActionUtils.DB_ROW_ADD -> {
                        scope.launch {
                            count = appDatabase.getShipTypeDao().getMaxCount()
                            val shipType = DatabaseActionUtils.getShipType(jsonObject,
                                model,
                                (count + 1),
                                i.mAction)
                            val qInsert = appDatabase.getShipTypeDao().insertShipType(shipType)
                        }
                    }
                    DatabaseActionUtils.DB_ROW_UPDATE -> {
                        scope.launch {
                            model = appDatabase.getShipTypeDao()
                                .getShipType(DatabaseActionUtils.getKeyValue(jsonObject,
                                    "typeId"))
                            val shipType = DatabaseActionUtils.getShipType(jsonObject,
                                model,
                                count,
                                i.mAction)
                            val qUpdate = appDatabase.getShipTypeDao().updateShipType(shipType)
                        }
                    }
                    DatabaseActionUtils.DB_ROW_DELETE -> {
                        scope.launch {
                            val qDelete = appDatabase.getShipTypeDao().deleteShipType(i.mId)
                        }
                    }
                }
            } else if (i.mTable == DatabaseActionUtils.TABLE_USER_ACCESS) {
                val jsonObject = JSONTokener(i.mValue.toString()).nextValue() as JSONObject
                var model: User = ModelRepository.getUserAccessModel()
                var count: Int = 0

                when (i.mAction) {
                    DatabaseActionUtils.DB_ROW_ADD -> {
                        scope.launch {
                            count = appDatabase.getUsersDao().getMaxCount()
                            val userFeature = DatabaseActionUtils.getUserAccess(jsonObject,
                                model,
                                (count + 1),
                                i.mAction)
                            val qInsert = appDatabase.getUsersDao().insertFeature(userFeature)
                        }
                    }
                    DatabaseActionUtils.DB_ROW_UPDATE -> {
                        scope.launch {
                            model = appDatabase.getUsersDao()
                                .getUserAccess(DatabaseActionUtils.getKeyValue(jsonObject,
                                    "fid"))
                            val userFeature = DatabaseActionUtils.getUserAccess(jsonObject,
                                model,
                                count,
                                i.mAction)
                            val qUpdate = appDatabase.getUsersDao().updateFeature(userFeature)
                        }
                    }
                    DatabaseActionUtils.DB_ROW_DELETE -> {
                        scope.launch {
                            val qDelete = appDatabase.getUsersDao().deleteFeature(i.mId)
                        }
                    }
                }
            }
        }
    }
}