package com.navigatorsguide.app.model

import com.navigatorsguide.app.database.entities.*
import com.navigatorsguide.app.database.entities.User

class ModelRepository {
    companion object {
        fun getQuestionModel() =
            Questions(0, "", "", "", "", "", "", "", 0, "", "", "".toByteArray(), "")

        fun getSectionModel() = Section(0, "", "", "", "", 0, "","")

        fun getSubSectionModel() = SubSection(0,"", 0, "", 0, "", "", "", 0, 0,"", "","","","")

        fun getRankModel() = Rank(0,"")

        fun getShipTypeModel() = ShipType(0,"")

        fun getUserAccessModel() = User(0,"", "")

    }
}