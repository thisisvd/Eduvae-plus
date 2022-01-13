package com.digitalinclined.edugate.ui.fragments.setupactivity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentLoginBinding
import com.digitalinclined.edugate.databinding.FragmentOtpBinding

class OTPFragment: Fragment(R.layout.fragment_otp) {

    // viewBinding
    private lateinit var binding: FragmentOtpBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOtpBinding.bind(view)

        binding.apply {

            // verify button listener
            verifyButton.setOnClickListener {
                Toast.makeText(requireContext(),"Rough Verified!",Toast.LENGTH_SHORT).show()
            }

            // navigate to signup screen
            signUP.setOnClickListener {
                findNavController().navigate(R.id.action_OTPFragment_to_signUpScreenFragment)
            }

        }

    }

}