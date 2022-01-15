package com.digitalinclined.edugate.ui.fragments.setupactivity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentSplashScreenBinding
import com.digitalinclined.edugate.ui.fragments.SetupActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreenFragment : Fragment(R.layout.fragment_splash_screen) {

    // viewBinding
    private lateinit var binding: FragmentSplashScreenBinding

    // firebase
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSplashScreenBinding.bind(view)

        // firebase init
        firebaseAuth = FirebaseAuth.getInstance()

        // delay Splash Screen for 5 sec
        object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Do something here in every sec
            }

            // After finish navigate to next fragment
            override fun onFinish() {
                if (firebaseAuth.currentUser != null) {
//                    findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment)
                } else {
                    startActivity(Intent(requireActivity(), SetupActivity::class.java))
                    requireActivity().finish()
                }
                Toast.makeText(requireContext(),"Chala",Toast.LENGTH_SHORT).show()
            }
        }.start()

    }


}