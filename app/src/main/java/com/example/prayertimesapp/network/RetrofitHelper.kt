package com.example.prayertimesapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

    const val BASE_URL="http://api.aladhan.com/v1/"

    val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

    val apiService: PrayerTimesServices by lazy {
        retrofitInstance.create(PrayerTimesServices::class.java)
    }
}