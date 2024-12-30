package com.example.prayertimesapp.di

import android.content.Context
import com.example.prayertimesapp.database.PrayerTimesDAO
import com.example.prayertimesapp.database.PrayerTimesDataBase
import com.example.prayertimesapp.network.PrayerTimesServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class FirstModuleProvider {

    @Provides
    fun provideRetrofit():PrayerTimesServices{
        return Retrofit.Builder()
            .baseUrl("https://api.aladhan.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(PrayerTimesServices::class.java)
    }

    @Provides
    fun providerPrayerTimesDao(@ApplicationContext appContext: Context):PrayerTimesDAO{
        return PrayerTimesDataBase.getInstance(appContext).getPrayerTimesDao()
    }
}