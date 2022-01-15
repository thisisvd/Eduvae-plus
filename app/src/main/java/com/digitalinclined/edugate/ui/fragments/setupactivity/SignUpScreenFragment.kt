package com.digitalinclined.edugate.ui.fragments.setupactivity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentSignUpScreenBinding
import com.digitalinclined.edugate.databinding.FragmentSplashScreenBinding

class SignUpScreenFragment : Fragment(R.layout.fragment_sign_up_screen) {

    // viewBinding
    private lateinit var binding: FragmentSignUpScreenBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignUpScreenBinding.bind(view)

        binding.apply {

            // navigate to complete profile screen
            nextButton.setOnClickListener {
                if (!isTextEmpty()) {
                    val bundle = Bundle().apply {
                        putString("fragment", "signUP")
                        putString("phone",phoneNumber.text.toString())
                    }
                    findNavController().navigate(
                        R.id.action_signUpScreenFragment_to_OTPFragment,
                        bundle
                    )
                }
            }

            // navigate to login screen
            login.setOnClickListener {
                findNavController().navigate(R.id.action_signUpScreenFragment_to_loginFragment)
            }

            // handling onBack Pressed
            onBack()

        }

    }

    // check for empty edit texts
    private fun isTextEmpty(): Boolean {

        // Activate the edittext listener's
        isTextEmptyListeners()

        var isTextEmpty = false

        binding.apply {

            if (name.text.isNullOrEmpty()) {
                isTextEmpty = true
                nameLayout.error = "*Name can't be empty!"
            }

            if (email.text.isNullOrEmpty()) {
                isTextEmpty = true
                emailLayout.error = "*Valid email required!"
            }

            if (phoneNumber.text.isNullOrEmpty()) {
                isTextEmpty = true
                phoneNumberLayout.error = "*Phone number can't be empty!"
            } else if(phoneNumber.text.toString().length < 10) {
                isTextEmpty = true
                phoneNumberLayout.error = "*Please enter a valid phone number!"
            }

            return isTextEmpty
        }

    }

    // check for empty texts listener
    private fun isTextEmptyListeners() {
        binding.apply {

            name.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    nameLayout.error = "*Name can't be empty!"
                } else {
                    nameLayout.error = null
                }
            }

            email.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    emailLayout.error = "*Valid email required!"
                } else {
                    emailLayout.error = null
                }
            }

            phoneNumber.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    phoneNumberLayout.error = "*Phone number can't be empty!"
                } else {
                    phoneNumberLayout.error = null
                }
            }
        }
    }

    // handle onBack pressed
    private fun onBack() {
//        requireActivity()
//            .onBackPressedDispatcher
//            .addCallback(requireActivity(), object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    findNavController().navigate(R.id.action_signUpScreenFragment_to_onBoardingScreenFragment)
//                }
//            }
//            )

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event
            findNavController().popBackStack(R.id.onBoardingScreenFragment,false)
        }

    }

}