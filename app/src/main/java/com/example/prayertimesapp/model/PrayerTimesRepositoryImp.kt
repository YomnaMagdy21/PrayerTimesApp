package com.example.prayertimesapp.model

import com.example.prayertimesapp.database.PrayerTimesLocalDataSource
import com.example.prayertimesapp.network.PrayerTimesRemoteDataSource
import kotlinx.coroutines.flow.Flow

class PrayerTimesRepositoryImp(private var prayerTimesRemoteDataSource: PrayerTimesRemoteDataSource,
    private var prayerTimesLocalDataSource: PrayerTimesLocalDataSource): PrayerTimesRepository {

    companion object{
        private var instance:PrayerTimesRepositoryImp?=null
        fun getInstance(
            prayerTimesRemoteDataSource: PrayerTimesRemoteDataSource,
            prayerTimesLocalDataSource: PrayerTimesLocalDataSource
        ):PrayerTimesRepositoryImp{
            return instance?: synchronized(this){
                val temp=PrayerTimesRepositoryImp(
                    prayerTimesRemoteDataSource, prayerTimesLocalDataSource)
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

    override suspend fun getPrayerDataInfo(): Flow<List<PrayerData>> {
        return prayerTimesLocalDataSource.getPrayerDataInfo()
    }

    override suspend fun insertData(prayerData: List<PrayerData>): List<Long>  {
        return prayerTimesLocalDataSource.insertData(prayerData)
    }

    override suspend fun deleteData(): Int {
        return prayerTimesLocalDataSource.deleteData()
    }
}