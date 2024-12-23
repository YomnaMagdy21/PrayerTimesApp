package com.example.prayertimesapp.network

import com.example.prayertimesapp.model.PrayerTimes
import kotlinx.coroutines.flow.Flow


interface PrayerTimesRemoteDataSource {
    fun getPrayerTimesOverNetwork(year:Int, month:Int, city:String, country:String, method:Int):Flow<PrayerTimes?>
}