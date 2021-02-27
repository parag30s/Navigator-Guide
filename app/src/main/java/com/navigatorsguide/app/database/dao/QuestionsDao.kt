package com.navigatorsguide.app.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.navigatorsguide.app.database.entities.Questions

@Dao
interface QuestionsDao {

    @Query("SELECT * FROM Questions")
    suspend fun getAllQuestions(): List<Questions>

    @Query("SELECT * FROM Questions WHERE qparent IN(:subsId) AND ranks LIKE'%' || :rankId || '%' AND shiptypes LIKE'%' || :shipId || '%'")
    suspend fun getSelectedQuestions(subsId: Int, rankId: Int, shipId: Int): List<Questions>

    @Query("UPDATE Questions SET answer = :answer WHERE qid =:qid")
    suspend fun updateRadioSelection(qid: Int, answer: String?)

    @Query("UPDATE Questions SET comment= :comment WHERE qid =:qid")
    suspend fun updateComment(qid: Int, comment: String?)

    @Query("SELECT * FROM Questions WHERE qid IN(:qid)")
    suspend fun getUserResponse(qid: Int): List<Questions>

    @Query("UPDATE Questions SET attachment= :image WHERE qid =:qid")
    suspend fun updateAttachment(qid: Int, image: String?)

    @Query("SELECT * FROM Questions WHERE qparent IN(:subsId) AND (answer IN('No') OR comment IS NOT NULL OR attachment IS NOT NULL)")
    suspend fun getReportedQuestions(subsId: Int): List<Questions>

}