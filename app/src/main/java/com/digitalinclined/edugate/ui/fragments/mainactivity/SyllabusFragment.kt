package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentSyllabusBinding

class SyllabusFragment : Fragment(R.layout.fragment_syllabus) {

    // viewBinding
    private lateinit var binding: FragmentSyllabusBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSyllabusBinding.bind(view)



    }

}