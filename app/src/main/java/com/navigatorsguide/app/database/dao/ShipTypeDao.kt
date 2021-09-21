package com.navigatorsguide.app.database.dao

import androidx.room.*
import com.navigatorsguide.app.database.entities.Rank
import com.navigatorsguide.app.database.entities.ShipType

@Dao
interface ShipTypeDao {

    @Query("SELECT * FROM ShipType")
    suspend fun getAllShipType(): List<ShipType>

    @Query("SELECT typeId FROM ShipType WHERE typeName =:mType")
    suspend fun getIdFromShip(mType: String): Int

    @Query("SELECT * FROM ShipType WHERE typeId IN(:typeId)")
    suspend fun getShipType(typeId: Int): ShipType

    @Query("Select MAX(typeId) from ShipType")
    suspend fun getMaxCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShipType(vararg shipType: ShipType)

    @Update
    suspend fun updateShipType(vararg shipType: ShipType)

    @Query("DELETE FROM ShipType WHERE typeId = :typeId")
    suspend fun deleteShipType(typeId: Int)
}