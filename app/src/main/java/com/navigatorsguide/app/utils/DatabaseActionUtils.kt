package com.navigatorsguide.app.utils

import android.util.Log
import com.navigatorsguide.app.database.entities.*
import org.json.JSONObject

class DatabaseActionUtils {
    companion object {
        const val TABLE_SECTIONS = 1
        const val TABLE_SUB_SECTIONS = 2
        const val TABLE_QUESTIONS = 3
        const val TABLE_RANK = 4
        const val TABLE_SHIP_TYPE = 5
        const val TABLE_USER_ACCESS = 6
        const val TABLE_SHIP_COMPANY = 7

        const val DB_ROW_ADD = 1
        const val DB_ROW_UPDATE = 2
        const val DB_ROW_DELETE = 3

        fun getKeyValue(jsonObject: JSONObject, key: String): Int {
            var value: Int = 0
            val iterator: Iterator<String> = jsonObject.keys()
            while (iterator.hasNext()) {
                when (val k = iterator.next()) {
                    key -> {
                        value = jsonObject.get(k) as Int
                    }
                }
            }
            return value
        }

        fun getQuestion(
            jsonObject: JSONObject,
            model: Questions,
            count: Int,
            mAction: Int,
        ): Questions {
            val iterator: Iterator<String> = jsonObject.keys()

            while (iterator.hasNext()) {
                val key = iterator.next()
                when (key) {
                    "qid" -> {
                        if (mAction == DB_ROW_ADD) {
                            model.qid = count
                        } else {
                            model.qid = jsonObject.get(key) as Int
                        }
                    }
                    "qtext" -> {
                        model.qtext = jsonObject.get(key).toString()
                    }
                    "ansType" -> {
                        model.ansType = jsonObject.get(key).toString()
                    }
                    "link" -> {
                        model.link = jsonObject.get(key).toString()
                    }
                    "description" -> {
                        model.description = jsonObject.get(key).toString()
                    }
                    "ranks" -> {
                        model.ranks = jsonObject.get(key).toString()
                    }
                    "shiptypes" -> {
                        model.shiptypes = jsonObject.get(key).toString()
                    }
                    "viq" -> {
                        model.viq = jsonObject.get(key).toString()
                    }
                    "qparent" -> {
                        model.qparent = jsonObject.get(key) as Int
                    }
                    "answer" -> {
                        model.answer = jsonObject.get(key).toString()
                    }
                    "comment" -> {
                        model.comment = jsonObject.get(key).toString()
                    }
                    "attachment" -> {
                        model.attachment = jsonObject.get(key).toString().toByteArray()
                    }
                    "attachmentlink" -> {
                        model.attachmentlink = jsonObject.get(key).toString()
                    }
                }
            }
            Log.d("NotificationRepository", "model $model")
            return model
        }

        fun getSection(jsonObject: JSONObject, model: Section, count: Int, mAction: Int): Section {
            val iterator: Iterator<String> = jsonObject.keys()

            while (iterator.hasNext()) {
                val key = iterator.next()
                when (key) {
                    "sectionid" -> {
                        if (mAction == DB_ROW_ADD) {
                            model.sectionid = count
                        } else {
                            model.sectionid = jsonObject.get(key) as Int
                        }
                    }
                    "sectionName" -> {
                        model.sectionName = jsonObject.get(key).toString()
                    }
                    "sectionThumbnail" -> {
                        model.sectionThumbnail = jsonObject.get(key).toString()
                    }
                    "eligibleRank" -> {
                        model.eligibleRank = jsonObject.get(key).toString()
                    }
                    "eligibleShipType" -> {
                        model.eligibleShipType = jsonObject.get(key).toString()
                    }
                    "sequence" -> {
                        if (mAction == DB_ROW_ADD) {
                            model.sequence = count
                        } else {
                            model.sequence = jsonObject.get(key) as Int
                        }
                    }
                    "note" -> {
                        model.note = jsonObject.get(key).toString()
                    }
                    "lastUnlockDate" -> {
                        model.lastUnlockDate = jsonObject.get(key).toString()
                    }
                }
            }
            Log.d("NotificationRepository", "model$model")
            return model
        }

        fun getSubSection(
            jsonObject: JSONObject,
            model: SubSection,
            count: Int,
            mAction: Int,
        ): SubSection {
            val iterator: Iterator<String> = jsonObject.keys()

            while (iterator.hasNext()) {
                val key = iterator.next()
                when (key) {
                    "subsid" -> {
                        if (mAction == DB_ROW_ADD) {
                            model.subsid = count
                        } else {
                            model.subsid = jsonObject.get(key) as Int
                        }
                    }
                    "subsname" -> {
                        model.subsname = jsonObject.get(key).toString()
                    }
                    "subsparent" -> {
                        model.subsparent = jsonObject.get(key) as Int
                    }
                    "subLink" -> {
                        model.subLink = jsonObject.get(key).toString()
                    }
                    "sited" -> {
                        model.sited = jsonObject.get(key) as Int
                    }
                    "observations" -> {
                        model.observations = jsonObject.get(key).toString()
                    }
                    "closureDate" -> {
                        model.closureDate = jsonObject.get(key).toString()
                    }
                    "comments" -> {
                        model.comments = jsonObject.get(key).toString()
                    }
                    "risk" -> {
                        model.risk = jsonObject.get(key) as Int
                    }
                    "status" -> {
                        model.status = jsonObject.get(key) as Int
                    }
                    "note" -> {
                        model.note = jsonObject.get(key).toString()
                    }
                    "attachment_link" -> {
                        model.attachment_link = jsonObject.get(key).toString()
                    }
                }
            }
            Log.d("NotificationRepository", "model$model")
            return model
        }

        fun getRank(
            jsonObject: JSONObject,
            model: Rank,
            count: Int,
            mAction: Int,
        ): Rank {
            val iterator: Iterator<String> = jsonObject.keys()

            while (iterator.hasNext()) {
                val key = iterator.next()
                when (key) {
                    "rankid" -> {
                        if (mAction == DB_ROW_ADD) {
                            model.rankid = count
                        } else {
                            model.rankid = jsonObject.get(key) as Int
                        }
                    }
                    "rankName" -> {
                        model.rankName = jsonObject.get(key).toString()
                    }
                }
            }
            Log.d("NotificationRepository", "model$model")
            return model
        }

        fun getShipType(
            jsonObject: JSONObject,
            model: ShipType,
            count: Int,
            mAction: Int,
        ): ShipType {
            val iterator: Iterator<String> = jsonObject.keys()

            while (iterator.hasNext()) {
                val key = iterator.next()
                when (key) {
                    "typeId" -> {
                        if (mAction == DB_ROW_ADD) {
                            model.typeId = count
                        } else {
                            model.typeId = jsonObject.get(key) as Int
                        }
                    }
                    "typeName" -> {
                        model.typeName = jsonObject.get(key).toString()
                    }
                }
            }
            Log.d("NotificationRepository", "model$model")
            return model
        }

        fun getUserAccess(
            jsonObject: JSONObject,
            model: User,
            count: Int,
            mAction: Int,
        ): User {
            val iterator: Iterator<String> = jsonObject.keys()

            while (iterator.hasNext()) {
                val key = iterator.next()
                when (key) {
                    "fid" -> {
                        if (mAction == DB_ROW_ADD) {
                            model.fid = count
                        } else {
                            model.fid = jsonObject.get(key) as Int
                        }
                    }
                    "feature" -> {
                        model.feature = jsonObject.get(key).toString()
                    }
                    "value" -> {
                        model.value = jsonObject.get(key).toString()
                    }
                }
            }
            Log.d("NotificationRepository", "model$model")
            return model
        }
    }
}