package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentPreviousYearPapersBinding

class PreviousYearPapersFragment : Fragment(R.layout.fragment_previous_year_papers) {

    // viewBinding
    private lateinit var binding: FragmentPreviousYearPapersBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPreviousYearPapersBinding.bind(view)



    }

}