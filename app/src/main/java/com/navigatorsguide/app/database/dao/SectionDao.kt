package com.navigatorsguide.app.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.navigatorsguide.app.database.entities.Section

@Dao
interface SectionDao {

    @Query("SELECT * FROM Section")
    suspend fun getAllSections(): List<Section>

    @Query("SELECT * FROM Section WHERE sectionid IN(:subsid)")
    suspend fun getSelectedSections(subsid: Int): List<Section>

    @Query("SELECT * FROM Section WHERE sectionid IN(:sid)")
    fun getSectionInfo(sid: Int?): Section
}