package com.example.prayertimesapp.model

import com.example.prayertimesapp.network.PrayerTimesRemoteDataSource
import kotlinx.coroutines.flow.Flow

class PrayerTimesRepositoryImp(private var prayerTimesRemoteDataSource: PrayerTimesRemoteDataSource): PrayerTimesRepository {

    companion object{
        private var instance:PrayerTimesRepositoryImp?=null
        fun getInstance(
            weatherRemoteDataSource: PrayerTimesRemoteDataSource
        ):PrayerTimesRepositoryImp{
            return instance?: synchronized(this){
                val temp=PrayerTimesRepositoryImp(
                    weatherRemoteDataSource)
                instance=temp
                temp
            }
        }
    }

    override fun getPrayerTimes(
        year: Int,
        month: Int,
        city: String,
        country: String,
        method: Int
    ): Flow<PrayerTimes?> {
       return prayerTimesRemoteDataSource.getPrayerTimesOverNetwork(year,month,city,country,method)
    }
}