package com.example.prayertimesapp.firstpage.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.prayertimesapp.R
import com.example.prayertimesapp.databinding.FragmentSplashBinding
import com.example.prayertimesapp.utility.PreferenceManager
import com.example.prayertimesapp.utility.SharedPreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {


    lateinit var binding : FragmentSplashBinding
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

// Create an instance of the PreferenceManager
        val preferenceManager = PreferenceManager(requireContext())

        // Delay to show the splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if it's the first time using the app
            if (preferenceManager.isFirstTime()) {
                // Navigate to FragmentA (First-Time Screen)
                findNavController().navigate(R.id.action_splashFragment_to_fragmentA)


            } else {
                // Navigate to FragmentB (Regular Screen)
                findNavController().navigate(R.id.action_splashFragment_to_fragmentB)
            }
        }, 5000)

    }



}