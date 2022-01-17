package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentDiscussionBinding

class DiscussionFragment : Fragment(R.layout.fragment_discussion) {

    // viewBinding
    private lateinit var binding: FragmentDiscussionBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view,savedInstanceState)
        binding = FragmentDiscussionBinding.bind(view)



    }

}