package com.example.prayertimesapp.model



data class PrayerTimes(
    val code: Int,
    val status: String,
    val data: List<PrayerData>
)

data class PrayerData(
    val timings: PrayerTimings,
    val date: DateInfo,
    val meta: MetaData
)

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
    val timestamp: Long,
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
    val weekday: Weekday,
    val month: Month,
    val year: String,
    val designation: Designation,
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

data class MetaData(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val method: MethodInfo,
    val latitudeAdjustmentMethod: String,
    val midnightMode: String,
    val school: String,
    val offset: TimeOffset
)

data class MethodInfo(
    val id: Int,
    val name: String,
    val params: MethodParams
)

data class MethodParams(
    val Fajr: Double,
    val Isha: String
)

data class TimeOffset(
    val Imsak: String,
    val Fajr: String,
    val Sunrise: String,
    val Dhuhr: String,
    val Asr: String,
    val Maghrib: String,
    val Sunset: String,
    val Isha: String,
    val Midnight: String
)
