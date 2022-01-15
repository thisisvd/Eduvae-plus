package com.digitalinclined.edugate.ui.fragments.setupactivity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentLoginBinding

class LoginFragment: Fragment(R.layout.fragment_login) {

    // viewBinding
    private lateinit var binding: FragmentLoginBinding

    // temp login first time open variable
    private var isOpenFirstTime = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        binding.apply {

            // verify button listener
            verifyWithOTP.setOnClickListener {
                onVerificationClicked()
            }

            // navigate to signup screen
            signUP.setOnClickListener {
                findNavController().navigate(
                    R.id.action_loginFragment_to_signUpScreenFragment
                )
            }
        }

    }

    // on verification button clicked
    private fun onVerificationClicked() {

        binding.apply {

            // check for layout visibility
            if(phoneNumberLayout.isVisible && !isTextEmpty()){

                val bundle = Bundle().apply {
                    putString("fragment", "login")
                    putString("phone",phoneNumber.text.toString())
                }
                findNavController().navigate(R.id.action_loginFragment_to_OTPFragment, bundle)

            } else {
                if(isOpenFirstTime) {
                    // Animate loginLinearLayout
                    ObjectAnimator.ofFloat(loginLinearLayout, "translationY", 222f).apply {
                        duration = 800
                        start()
                    }

                    // Animate phone number Layout
                    val fadeInAnimation =
                        AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_slide_down)
                    phoneNumberLayout.startAnimation(fadeInAnimation)
                    phoneNumberLayout.visibility = View.VISIBLE

                    isOpenFirstTime = false
                }
            }

        }

    }

    // check for empty edit texts
    private fun isTextEmpty(): Boolean {
        var isTextEmpty = false

        binding.apply {

            // Activate the edittext listener's
            phoneNumber.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    phoneNumberEdittextLayout.error = "*Phone number can't be empty!"
                } else {
                    phoneNumberEdittextLayout.error = null
                }
            }

            // checks for empty or null
            if (phoneNumber.text.isNullOrEmpty()) {
                isTextEmpty = true
                phoneNumberEdittextLayout.error = "*Phone number can't be empty!"
            } else if(phoneNumber.text.toString().length < 10) {
                isTextEmpty = true
                phoneNumberEdittextLayout.error = "*Please enter a valid phone number!"
            }

            return isTextEmpty
        }
    }


}