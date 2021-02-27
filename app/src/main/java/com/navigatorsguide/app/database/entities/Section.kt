package com.navigatorsguide.app.database.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Section")
data class Section (@PrimaryKey var sectionid: Int, @NonNull var sectionName: String?,
                    @NonNull var sectionThumbnail: String?, @NonNull var eligibleRank: String?,
    @NonNull var eligibleShipType: String?)