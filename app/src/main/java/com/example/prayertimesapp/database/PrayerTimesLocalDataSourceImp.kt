package com.example.prayertimesapp.database

import android.content.Context
import android.util.Log
import com.example.prayertimesapp.model.PrayerData
import kotlinx.coroutines.flow.Flow

class PrayerTimesLocalDataSourceImp (context: Context): PrayerTimesLocalDataSource {

    private val dao:PrayerTimesDAO by lazy {
        val db:PrayerTimesDataBase=PrayerTimesDataBase.getInstance(context)
        db.getPrayerTimesDao()
    }
    override suspend fun getPrayerDataInfo(): Flow<List<PrayerData>> {
        return dao.getPrayerData()
    }

    override suspend fun insertData(prayerData: List<PrayerData>): List<Long>  {
       return dao.insert(prayerData)

    }

    override suspend fun deleteData(): Int {
        return dao.delete()
    }
}