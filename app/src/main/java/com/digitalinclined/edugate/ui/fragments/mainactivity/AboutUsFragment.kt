package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentAboutUsBinding
import com.digitalinclined.edugate.databinding.FragmentJobUpdateBinding

class AboutUsFragment : Fragment(R.layout.fragment_about_us) {

    // viewBinding
    private lateinit var binding: FragmentAboutUsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAboutUsBinding.bind(view)

        binding.apply {



        }

    }

}