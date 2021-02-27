package com.navigatorsguide.app.utils

import com.navigatorsguide.app.database.entities.Section
import java.util.*

object Eligibility {
    fun isEligibleSection(sectionList: List<Section?>, rankId: Int): List<Int> {
        val rankEligibility = ArrayList<Int>()
        val shipEligibility = ArrayList<Int>()
        for (section in sectionList) {
            if (section?.eligibleRank != null && section.eligibleShipType != null) {
                val rankList = section.eligibleRank!!.split(",").toTypedArray()
                val shipList = section.eligibleShipType!!.split(",").toTypedArray()
                for (s in rankList) {
                    if (s.equals(
                            rankId.toString(),
                            ignoreCase = true
                        )
                    ) rankEligibility.add(section.sectionid)
                }
                for (s in shipList) {
                    if (s.equals(
                            rankId.toString(),
                            ignoreCase = true
                        )
                    ) shipEligibility.add(section.sectionid)
                }
            }
        }
        return rankEligibility.intersect(shipEligibility.toList()).toList()
    }
}