package com.digitalinclined.edugate.ui.fragments.setupactivity

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants.TEMP_CREATE_USER_EMAIL
import com.digitalinclined.edugate.constants.Constants.TEMP_CREATE_USER_NAME
import com.digitalinclined.edugate.databinding.FragmentSignUpScreenBinding

class SignUpScreenFragment : Fragment(R.layout.fragment_sign_up_screen) {

    // viewBinding
    private lateinit var binding: FragmentSignUpScreenBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignUpScreenBinding.bind(view)

        binding.apply {

            // navigate to OTP screen
            nextButton.setOnClickListener {
                nextButtonPressed()
            }

            // navigate to login screen
            login.setOnClickListener {
                findNavController().navigate(R.id.action_signUpScreenFragment_to_loginFragment)
            }

            // handling onBack Pressed
            onBack()

        }

    }

    // navigate to OTP screen
    private fun nextButtonPressed() {
        binding.apply {
            if (!isTextEmpty()) {
                // Data to be send to next fragment
                val bundle = Bundle().apply {
                    putString("fragment", "signUP")
                    putString("phone", phoneNumber.text.toString())
                }

                // Setting up the user detail's to temporary memory
                TEMP_CREATE_USER_NAME = nameTV.text.toString()
                TEMP_CREATE_USER_EMAIL = email.text.toString()

                // navigate to OTP Fragment with carry data in bundle
                findNavController().navigate(
                    R.id.action_signUpScreenFragment_to_OTPFragment,
                    bundle
                )
            }
        }
    }

    // check for empty edit texts
    private fun isTextEmpty(): Boolean {

        // Activate the edittext listener's
        isTextEmptyListeners()

        var isTextEmpty = false

        binding.apply {

            if (nameTV.text.isNullOrEmpty()) {
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
            } else if (phoneNumber.text.toString().length < 10) {
                isTextEmpty = true
                phoneNumberLayout.error = "*Please enter a valid phone number!"
            }

            return isTextEmpty
        }

    }

    // check for empty texts listener
    private fun isTextEmptyListeners() {
        binding.apply {

            nameTV.addTextChangedListener {
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
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event
            findNavController().popBackStack(R.id.onBoardingScreenFragment, false)
        }

    }

}