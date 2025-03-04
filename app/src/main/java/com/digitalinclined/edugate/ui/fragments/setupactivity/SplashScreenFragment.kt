package com.digitalinclined.edugate.ui.fragments.setupactivity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants.IS_SPLASH_SCREEN_FIRST_SHOWED
import com.digitalinclined.edugate.databinding.FragmentSplashScreenBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreenFragment : Fragment(R.layout.fragment_splash_screen) {

    // viewBinding
    private lateinit var binding: FragmentSplashScreenBinding

    // firebase auth & user
    private var firebaseAuth = FirebaseAuth.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSplashScreenBinding.bind(view)

        // firebase init
        firebaseAuth = FirebaseAuth.getInstance()

        if (IS_SPLASH_SCREEN_FIRST_SHOWED) {

            // setting fragment 2nd showed
            IS_SPLASH_SCREEN_FIRST_SHOWED = false

            // delay Splash Screen for 5 sec
            object : CountDownTimer(5000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // Do something here in every sec
                }

                // After finish navigate to next fragment
                override fun onFinish() {
                    if (firebaseAuth.currentUser != null) {
                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                        requireActivity().finish()
                    } else {
                        findNavController().navigate(R.id.action_splashScreenFragment_to_onBoardingScreenFragment)
                    }
                }
            }.start()

        } else {
            findNavController().navigate(R.id.action_splashScreenFragment_to_onBoardingScreenFragment)
        }

    }

}