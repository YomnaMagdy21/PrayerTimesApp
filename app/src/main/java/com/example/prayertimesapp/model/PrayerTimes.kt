package com.example.prayertimesapp.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


data class PrayerTimes(
    val code: Int,
    val status: String,
    val data: List<PrayerData>
)

@Entity(tableName = "prayer_times")
data class PrayerData(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,  // Ensure a default value for auto-generated ID

    val timings: PrayerTimings,

    val date: DateInfo
)
//{
//    constructor(prayerTimings: PrayerTimings,date: DateInfo) : this(0,
//        PrayerTimings("","","","","","","","",""),
//        DateInfo("","",
//            GregorianDate("","","", Weekday("",""), Month(0,"",""),"", Designation("","")),
//            HijriDate("","","",Weekday("",""),Month(0,"",""),"",Designation("",""),holidays = null)
//        )
//    )
//}
data class PrayerTimings(
    val Fajr: String,
    val Sunrise: String,
    val Dhuhr: String,
    val Asr: String,
    val Sunset: String,
    val Maghrib: String,
    val Isha: String,
    val Imsak: String,
    val Midnight: String
)

data class DateInfo(
    val readable: String,
    val timestamp: String,

    val gregorian: GregorianDate,

    val hijri: HijriDate
)


data class GregorianDate(
    val date: String,
    val format: String,
    val day: String,
    val weekday: Weekday,
    val month: Month,
    val year: String,
    val designation: Designation
)

data class HijriDate(
    val date: String,
    val format: String,
    val day: String,
    val weekday: Weekday?,
    val month: Month?,
    val year: String,
    val designation: Designation?,
    val holidays: List<String>?
)

data class Weekday(
    val en: String,
    val ar: String?
)

data class Month(
    val number: Int,
    val en: String,
    val ar: String?
)

data class Designation(
    val abbreviated: String,
    val expanded: String
)





