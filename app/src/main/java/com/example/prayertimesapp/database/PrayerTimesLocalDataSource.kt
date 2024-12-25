package com.example.prayertimesapp.database

import com.example.prayertimesapp.model.PrayerData
import kotlinx.coroutines.flow.Flow

interface PrayerTimesLocalDataSource {

    suspend fun getPrayerDataInfo():Flow<List<PrayerData>>
    suspend fun insertData(prayerData: List<PrayerData>): List<Long>
    suspend fun deleteData():Int
}