package com.navigatorsguide.app.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.navigatorsguide.app.database.entities.Questions
import com.navigatorsguide.app.database.entities.SubSection

@Dao
interface QuestionsDao {

    @Query("SELECT * FROM Questions")
    suspend fun getAllQuestions(): List<Questions>

    @Query("SELECT * FROM Questions WHERE qparent IN(:qParent)")
    fun getSelectedQuestions(qParent: Int): List<Questions>
}