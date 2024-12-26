package com.example.prayertimesapp.utility

import android.content.Context
import android.content.SharedPreferences

object SharedPreference {

    fun saveCity(context: Context,name:String){
        val prefs = context.getSharedPreferences("cityPref", Context.MODE_PRIVATE)
        prefs.edit().putString("city",name).apply()
    }

    fun getCity(context: Context): String {
        val prefs = context.getSharedPreferences("cityPref", Context.MODE_PRIVATE)
        return prefs.getString("city", "") ?: "Alex"
    }

    fun saveCountry(context: Context,country:String){
        val prefs = context.getSharedPreferences("countryPref", Context.MODE_PRIVATE)
        prefs.edit().putString("country",country).apply()
    }

    fun getCountry(context: Context): String {
        val prefs = context.getSharedPreferences("countryPref", Context.MODE_PRIVATE)
        return prefs.getString("country", "") ?: "Egypt"
    }

    fun saveMethod(context: Context,number:Int){
        val prefs = context.getSharedPreferences("methodPref", Context.MODE_PRIVATE)
        prefs.edit().putInt("method",number).apply()
    }

    fun getMethod(context: Context): Int {
        val prefs = context.getSharedPreferences("methodPref", Context.MODE_PRIVATE)
        return prefs.getInt("method", 0) ?: 0
    }

    fun saveAlarm(context: Context,str:String){
        val prefs = context.getSharedPreferences("alarmPref", Context.MODE_PRIVATE)
        prefs.edit().putString("alarm",str).apply()
    }

    fun getAlarm(context: Context): String {
        val prefs = context.getSharedPreferences("alarmPref", Context.MODE_PRIVATE)
        return prefs.getString("alarm", "") ?: ""
    }



}


class PreferenceManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

    fun isFirstTime(): Boolean {
        return sharedPreferences.getBoolean("isFirstTime", true)
    }

    fun setFirstTime(isFirstTime: Boolean) {
        sharedPreferences.edit().putBoolean("isFirstTime", isFirstTime).apply()
    }
}
