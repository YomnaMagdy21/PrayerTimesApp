package com.example.prayertimesapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.prayertimesapp.model.PrayerData
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerTimesDAO {

    @Query("SELECT * FROM prayer_times")
    fun getPrayerData(): Flow<List<PrayerData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prayerData: List<PrayerData>): List<Long>

    @Query("DELETE FROM prayer_times")
    suspend fun delete(): Int

}