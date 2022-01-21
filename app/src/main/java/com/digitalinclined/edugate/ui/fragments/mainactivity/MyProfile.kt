package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentMyprofileBinding

class MyProfile: Fragment(R.layout.fragment_myprofile) {

    // viewBinding
    private lateinit var binding: FragmentMyprofileBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyprofileBinding.bind(view)

        binding.apply {

            // back button pressed
            back.setOnClickListener {
                findNavController().popBackStack()
            }

        }

    }


}