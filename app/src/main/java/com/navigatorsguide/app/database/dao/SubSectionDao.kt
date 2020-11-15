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
}