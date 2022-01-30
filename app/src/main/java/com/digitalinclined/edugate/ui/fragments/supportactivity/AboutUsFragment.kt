package com.digitalinclined.edugate.ui.fragments.supportactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentAboutUsBinding
import com.digitalinclined.edugate.ui.fragments.SupportActivity

class AboutUsFragment : Fragment(R.layout.fragment_about_us) {

    // viewBinding
    private lateinit var binding: FragmentAboutUsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAboutUsBinding.bind(view)

        // change the title bar
        (activity as SupportActivity).findViewById<TextView>(R.id.toolbarTitle).text = "About Us"

        binding.apply {




        }

    }

}