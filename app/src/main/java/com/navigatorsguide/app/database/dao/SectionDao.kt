package com.navigatorsguide.app.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.navigatorsguide.app.database.entities.Section

@Dao
interface SectionDao {

    @Query("SELECT * FROM Section")
    suspend fun getAllSections(): List<Section>
}