package com.navigatorsguide.app.database.dao

import androidx.room.*
import com.navigatorsguide.app.database.entities.User

@Dao
interface UserDao {

    @Query("SELECT * FROM User")
    suspend fun getUserAllAccesses(): List<User>

    @Query("SELECT * FROM User WHERE fid =:fid")
    suspend fun getUserAccess(fid: Int): User

    @Query("Select MAX(fid) from User")
    suspend fun getMaxCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeature(vararg userFeature: User)

    @Update
    suspend fun updateFeature(vararg userFeature: User)

    @Query("DELETE FROM User WHERE fid = :fid")
    suspend fun deleteFeature(fid: Int)
}