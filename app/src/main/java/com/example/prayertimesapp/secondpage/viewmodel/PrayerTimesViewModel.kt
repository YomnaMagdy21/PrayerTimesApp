package com.example.prayertimesapp.secondpage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prayertimesapp.model.PrayerTimesRepository
import com.example.prayertimesapp.utility.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PrayerTimesViewModel  (private val _repo: PrayerTimesRepository): ViewModel(){

    private var _prayerTimes: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val prayerTimes:StateFlow<ApiState> = _prayerTimes

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

}