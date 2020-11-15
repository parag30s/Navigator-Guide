package com.navigatorsguide.app.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ShipType")
data class ShipType (@PrimaryKey var typeId: Int, var typeName: String)