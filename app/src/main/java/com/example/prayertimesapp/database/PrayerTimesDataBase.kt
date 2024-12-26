package com.example.prayertimesapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.prayertimesapp.model.PrayerData


@Database(entities = [PrayerData::class], version = 7 )
@TypeConverters(PrayerTimesDataConverter::class)
abstract class PrayerTimesDataBase : RoomDatabase() {
    abstract fun getPrayerTimesDao(): PrayerTimesDAO
    companion object{
        @Volatile
        private var INSTANCE: PrayerTimesDataBase? = null
        fun getInstance (ctx: Context): PrayerTimesDataBase{
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext, PrayerTimesDataBase::class.java, "prayer_times_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                instance }
        }
    }
}