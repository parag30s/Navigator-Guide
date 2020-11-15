package com.navigatorsguide.app.database.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Questions")
data class Questions (@PrimaryKey var qid: Int,
                      var qtext: String,
                      @NonNull var ansType: String?,
                      @NonNull var link: String?,
                      @NonNull var description: String?,
                      @NonNull var ranks: String?,
                      @NonNull var shiptypes: String?,
                      @NonNull var viq: String?,
                      @NonNull var qparent: Int?)