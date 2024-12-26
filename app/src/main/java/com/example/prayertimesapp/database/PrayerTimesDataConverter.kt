package com.example.prayertimesapp.database

import androidx.room.TypeConverter
import com.example.prayertimesapp.model.DateInfo
import com.example.prayertimesapp.model.Designation
import com.example.prayertimesapp.model.Month
import com.example.prayertimesapp.model.PrayerTimings
import com.example.prayertimesapp.model.Weekday
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PrayerTimesDataConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromPrayerTimings(timings: PrayerTimings?): String? {
        return gson.toJson(timings)
    }

    @TypeConverter
    fun toPrayerTimings(jsonString: String?): PrayerTimings? {
        val type = object : TypeToken<PrayerTimings>() {}.type
        return gson.fromJson<PrayerTimings>(jsonString, type)
    }

    @TypeConverter
    fun fromDateInfo(date: DateInfo?): String? {
        return gson.toJson(date)
    }

    @TypeConverter
    fun toDateInfo(jsonString: String?): DateInfo? {
        val type = object : TypeToken<DateInfo>() {}.type
        return gson.fromJson<DateInfo>(jsonString, type)
    }
//    @TypeConverter
//    fun fromWeekday(weekday: Weekday?): String {
//        return gson.toJson(weekday)
//    }
//
//    @TypeConverter
//    fun toWeekday(json: String): Weekday? {
//        val type = object : TypeToken<Weekday>() {}.type
//        return gson.fromJson(json, type)
//    }
//
//    @TypeConverter
//    fun fromMonth(month: Month?): String {
//        return gson.toJson(month)
//    }
//
//    @TypeConverter
//    fun toMonth(json: String): Month? {
//        val type = object : TypeToken<Month>() {}.type
//        return gson.fromJson(json, type)
//    }
//
//    @TypeConverter
//    fun fromDesignation(designation: Designation?): String {
//        return gson.toJson(designation)
//    }
//
//    @TypeConverter
//    fun toDesignation(json: String): Designation? {
//        val type = object : TypeToken<Designation>() {}.type
//        return gson.fromJson(json, type)
//    }
//
//    @TypeConverter
//    fun fromStringList(list: List<String>?): String {
//        return gson.toJson(list)
//    }
//
//    @TypeConverter
//    fun toStringList(json: String): List<String>? {
//        val type = object : TypeToken<List<String>>() {}.type
//        return gson.fromJson(json, type)
//    }
}