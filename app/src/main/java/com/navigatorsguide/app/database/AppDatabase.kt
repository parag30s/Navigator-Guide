package com.navigatorsguide.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.navigatorsguide.app.database.dao.*
import com.navigatorsguide.app.database.entities.*
import kotlinx.coroutines.CoroutineScope

@Database(entities = arrayOf(
    Rank::class,
    ShipType::class,
    Section::class,
    SubSection::class,
    Questions::class), version = 1)
abstract class AppDatabase: RoomDatabase() {

    companion object{
        @Volatile private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator  fun invoke(context: Context) = instance?: synchronized(LOCK){
            instance?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context)
                = Room.databaseBuilder(context, AppDatabase::class.java, "navguide.db")
            .createFromAsset("database/NavGuide.db")
            .build()
    }

    abstract fun getRankDao(): RankDao

    abstract fun getShipTypeDao(): ShipTypeDao

    abstract fun getSectionDao(): SectionDao

    abstract fun getSubSectionDao(): SubSectionDao

    abstract fun getQuestionsDao(): QuestionsDao
}