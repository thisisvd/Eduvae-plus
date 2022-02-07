package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentUploadVideoBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity

class UploadVideoFragment : Fragment(R.layout.fragment_upload_video) {

    // viewBinding
    private lateinit var binding: FragmentUploadVideoBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUploadVideoBinding.bind(view)
        binding.apply {

            // change the title bar
            (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Upload A Video"


        }
    }

}