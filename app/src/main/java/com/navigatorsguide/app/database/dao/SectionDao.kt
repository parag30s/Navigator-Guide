package com.navigatorsguide.app.database.dao

import androidx.room.*
import com.navigatorsguide.app.database.entities.Questions
import com.navigatorsguide.app.database.entities.Section

@Dao
interface SectionDao {

    @Query("SELECT * FROM Section ORDER BY sequence")
    suspend fun getAllSections(): List<Section>

    @Query("SELECT * FROM Section WHERE sectionid IN(:sectionId)")
    suspend fun getSections(sectionId: Int): Section

    @Query("SELECT * FROM Section WHERE sectionid IN(:sid)")
    fun getSectionInfo(sid: Int?): Section

    @Query("Select MAX(sectionid) from Section")
    suspend fun getMaxCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSection(vararg section: Section)

    @Update
    suspend fun updateSection(vararg section: Section)

    @Query("DELETE FROM Section WHERE sectionid = :sectionid")
    suspend fun deleteSection(sectionid: Int)
}