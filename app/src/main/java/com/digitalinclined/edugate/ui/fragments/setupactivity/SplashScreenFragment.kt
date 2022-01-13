package com.digitalinclined.edugate.ui.fragments.setupactivity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentSplashScreenBinding

class SplashScreenFragment : Fragment(R.layout.fragment_splash_screen) {

    // viewBinding
    private lateinit var binding: FragmentSplashScreenBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSplashScreenBinding.bind(view)

        // delay Splash Screen for 5 sec
        object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Do something here in every sec
            }

            // After finish navigate to next fragment
            override fun onFinish() {
                findNavController().navigate(R.id.action_splashScreenFragment_to_onBoardingScreenFragment)
            }
        }.start()

    }


}