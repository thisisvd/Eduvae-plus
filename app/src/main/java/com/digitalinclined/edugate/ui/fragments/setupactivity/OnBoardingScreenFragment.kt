package com.digitalinclined.edugate.ui.fragments.setupactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentOnBoardingScreenBinding
import com.digitalinclined.edugate.databinding.FragmentSplashScreenBinding

class OnBoardingScreenFragment : Fragment(R.layout.fragment_on_boarding_screen) {

    // viewBinding
    private lateinit var binding: FragmentOnBoardingScreenBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOnBoardingScreenBinding.bind(view)

        binding.apply {

            // navigate to signup screen
            getStartedButton.setOnClickListener {
                findNavController().navigate(R.id.action_onBoardingScreenFragment_to_signUpScreenFragment)
            }

            // navigate to login screen
            login.setOnClickListener {
                findNavController().navigate(R.id.action_onBoardingScreenFragment_to_loginFragment)
            }

        }

    }


}