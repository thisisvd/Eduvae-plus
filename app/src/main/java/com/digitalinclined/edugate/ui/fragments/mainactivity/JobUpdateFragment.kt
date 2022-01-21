package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentJobUpdateBinding

class JobUpdateFragment : Fragment(R.layout.fragment_job_update) {

    // viewBinding
    private lateinit var binding: FragmentJobUpdateBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentJobUpdateBinding.bind(view)

        binding.apply {



        }

    }

}