package com.example.prayertimesapp.utility

import android.content.Context

object SharedPreference {

    fun saveCity(context: Context,name:String){
        val prefs = context.getSharedPreferences("cityPref", Context.MODE_PRIVATE)
        prefs.edit().putString("city",name).apply()
    }

    fun getCity(context: Context): String {
        val prefs = context.getSharedPreferences("cityPref", Context.MODE_PRIVATE)
        return prefs.getString("city", "") ?: ""
    }

    fun saveCountry(context: Context,country:String){
        val prefs = context.getSharedPreferences("countryPref", Context.MODE_PRIVATE)
        prefs.edit().putString("country",country).apply()
    }

    fun getCountry(context: Context): String {
        val prefs = context.getSharedPreferences("countryPref", Context.MODE_PRIVATE)
        return prefs.getString("country", "") ?: ""
    }

    fun saveMethod(context: Context,number:Int){
        val prefs = context.getSharedPreferences("methodPref", Context.MODE_PRIVATE)
        prefs.edit().putInt("method",number).apply()
    }

    fun getMethod(context: Context): Int {
        val prefs = context.getSharedPreferences("methodPref", Context.MODE_PRIVATE)
        return prefs.getInt("method", 0) ?: 0
    }
}