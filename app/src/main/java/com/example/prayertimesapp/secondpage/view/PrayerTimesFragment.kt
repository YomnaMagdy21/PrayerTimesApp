package com.example.prayertimesapp.secondpage.view

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prayertimesapp.DaysAdapter
import com.example.prayertimesapp.R
import com.example.prayertimesapp.databinding.FragmentFirstBinding
import com.example.prayertimesapp.databinding.FragmentPrayerTimesBinding
import com.example.prayertimesapp.model.DateInfo
import com.example.prayertimesapp.model.PrayerTimes
import com.example.prayertimesapp.model.PrayerTimesRepositoryImp
import com.example.prayertimesapp.network.PrayerTimesRemoteDataSourceImp
import com.example.prayertimesapp.secondpage.viewmodel.PrayerTimesViewModel
import com.example.prayertimesapp.secondpage.viewmodel.PrayerTimesViewModelFactory
import com.example.prayertimesapp.utility.ApiState
import com.example.prayertimesapp.utility.SharedPreference
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar


class PrayerTimesFragment : Fragment() ,OnDayClickListener{

    lateinit var binding: FragmentPrayerTimesBinding
    lateinit var daysAdapter: DaysAdapter
    lateinit var prayerTimesViewModel: PrayerTimesViewModel
    lateinit var prayerTimesViewModelFactory: PrayerTimesViewModelFactory

    lateinit var city:String
    lateinit var country:String
    var method:Int = 0
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prayerTimesViewModelFactory = PrayerTimesViewModelFactory(
            PrayerTimesRepositoryImp.getInstance(
                PrayerTimesRemoteDataSourceImp.getInstance())
            )

        prayerTimesViewModel = ViewModelProvider(this, prayerTimesViewModelFactory).get(PrayerTimesViewModel::class.java)
        val calender = Calendar.getInstance()

        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH) + 1
        city = SharedPreference.getCity(requireContext())
        country = SharedPreference.getCountry(requireContext())
        method = SharedPreference.getMethod(requireContext())
        Log.i("TAG", "onCreate:${year} ")
        Log.i("TAG", "onCreate:${month} ")
        Log.i("TAG", "onCreate:${city} ")
        Log.i("TAG", "onCreate:${country} ")
        Log.i("TAG", "onCreate:${method} ")
        prayerTimesViewModel.getPrayerTimesForCurrentMonth(year,month,city,country,method)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPrayerTimesBinding.inflate(inflater, container, false)
        setUpRecyclerViewDay()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        lifecycleScope.launch {
            prayerTimesViewModel.prayerTimes.collectLatest { result ->
                when(result){
                    is ApiState.Loading ->{
                        Toast.makeText(requireContext(),"Loading...",Toast.LENGTH_SHORT).show()
                    }
                    is ApiState.Success<*> ->{
                        val data = result.data as PrayerTimes
                        Log.i("TAG", "onViewCreated: ${result.data.toString()}")
                        val prayerTimes: List<PrayerTimes> = listOf(data)
                        Log.i("TAG", "onViewCreated2: ${prayerTimes}")
                        daysAdapter.submitList(data.data)

                        val today =  Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                        val day = data.data.get(today-1).date.gregorian.day.trim().toInt()

                        Log.i("TAG", "Today: $today, Day from Data: $day")
                        val fajr= data.data.get(0).timings.Fajr
                        val fajrEdit = fajr.replace(Regex("\\s?\\(EET\\)"), " AM")
                        val sunrise = data.data.get(0).timings.Sunrise
                        val sunriseEdit = sunrise.replace(Regex("\\s?\\(EET\\)"), " AM")
                        val duhur = data.data.get(0).timings.Dhuhr
                        val duhurEdit = duhur.replace(Regex("\\s?\\(EET\\)"), " PM")
                        val asr= data.data.get(0).timings.Asr
                        val asrEdit = asr.replace(Regex("\\s?\\(EET\\)"), " PM")
                        val maghrib = data.data.get(0).timings.Maghrib
                        val maghribEdit = maghrib.replace(Regex("\\s?\\(EET\\)"), " PM")
                        val isha = data.data.get(0).timings.Isha
                        val ishaEdit = isha.replace(Regex("\\s?\\(EET\\)"), " PM")

                         if(day == today){
                            Log.i("TAG", "Today matched: $day")
                            binding.fajerTime.text = fajrEdit
                            binding.sunriseTime.text = sunriseEdit
                            binding.dhuhrTime.text = duhurEdit
                            binding.asrTime.text = asrEdit
                            binding.maghribTime.text = maghribEdit
                            binding.ishaTime.text = ishaEdit
                        }


                        setupSwitchListeners("20:45",sunrise,"20:37","20:38",maghrib,"20:39")

                        binding.fajrSwitch.setOnClickListener {
                            if (binding.fajrSwitch.isChecked) {
                                schedulePrayerNotifications("20:36", "الفجر")
                            } else{
                                cancelPrayerNotification("الفجر")
                            }
                        }
                        binding.sunriseSwitch.setOnClickListener {
                            if (binding.sunriseSwitch.isChecked) {
                                schedulePrayerNotifications(sunrise, "الشروق")
                            } else{
                                cancelPrayerNotification("الشروق")
                            }
                        }
                        binding.duhurSwitch.setOnClickListener {
                            if (binding.duhurSwitch.isChecked) {
                                schedulePrayerNotifications("20:37", "الظهر")
                            } else{
                                cancelPrayerNotification("الظهر")
                            }
                        }
                        binding.asrSwitch.setOnClickListener {

                            if (binding.asrSwitch.isChecked) {
                                schedulePrayerNotifications("20:38", "العصر")
                            } else{
                                cancelPrayerNotification("العصر")
                            }
                        }
                        binding.maghribSwitch.setOnClickListener {
                            if (binding.maghribSwitch.isChecked) {
                                schedulePrayerNotifications(maghrib, "المغرب")
                            } else{
                                cancelPrayerNotification("المغرب")
                            }
                        }
                        binding.ishaSwitch.setOnClickListener {
                            if (binding.ishaSwitch.isChecked) {
                                schedulePrayerNotifications("20:39", "العشاء")
                            } else {
                                cancelPrayerNotification("العشاء")
                            }
                        }

                    }
                    is ApiState.Failure ->{
                        val errorMessage = result.msg?.message ?: "Unknown error"
                        Toast.makeText(requireContext(),"Failure!!!",Toast.LENGTH_SHORT).show()
                        Log.e("TAG", "Error fetching prayer times: $errorMessage", result.msg)

                    }
                }

            }
        }




    }

    private fun setUpRecyclerViewDay() {
        daysAdapter = DaysAdapter(this)
        binding.recViewDay.apply {
            adapter = daysAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        }

    }

    override fun changeDay(dayChosen: Int) {
        lifecycleScope.launch {
            prayerTimesViewModel.prayerTimes.collectLatest { result ->
                when(result){
                    is ApiState.Loading ->{
                        Toast.makeText(requireContext(),"Loading...",Toast.LENGTH_SHORT).show()
                    }
                    is ApiState.Success<*> ->{
                        val data = result.data as PrayerTimes


                        // Get the correct day's prayer times
                        val timings = data.data[dayChosen - 1].timings

                        // Update TextViews with the selected day's timings
                        binding.fajerTime.text = timings.Fajr.replace(Regex("\\s?\\(EET\\)"), " AM")
                        binding.sunriseTime.text = timings.Sunrise.replace(Regex("\\s?\\(EET\\)"), " AM")
                        binding.dhuhrTime.text = timings.Dhuhr.replace(Regex("\\s?\\(EET\\)"), " PM")
                        binding.asrTime.text = timings.Asr.replace(Regex("\\s?\\(EET\\)"), " PM")
                        binding.maghribTime.text = timings.Maghrib.replace(Regex("\\s?\\(EET\\)"), " PM")
                        binding.ishaTime.text = timings.Isha.replace(Regex("\\s?\\(EET\\)"), " PM")




                    }
                    is ApiState.Failure ->{
                        val errorMessage = result.msg?.message ?: "Unknown error"
                        Toast.makeText(requireContext(),"Failure!!!",Toast.LENGTH_SHORT).show()
                        Log.e("TAG", "Error fetching prayer times: $errorMessage", result.msg)

                    }
                }

            }
        }
    }


    // Setup listeners for child switches
    private fun setupSwitchListeners(fajr:String,sunrise:String,duhur:String,asr:String,maghrib:String,isha:String) {

        // Iterate through the switches and manage notifications dynamically
        val prayerTimes = mapOf(
            binding.fajrSwitch to Pair(fajr, "الفجر"),
            binding.sunriseSwitch to Pair(sunrise, "الشروق"),
            binding.duhurSwitch to Pair(duhur, "الظهر"),
            binding.asrSwitch to Pair(asr, "العصر"),
            binding.maghribSwitch to Pair(maghrib, "المغرب"),
            binding.ishaSwitch to Pair(isha, "العشاء")
        )

        prayerTimes.forEach { (switch, prayerData) ->
            val (time, prayerName) = prayerData
            if (switch.isChecked) {
                schedulePrayerNotifications(time, prayerName)
            } else {
                cancelPrayerNotification(prayerName)
            }
        }



    }

    @SuppressLint("ScheduleExactAlarm")
    private fun schedulePrayerNotifications(prayerTimes:String,prayerName:String) {
        val alarmManager:AlarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager


            //  Parse the time into hour and minute
            val (hour, minute) = prayerTimes.replace(Regex("\\s?\\(.*?\\)"), "").split(":").map { it.toInt() }

            val intent = Intent(requireContext(), PrayerTimesNotificationReceiver::class.java)
            intent.putExtra("PRAYER_NAME", prayerName)
            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                prayerName.hashCode(), // Unique request code for each prayer
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            // Set the alarm for the specific prayer time
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)

                // Ensure time is in the future
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

            Log.i("AlarmManager", "Test alarm set for ${calendar.time}")
            Toast.makeText(requireContext(), "Test alarm set for ${calendar.time}", Toast.LENGTH_LONG).show()

        }

    private fun cancelPrayerNotification(prayerName: String) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), PrayerTimesNotificationReceiver::class.java).apply {
            putExtra("PRAYER_NAME", prayerName)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            prayerName.hashCode(), // Unique request code for each prayer
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        pendingIntent?.let { alarmManager.cancel(it) }
    }





}