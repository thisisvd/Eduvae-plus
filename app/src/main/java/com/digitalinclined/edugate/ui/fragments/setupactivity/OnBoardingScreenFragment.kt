package com.digitalinclined.edugate.ui.fragments.setupactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentOnBoardingScreenBinding
import com.digitalinclined.edugate.databinding.FragmentSplashScreenBinding

class OnBoardingScreenFragment : Fragment(R.layout.fragment_on_boarding_screen) {

    // viewBinding
    private lateinit var binding: FragmentOnBoardingScreenBinding

    // temp onBackPressed count
    private var isBackPressedTwice = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOnBoardingScreenBinding.bind(view)

        binding.apply {

            // navigate to signup screen
            getStartedButton.setOnClickListener {
                isBackPressedTwice = false
                findNavController().navigate(R.id.action_onBoardingScreenFragment_to_signUpScreenFragment)
            }

            // navigate to login screen
            login.setOnClickListener {
                isBackPressedTwice = false
                findNavController().navigate(R.id.action_onBoardingScreenFragment_to_loginFragment)
            }

            // handle onBack Pressed
            onBack()

        }

    }

    // handle onBack pressed
    private fun onBack() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event

            if(isBackPressedTwice) {
                requireActivity().finish()
            }else {
                Toast.makeText(requireContext(),"Press back one more time to quit!",Toast.LENGTH_SHORT).show()
                isBackPressedTwice = true
            }

        }
    }

}


