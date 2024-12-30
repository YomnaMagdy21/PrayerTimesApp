package com.example.prayertimesapp.secondpage.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prayertimesapp.model.PrayerData
import com.example.prayertimesapp.model.PrayerTimesRepository
import com.example.prayertimesapp.utility.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrayerTimesViewModel  @Inject constructor(private val _repo: PrayerTimesRepository): ViewModel(){

    private var _prayerTimes: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val prayerTimes:StateFlow<ApiState> = _prayerTimes


    init {
        getPrayerDataFromDatabase()
    }
    fun getPrayerTimesForCurrentMonth( year: Int,
                                       month: Int,
                                       city: String,
                                       country: String,
                                       method: Int){
        viewModelScope.launch(Dispatchers.IO){
            _repo.getPrayerTimes(year,month,city,country,method)
                .catch {
                        e->
                    _prayerTimes.value= ApiState.Failure(e)
                }
                .collect{
                    _prayerTimes.value= ApiState.Success(it)
                }
        }
    }

    fun getPrayerDataFromDatabase(){
        viewModelScope.launch(Dispatchers.IO){
            _repo.getPrayerDataInfo()
                .catch {
                        e->
                    _prayerTimes.value= ApiState.Failure(e)
                }
                .collect{
                    _prayerTimes.value= ApiState.Success(it)
                }
        }
    }
    fun insertDataInDatabase(prayerData: List<PrayerData>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("Database", "Inserting data into the database")
                // Check the result of the insert operation
                val result = _repo.insertData(prayerData)
                Log.d("RoomInsert", "Inserted rows: ${result.size}")
                if (result.isNotEmpty()) {
                    Log.d("Database", "Data inserted successfully.")
                } else {
                    Log.d("Database", "No data inserted.")
                }
            } catch (e: Exception) {
                Log.e("Database", "Failed to insert prayer times", e)
            }
        }
    }



    fun deleteDataFromDatabase(){
        viewModelScope.launch(Dispatchers.IO){
            _repo.deleteData()
        }
    }

}