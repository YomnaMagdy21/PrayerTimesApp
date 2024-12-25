package com.example.prayertimesapp.model

import kotlinx.coroutines.flow.Flow

interface PrayerTimesRepository {

    fun getPrayerTimes(year:Int, month:Int, city:String, country:String, method:Int): Flow<PrayerTimes?>

    suspend fun getPrayerDataInfo():Flow<List<PrayerData>>
    suspend fun insertData(prayerData: List<PrayerData>): List<Long>
        suspend fun deleteData():Int
}