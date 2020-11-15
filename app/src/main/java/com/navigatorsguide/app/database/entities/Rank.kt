package com.navigatorsguide.app.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Rank")
data class Rank(@PrimaryKey var rankid: Int, var rankName: String)