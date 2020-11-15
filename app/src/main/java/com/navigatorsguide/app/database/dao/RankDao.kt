package com.navigatorsguide.app.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.navigatorsguide.app.database.entities.Rank

@Dao
interface RankDao {

    @Query("SELECT * FROM Rank")
    suspend fun getAllRanks(): List<Rank>
}