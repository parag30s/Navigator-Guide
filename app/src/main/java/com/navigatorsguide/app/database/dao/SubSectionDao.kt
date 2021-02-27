package com.navigatorsguide.app.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.navigatorsguide.app.database.entities.SubSection

@Dao
interface SubSectionDao {

    @Query("SELECT * FROM SubSection")
    suspend fun getAllSubSections(): List<SubSection>

    @Query("SELECT * FROM SubSection WHERE subsparent IN(:sectionId)")
    fun getSelectedSubSections(sectionId: Int): List<SubSection>

    @Query("UPDATE SubSection SET sited= :sited, observations= :observation, closureDate= :date, comments= :comment, risk= :risk, status= :status WHERE subsid =:sid")
    suspend fun SubmitSectionStatus(
        sid: kotlin.Int?,
        sited: Int,
        observation: String?,
        date: String?,
        comment: String?,
        risk: Int,
        status: Int
    )

    @Query("SELECT * FROM SubSection WHERE subsid IN(:sid)")
    suspend fun getParentIdFromSubId(sid: Int?): SubSection

    @Query("SELECT * FROM SubSection WHERE status IN(1)")
    suspend fun getCompletedSections(): List<SubSection>
}