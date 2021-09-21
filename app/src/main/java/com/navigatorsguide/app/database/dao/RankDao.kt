package com.navigatorsguide.app.database.dao

import androidx.room.*
import com.navigatorsguide.app.database.entities.Rank
import com.navigatorsguide.app.database.entities.Section
import com.navigatorsguide.app.database.entities.SubSection

@Dao
interface RankDao {

    @Query("SELECT * FROM Rank")
    suspend fun getAllRanks(): List<Rank>

    @Query("SELECT rankid FROM Rank WHERE rankName =:mRank")
    suspend fun getIdFromRank(mRank: String): Int

    @Query("SELECT * FROM Rank WHERE rankid IN(:rank)")
    suspend fun getRank(rank: Int): Rank

    @Query("Select MAX(rankid) from Rank")
    suspend fun getMaxCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRank(vararg rank: Rank)

    @Update
    suspend fun updateRank(vararg rank: Rank)

    @Query("DELETE FROM Rank WHERE rankid = :rankId")
    suspend fun deleteRank(rankId: Int)
}