package com.example.prayertimesapp.network

import com.example.prayertimesapp.model.PrayerTimes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PrayerTimesServices {
    @GET("calendarByCity")
    suspend fun getPrayerTimesForTheMonth(
        @Path("year") year:Int,
        @Path("month") month:Int,
        @Query("city") city:String,
        @Query("country") country:String,
        @Query("method") method:Int):Response<PrayerTimes>
}