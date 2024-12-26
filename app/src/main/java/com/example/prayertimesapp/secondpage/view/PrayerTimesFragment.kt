package com.example.prayertimesapp.secondpage.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prayertimesapp.DaysAdapter
import com.example.prayertimesapp.R
import com.example.prayertimesapp.database.PrayerTimesLocalDataSourceImp
import com.example.prayertimesapp.databinding.FragmentPrayerTimesBinding
import com.example.prayertimesapp.firstpage.view.FirstFragment
import com.example.prayertimesapp.model.DateInfo
import com.example.prayertimesapp.model.PrayerData
import com.example.prayertimesapp.model.PrayerTimes
import com.example.prayertimesapp.model.PrayerTimesRepositoryImp
import com.example.prayertimesapp.model.PrayerTimings
import com.example.prayertimesapp.network.PrayerTimesRemoteDataSourceImp
import com.example.prayertimesapp.secondpage.viewmodel.PrayerTimesViewModel
import com.example.prayertimesapp.secondpage.viewmodel.PrayerTimesViewModelFactory
import com.example.prayertimesapp.utility.ApiState
import com.example.prayertimesapp.utility.NetworkConnection
import com.example.prayertimesapp.utility.SharedPreference
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Calendar
import java.util.Locale


class PrayerTimesFragment : Fragment() ,OnDayClickListener {

    lateinit var binding: FragmentPrayerTimesBinding
    lateinit var daysAdapter: DaysAdapter
    lateinit var prayerTimesViewModel: PrayerTimesViewModel
    lateinit var prayerTimesViewModelFactory: PrayerTimesViewModelFactory

    lateinit var city: String
    lateinit var country: String
    var method: Int = 0
    private val LOCATION_PERMISSION_REQUEST_CODE = 101
    lateinit var geocoder: Geocoder
    private lateinit var fusedClient: FusedLocationProviderClient
    var locationRequestID = 5

    private lateinit var locationCallback: LocationCallback

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prayerTimesViewModelFactory = PrayerTimesViewModelFactory(
            PrayerTimesRepositoryImp.getInstance(
                PrayerTimesRemoteDataSourceImp.getInstance(),
                PrayerTimesLocalDataSourceImp(requireContext())
            )
        )

        prayerTimesViewModel = ViewModelProvider(
            this,
            prayerTimesViewModelFactory
        ).get(PrayerTimesViewModel::class.java)



        // Initialize fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPrayerTimesBinding.inflate(inflater, container, false)
        // Check and request location permission
        geocoder = Geocoder(requireContext(), Locale.getDefault())

        fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireContext() as Activity,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), locationRequestID
            )

        }
        getFreshLocation()
        setUpRecyclerViewDay()

        city = SharedPreference.getCity(requireContext())
        country = SharedPreference.getCountry(requireContext())
        method = SharedPreference.getMethod(requireContext())

        Log.i("TAG", "City: $city, Country: $country, Method: $method")

        // Pass data to ViewModel
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH) + 1
        prayerTimesViewModel.getPrayerTimesForCurrentMonth(year, month, city, country, method)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageViewSetting.setOnClickListener {
            val firstFragment = FirstFragment()
            val transaction = requireActivity().supportFragmentManager
                .beginTransaction()
            transaction.replace(R.id.main, firstFragment)
            transaction.commit()
        }
        //check network connection if no get data from database
        if (NetworkConnection.checkNetworkConnection(requireContext())) {
            prayerTimesViewModel.deleteDataFromDatabase()
            Log.i("TAG", "check network: is available")
            lifecycleScope.launch {
                prayerTimesViewModel.prayerTimes.collectLatest { result ->
                    when (result) {
                        is ApiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is ApiState.Success<*> -> {
                            binding.progressBar.visibility = View.GONE
                            val data = result.data as? PrayerTimes
                            if (data != null) {
                                Log.i("TAG", "onViewCreated: ${result.data.toString()}")
                                val prayerTimes: List<PrayerTimes> = listOf(data)
                                Log.i("TAG", "onViewCreated2: ${prayerTimes}")

                                //   prayerTimesViewModel.insertDataInDatabase(data.data)
                                val prayerData: List<PrayerData> = data.data.map { prayerItem ->
                                    PrayerData(
                                        timings = PrayerTimings(
                                            Fajr = prayerItem.timings.Fajr,
                                            Sunrise = prayerItem.timings.Sunrise,
                                            Dhuhr = prayerItem.timings.Dhuhr,
                                            Asr = prayerItem.timings.Asr,
                                            Sunset = prayerItem.timings.Sunset,
                                            Maghrib = prayerItem.timings.Maghrib,
                                            Isha = prayerItem.timings.Isha,
                                            Imsak = prayerItem.timings.Imsak,
                                            Midnight = prayerItem.timings.Midnight
                                        ),
                                        date = DateInfo(
                                            readable = prayerItem.date.readable,
                                            timestamp = prayerItem.date.timestamp,
                                            gregorian = prayerItem.date.gregorian,
                                            hijri = prayerItem.date.hijri
                                        )
                                    )
                                }
                                // Insert the mapped data into the database
                                prayerTimesViewModel.insertDataInDatabase(prayerData)

                                val insertedData = prayerTimesViewModel.getPrayerDataFromDatabase()
                                Log.d("Database", "Inserted Data: $insertedData")
                                daysAdapter.submitList(data.data)
                                setUpData(data.data)


                            }

                        }

                        is ApiState.Failure -> {
                            val errorMessage = result.msg?.message ?: "Unknown error"
                            binding.progressBar.visibility = View.VISIBLE
                            Log.e("TAG", "Error fetching prayer times: $errorMessage", result.msg)


                        }
                    }
                }
            }
        } else {
            Log.i("TAG", "check network: nottttt available")
            prayerTimesViewModel.getPrayerDataFromDatabase()
            val databaseData = prayerTimesViewModel.getPrayerDataFromDatabase()
            Log.d("DatabaseCheck", "Data fetched from database: $databaseData")


            lifecycleScope.launch {
                prayerTimesViewModel.prayerTimes.collectLatest { result ->
                    when (result) {
                        is ApiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE

                        }

                        is ApiState.Success<*> -> {
                            binding.progressBar.visibility = View.GONE

                            val data = result.data as? List<PrayerData>

                            if (data != null) {
                                Log.i("TAG", "onViewCreated: ${result.data.toString()}")
//                                val prayerTimes: List<PrayerTimes> = listOf(data)
//                                Log.i("TAG", "onViewCreated2: ${prayerTimes}")

                                daysAdapter.submitList(data)
                                setUpData(data)
                            } else {
                                Log.i("TAG", "check network: noooo  data")

                            }

                        }

                        is ApiState.Failure -> {
                            val errorMessage = result.msg?.message ?: "Unknown error"
                            binding.progressBar.visibility = View.VISIBLE

                            Log.e("TAG", "Error fetching prayer times: $errorMessage", result.msg)

                        }
                    }
                }
            }
        }


    }


    private fun setUpData(data: List<PrayerData>) {

        val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val day = data.get(today - 1).date.gregorian.day.trim().toInt()

        // Update TextViews with the selected day's timings
        Log.i("TAG", "Today: $today, Day from Data: $day")
        val fajr = data.get(today - 1).timings.Fajr
        val fajrEdit = fajr.split(" ")[0] + " AM"
        val sunrise = data.get(today - 1).timings.Sunrise
        val sunriseEdit = sunrise.split(" ")[0] + " AM"
        val duhur = data.get(today - 1).timings.Dhuhr
        val duhurEdit = duhur.split(" ")[0] + " PM"
        val asr = data.get(today - 1).timings.Asr
        val asrEdit = asr.split(" ")[0] + " PM"
        val maghrib = data.get(today - 1).timings.Maghrib
        val maghribEdit = maghrib.split(" ")[0] + " PM"
        val isha = data.get(today - 1).timings.Isha
        val ishaEdit = isha.split(" ")[0] + " PM"

        if (day == today) {
            Log.i("TAG", "Today matched: $day")
            binding.fajerTime.text = fajrEdit
            binding.sunriseTime.text = sunriseEdit
            binding.dhuhrTime.text = duhurEdit
            binding.asrTime.text = asrEdit
            binding.maghribTime.text = maghribEdit
            binding.ishaTime.text = ishaEdit
            //daysAdapter.submitList(prayerTimes.get(0).data)

        }
        // daysAdapter.submitList(data)


        setupSwitchListeners(fajr, sunrise, duhur, asr, maghrib, isha)

        binding.fajrSwitch.setOnClickListener {
            if (binding.fajrSwitch.isChecked) {
                schedulePrayerNotifications(fajr, "الفجر")
            } else {
                cancelPrayerNotification("الفجر")
            }
        }
        binding.sunriseSwitch.setOnClickListener {
            if (binding.sunriseSwitch.isChecked) {
                schedulePrayerNotifications(sunrise, "الشروق")
            } else {
                cancelPrayerNotification("الشروق")
            }
        }
        binding.duhurSwitch.setOnClickListener {
            if (binding.duhurSwitch.isChecked) {
                schedulePrayerNotifications(duhur, "الظهر")
            } else {
                cancelPrayerNotification("الظهر")
            }
        }
        binding.asrSwitch.setOnClickListener {

            if (binding.asrSwitch.isChecked) {
                schedulePrayerNotifications(asr, "العصر")
            } else {
                cancelPrayerNotification("العصر")
            }
        }
        binding.maghribSwitch.setOnClickListener {
            if (binding.maghribSwitch.isChecked) {
                schedulePrayerNotifications(maghrib, "المغرب")
            } else {
                cancelPrayerNotification("المغرب")
            }
        }
        binding.ishaSwitch.setOnClickListener {
            if (binding.ishaSwitch.isChecked) {
                schedulePrayerNotifications(isha, "العشاء")
            } else {
                cancelPrayerNotification("العشاء")
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

    override fun changeDay(daySelected: Int) {

        lifecycleScope.launch {
            prayerTimesViewModel.prayerTimes.collectLatest { result ->
                when (result) {
                    is ApiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is ApiState.Success<*> -> {
                        val data = result.data as? List<PrayerData>

                        binding.progressBar.visibility = View.GONE
                        if (data != null) {

                            // Get the correct day's prayer times
                            val timings = data.get(daySelected - 1).timings

                            // Update TextViews with the selected day's timings
                            binding.fajerTime.text = timings.Fajr.split(" ")[0] + " AM"
                            binding.sunriseTime.text =
                                timings.Sunrise.split(" ")[0] + " AM"
                            binding.dhuhrTime.text =
                                timings.Dhuhr.split(" ")[0] + " PM"
                            binding.asrTime.text =
                                timings.Asr.split(" ")[0] + " PM"
                            binding.maghribTime.text =
                                timings.Maghrib.split(" ")[0] + " PM"
                            binding.ishaTime.text =
                                timings.Isha.split(" ")[0] + " PM"


                        }

                    }

                    is ApiState.Failure -> {
                        val errorMessage = result.msg?.message ?: "Unknown error"
                        binding.progressBar.visibility = View.VISIBLE
//                        Toast.makeText(requireContext(),"Failure!!!",Toast.LENGTH_SHORT).show()
                        Log.e("TAG", "Error fetching prayer times: $errorMessage", result.msg)

                    }
                }

            }
        }
    }


    // Setup listeners for child switches
    private fun setupSwitchListeners(
        fajr: String,
        sunrise: String,
        duhur: String,
        asr: String,
        maghrib: String,
        isha: String
    ) {

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
    private fun schedulePrayerNotifications(prayerTimes: String, prayerName: String) {
        val alarmManager: AlarmManager =
            requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager


        //  Parse the time into hour and minute
        val (hour, minute) = prayerTimes.replace(Regex("\\s?\\(.*?\\)"), "").split(":")
            .map { it.toInt() }

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

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        Log.i("AlarmManager", "Test alarm set for ${calendar.time}")
        //  Toast.makeText(requireContext(), "Test alarm set for ${calendar.time}", Toast.LENGTH_LONG).show()

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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
        } else {
            getFreshLocation()
        }
    }


    @SuppressLint("MissingPermission")
    fun getFreshLocation() {
        fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val locationRequest: LocationRequest = LocationRequest.Builder(100000000000).apply {
            setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val lastLocation = locationResult.lastLocation
                if (lastLocation != null) {
                    val latitude = lastLocation.latitude
                    val longitude = lastLocation.longitude

                    // Use Geocoder to get the city name
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    try {
                        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                        if (addresses != null && addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val city = address.locality // Gets the city name
                            val country = address.countryName // Gets the country name
                            // You can now use the city and country
                            Log.d("Location", "City: $city, Country: $country")
                            method = SharedPreference.getMethod(requireContext())

                            Log.i("TAG", "City: $city, Country: $country, Method: $method")

                            // Pass data to ViewModel
                            val year = Calendar.getInstance().get(Calendar.YEAR)
                            val month = Calendar.getInstance().get(Calendar.MONTH) + 1

                            prayerTimesViewModel.getPrayerTimesForCurrentMonth(
                                year,
                                month,
                                city,
                                country,
                                method
                            )

                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        // Handle exception (e.g., no network, etc.)
                    }
                }
            }
        }

        // First try to get the last known location
        fusedClient.lastLocation.addOnSuccessListener { lastLocation ->
            if (lastLocation != null) {
                val latitude = lastLocation.latitude
                val longitude = lastLocation.longitude
                // Proceed with the same Geocoder code as above  val geocoder = Geocoder(requireContext(), Locale.getDefault())
                try {
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    if (addresses != null && addresses.isNotEmpty()) {
                        val address = addresses[0]
                        val city = address.locality // Gets the city name
                        val country = address.countryName // Gets the country name
                        // You can now use the city and country
                        Log.d("Location", "City: $city, Country: $country")
                        method = SharedPreference.getMethod(requireContext())

                        Log.i("TAG", "City: $city, Country: $country, Method: $method")

                        // Pass data to ViewModel
                        val year = Calendar.getInstance().get(Calendar.YEAR)
                        val month = Calendar.getInstance().get(Calendar.MONTH) + 1

                        prayerTimesViewModel.getPrayerTimesForCurrentMonth(
                            year,
                            month,
                            city,
                            country,
                            method
                        )

                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    // Handle exception (e.g., no network, etc.)
                }
            }
                    else {
                        // Last location is unavailable, so request a fresh location update
                        fusedClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.myLooper()
                        )
                    }

                }
            }
        }









