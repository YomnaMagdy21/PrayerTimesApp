package com.example.prayertimesapp.network

import com.example.prayertimesapp.model.PrayerTimes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PrayerTimesRemoteDataSourceImp @Inject constructor(private val prayerTimesServices:PrayerTimesServices): PrayerTimesRemoteDataSource {

//    private val prayerTimesServices:PrayerTimesServices by lazy {
//        RetrofitHelper.retrofitInstance.create(PrayerTimesServices::class.java)
//    }
//
//    companion object{
//        private var instance:PrayerTimesRemoteDataSourceImp?=null
//        fun getInstance():PrayerTimesRemoteDataSourceImp{
//            return instance?: synchronized(this){
//                val temp=PrayerTimesRemoteDataSourceImp()
//                instance=temp
//                temp
//            }
//        }
//    }
    override fun getPrayerTimesOverNetwork(
        year: Int,
        month: Int,
        city: String,
        country: String,
        method: Int
    ): Flow<PrayerTimes?> {
        return flow {
            emit(prayerTimesServices.getPrayerTimesForTheMonth(year,month,city,country,method).body())
        }
    }
}