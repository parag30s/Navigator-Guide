package com.navigatorsguide.app.database.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SubSection")
data class SubSection (@PrimaryKey var subsid: Int, @NonNull var subsname: String?,
                      @NonNull var subsparent: Int?)