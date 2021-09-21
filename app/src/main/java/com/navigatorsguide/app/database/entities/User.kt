package com.navigatorsguide.app.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class User(@PrimaryKey var fid: Int, var feature: String, var value: String)