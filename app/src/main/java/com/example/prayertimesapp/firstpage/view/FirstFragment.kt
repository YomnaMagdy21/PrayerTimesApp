package com.example.prayertimesapp.firstpage.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.prayertimesapp.R
import com.example.prayertimesapp.databinding.FragmentFirstBinding
import com.example.prayertimesapp.secondpage.view.PrayerTimesFragment
import com.example.prayertimesapp.utility.PreferenceManager
import com.example.prayertimesapp.utility.SharedPreference
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.util.Calendar
import java.util.Locale


class FirstFragment : Fragment() {

    lateinit var binding:FragmentFirstBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preferenceManager = PreferenceManager(requireContext())
        if (preferenceManager.isFirstTime()) {
            binding.imageViewPrev.visibility = View.GONE
            // Update the flag after navigating
            preferenceManager.setFirstTime(false)
        } else{
            binding.imageViewPrev.visibility = View.VISIBLE

            binding.imageViewPrev.setOnClickListener{
                val secondFragment =PrayerTimesFragment()
                val transaction=requireActivity().supportFragmentManager
                    .beginTransaction()
                transaction.replace(R.id.main,secondFragment)
                transaction.commit()
            }
        }


        val cityList = listOf(
            "Alex", "Aswan", "London", "Dubai", "Paris", "Tokyo", "Berlin", "Sydney", "Rome", "Barcelona", "Moscow",
            "Istanbul", "Madrid", "Melbourne", "Beijing", "Singapore", "Amsterdam", "Toronto", "Lagos", "Bangkok",
            "Seoul", "Vienna", "Mumbai", "Jakarta", "Chicago", "Lisbon",
        )


        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, cityList)
        binding.cityInput.setAdapter(adapter)
        binding.cityInput.setOnItemClickListener{parent, view, position, id ->
            val selectedCityItem = parent.getItemAtPosition(position).toString()

            SharedPreference.saveCity(requireContext(),selectedCityItem)

        }

        val countryList = listOf(
            "Egypt", "UK", "UAE", "France", "Japan", "Germany", "Australia", "Italy", "Spain", "Russia", "Turkey", "Spain",
            "Australia", "China", "Singapore", "Netherlands", "Canada", "Nigeria", "Thailand", "SK", "Austria", "India",
            "Indonesia", "US", "Portugal"
        )


        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, countryList)
        binding.countryInput.setAdapter(adapter2)
        binding.countryInput.setOnItemClickListener{parent, view, position, id ->
            val selectedCountryItem = parent.getItemAtPosition(position).toString()

        //    Toast.makeText(requireContext(), "Selected: $selectedCountryItem", Toast.LENGTH_SHORT).show()
            SharedPreference.saveCountry(requireContext(),selectedCountryItem)

        }

        val calculationMethods = hashMapOf(
            "Jafari / Shia Ithna-Ashari" to 0,
            "University of Islamic Sciences, Karachi" to 1,
            "Islamic Society of North America" to 2,
            "Muslim World League" to 3,
            "Umm Al-Qura University, Makkah" to 4,
            "Egyptian General Authority of Survey" to 5,
            "Institute of Geophysics, University of Tehran" to 7,
            "Gulf Region" to 8,
            "Kuwait" to 9,
            "Qatar" to 10,
            "Majlis Ugama Islam Singapura, Singapore" to 12,
            "Union Organization islamic de France" to 12,
            "Diyanet İşleri Başkanlığı, Turkey" to 13,
            "Spiritual Administration of Muslims of Russia" to 14,
            "Moonsighting Committee Worldwide" to 15,
            "Dubai (experimental)" to 16,
            "Jabatan Kemajuan Islam Malaysia (JAKIM)" to 17,
            "Tunisia" to 18,
            "Algeria" to 19,
            "KEMENAG - Kementerian Agama Republik Indonesia" to 20,
            "Morocco" to 21,
            "Comunidade Islamica de Lisboa" to 22,
            "Ministry of Awqaf, Islamic Affairs and Holy Places, Jordan" to 23,
            "Custom" to 99
        )
        val methods = calculationMethods.keys.toList()

        val adapter3 = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, methods
        )

        binding.methodInput.setAdapter(adapter3)


        binding.methodInput.setOnItemClickListener { parent, view, position, id ->
            val selectedCountry = parent.getItemAtPosition(position).toString()
            val methodId = calculationMethods[selectedCountry]
           // Toast.makeText(requireContext(), "Selected $selectedCountry with Method ID $methodId", Toast.LENGTH_SHORT).show()
            if (methodId != null) {
                SharedPreference.saveMethod(requireContext(),methodId)
            }
        }

// go to prayer times
        binding.textButton.setOnClickListener {

            val selectedCity = binding.cityInput.text.toString()
            val selectedCountry = binding.countryInput.text.toString()
            val selectedMethod = binding.methodInput.text.toString()

            if (selectedCity.isEmpty()) {
                Snackbar.make(binding.root, "Please select a city", Snackbar.LENGTH_LONG).show()
            } else if (selectedCountry.isEmpty()) {
                Snackbar.make(binding.root, "Please select a country", Snackbar.LENGTH_LONG).show()        }
            else if (selectedMethod.isEmpty()) {
                Snackbar.make(binding.root, "Please select a method", Snackbar.LENGTH_LONG).show()        }

            else {
                // Save the selected city
                SharedPreference.saveCity(requireContext(), selectedCity)
                SharedPreference.saveCountry(requireContext(), selectedCountry)
                val methodId = calculationMethods[selectedMethod]
                // Toast.makeText(requireContext(), "Selected $selectedCountry with Method ID $methodId", Toast.LENGTH_SHORT).show()
                if (methodId != null) {
                    SharedPreference.saveMethod(requireContext(),methodId)
                }
                // Continue with further logic
                val secondFragment =PrayerTimesFragment()
                val transaction=requireActivity().supportFragmentManager
                    .beginTransaction()
                transaction.replace(R.id.main,secondFragment)
                transaction.commit()
            }




        }

        // Save the selected option
        binding.cardView1.findViewById<RadioGroup>(R.id.radioGroupAlert)
            .setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.notification -> {
                        SharedPreference.saveAlarm(requireContext(), "n")
                    }
                    R.id.alarm -> {
                        SharedPreference.saveAlarm(requireContext(), "a")
                    }
                }
            }

// Restore the selected option
        val savedAlarm = SharedPreference.getAlarm(requireContext()) // Retrieve the saved value
        when (savedAlarm) {
            "n" -> binding.cardView1.findViewById<RadioButton>(R.id.notification).isChecked = true
            "a" -> binding.cardView1.findViewById<RadioButton>(R.id.alarm).isChecked = true
        }



    }




}