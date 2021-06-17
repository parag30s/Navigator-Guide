package com.navigatorsguide.app.database.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.navigatorsguide.app.utils.DateConverter
import java.util.*


@Entity(tableName = "SubSection")
data class SubSection(
    @PrimaryKey var subsid: Int, @NonNull var subsname: String?,
    @NonNull var subsparent: Int?,
    @NonNull var subLink: String?,
    @NonNull var sited: Int?,
    @NonNull var observations: String?,
    @NonNull var closureDate: String?,
    @NonNull var comments: String?,
    @NonNull var risk: Int?,
    @NonNull var status: Int?,
    @NonNull var note: String?,
    @NonNull var attachment_link: String?)