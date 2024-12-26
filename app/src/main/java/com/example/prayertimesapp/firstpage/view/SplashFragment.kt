package com.example.prayertimesapp.firstpage.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.prayertimesapp.R
import com.example.prayertimesapp.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {

    lateinit var binding : FragmentSplashBinding
//    private lateinit var locationAccess: LocationAccess
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Add a delay for the splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            // Navigate to the next screen
            findNavController().navigate(R.id.prayerTimesFragment)
        }, 5000)
    }



}