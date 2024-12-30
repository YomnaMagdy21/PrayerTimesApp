//package com.example.prayertimesapp.secondpage.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.example.prayertimesapp.model.PrayerTimesRepository
//
//class PrayerTimesViewModelFactory (private  var _repo: PrayerTimesRepository) :
//    ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return if (modelClass.isAssignableFrom(PrayerTimesViewModel::class.java)) {
//            PrayerTimesViewModel(_repo) as T
//        } else {
//            throw IllegalArgumentException("ViewModel class not found")
//
//        }
//    }
//}