package com.example.prayertimesapp.model

import kotlinx.coroutines.flow.Flow

interface PrayerTimesRepository {

    fun getPrayerTimes(year:Int, month:Int, city:String, country:String, method:Int): Flow<PrayerTimes?>
}