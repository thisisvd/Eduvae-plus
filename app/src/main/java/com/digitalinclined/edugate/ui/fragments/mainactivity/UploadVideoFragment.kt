package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
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

            // adapter init
            adapterForSpinners()

            // upload
            upload.setOnClickListener {
                Toast.makeText(requireContext(),"Will open gallery and select pdf to upload",Toast.LENGTH_SHORT).show()
            }

        }
    }

    // Setting adapters for Spinner / AutoTextView
    private fun adapterForSpinners(){
        binding.apply {

            // adapter for course list
            var adapter = ArrayAdapter(
                requireContext(),
                R.layout.drop_down_list_view,
                arrayListOf("Question Paper", "Notes", "Sample Paper")
            )

            // course spinner adapter
            chooseAutoTextView.apply {
                setAdapter(
                    adapter
                )
            }

        }
    }

}