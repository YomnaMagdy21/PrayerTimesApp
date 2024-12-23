package com.example.prayertimesapp.secondpage.view

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

                         if(day == today){
                            Log.i("TAG", "Today matched: $day")
                            val fajr = data.data.get(0).timings.Fajr.replace(Regex("\\s?\\(EET\\)"), " AM")
                            binding.fajerTime.text = fajr
                            val sunrise = data.data.get(0).timings.Sunrise.replace(Regex("\\s?\\(EET\\)"), " AM")
                            binding.sunriseTime.text = sunrise
                            val duhur = data.data.get(0).timings.Dhuhr.replace(Regex("\\s?\\(EET\\)"), " PM")
                            binding.dhuhrTime.text = duhur
                            val asr = data.data.get(0).timings.Asr.replace(Regex("\\s?\\(EET\\)"), " PM")
                            binding.asrTime.text = asr
                            val maghrib = data.data.get(0).timings.Maghrib.replace(Regex("\\s?\\(EET\\)"), " PM")
                            binding.maghribTime.text = maghrib
                            val isha = data.data.get(0).timings.Isha.replace(Regex("\\s?\\(EET\\)"), " PM")
                            binding.ishaTime.text = isha
                        }



                       // daysAdapter.submitList(data)
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


}