package com.example.prayertimesapp.secondpage.view

import android.os.Bundle
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


class PrayerTimesFragment : Fragment() {

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
                        val data = result.data as? MutableList<PrayerTimes>?

                        daysAdapter.submitList(data)
                    }
                    is ApiState.Failure ->{
                        Toast.makeText(requireContext(),"Failure!!!",Toast.LENGTH_SHORT).show()

                    }
                }

            }
        }
    }

    private fun setUpRecyclerViewDay() {
        daysAdapter = DaysAdapter()
        binding.recViewDay.apply {
            adapter = daysAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        }

    }


}