package com.example.prayertimesapp.firstpage.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.prayertimesapp.R
import com.example.prayertimesapp.databinding.FragmentFirstBinding


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

        val cityList = listOf(
            "Aswan",
            "Alex",
            "London",
            "New York",
            "Dubai"
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, cityList)
        binding.cityInput.setAdapter(adapter)
        binding.cityInput.setOnItemClickListener{parent, view, position, id ->
            val selectedCityItem = parent.getItemAtPosition(position).toString()

            Toast.makeText(requireContext(), "Selected: $selectedCityItem", Toast.LENGTH_SHORT).show()

        }

        val countryList = listOf(
            "Egypt",
            "UK",
            "USA",
            "",
            "UAE"
        )

        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, countryList)
        binding.countryInput.setAdapter(adapter2)
        binding.countryInput.setOnItemClickListener{parent, view, position, id ->
            val selectedCountryItem = parent.getItemAtPosition(position).toString()

            Toast.makeText(requireContext(), "Selected: $selectedCountryItem", Toast.LENGTH_SHORT).show()

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
            Toast.makeText(requireContext(), "Selected $selectedCountry with Method ID $methodId", Toast.LENGTH_SHORT).show()
        }

//        val calender = Calendar.getInstance()
//
//        val year = calender.get(Calendar.YEAR)
//        val month = calender.get(Calendar.MONTH) + 1




    }


}