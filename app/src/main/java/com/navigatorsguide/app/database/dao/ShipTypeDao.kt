package com.navigatorsguide.app.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.navigatorsguide.app.database.entities.ShipType

@Dao
interface ShipTypeDao {

    @Query("SELECT * FROM ShipType")
    suspend fun getAllShipType(): List<ShipType>
}