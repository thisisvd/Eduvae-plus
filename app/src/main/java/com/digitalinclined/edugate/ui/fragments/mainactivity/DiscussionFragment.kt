package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentDiscussionBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity

class DiscussionFragment : Fragment(R.layout.fragment_discussion) {

    // viewBinding
    private lateinit var binding: FragmentDiscussionBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view,savedInstanceState)
        binding = FragmentDiscussionBinding.bind(view)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Discussion Form"



    }

}