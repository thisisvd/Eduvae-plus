package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.constants.Constants.IS_BACK_TOOLBAR_BTN_ACTIVE
import com.digitalinclined.edugate.databinding.FragmentUploadVideoBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity

class UploadVideoFragment : Fragment(R.layout.fragment_upload_video) {

    // viewBinding
    private lateinit var binding: FragmentUploadVideoBinding

    // toggle
    lateinit var toggle: ActionBarDrawerToggle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUploadVideoBinding.bind(view)
        binding.apply {

            // change the title bar
            (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Upload A Video"

            // toggle btn toolbar setup
            toggle = (activity as MainActivity).toggle
            toggle.isDrawerIndicatorEnabled = false
            val drawable = requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
            toggle.setHomeAsUpIndicator(drawable)
            IS_BACK_TOOLBAR_BTN_ACTIVE = true

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