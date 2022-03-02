package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.SubjectRecyclerAdapter
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentSyllabusBinding
import com.digitalinclined.edugate.models.SubjectRecyclerData
import com.digitalinclined.edugate.ui.fragments.MainActivity

class SyllabusFragment : Fragment(R.layout.fragment_syllabus) {

    // TAG
    private val TAG = "SyllabusFragment"

    // viewBinding
    private lateinit var binding: FragmentSyllabusBinding

    // Adapters
    lateinit var recyclerAdapter: SubjectRecyclerAdapter

    // toggle
    lateinit var toggle: ActionBarDrawerToggle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSyllabusBinding.bind(view)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Syllabus"

        // toggle setup
        toggle = (activity as MainActivity).toggle
        toggle.isDrawerIndicatorEnabled = false
        val drawable = requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
        toggle.setHomeAsUpIndicator(drawable)
        Constants.IS_BACK_TOOLBAR_BTN_ACTIVE = true

        // getting the name
        binding.nameTV.text = (requireActivity() as MainActivity).sharedPreferences.getString(Constants.USER_NAME,"")

        // set up recycler view
        setupRecyclerView()

        // on click listener
        recyclerAdapter.apply {
            setOnItemClickListener {
                val bundle = bundleOf(
                    "previousFragment" to "SyllabusFragment"
                )
                findNavController().navigate(R.id.PDFFragment,bundle)
            }
        }

        // viewModels
        viewModelObservers()

    }

    // viewModel observers
    private fun viewModelObservers() {
        val list = arrayListOf(
            SubjectRecyclerData("E","English"),
            SubjectRecyclerData("H","Hindi"),
            SubjectRecyclerData("M","Maths"),
            SubjectRecyclerData("P","Physics"),
            SubjectRecyclerData("C","Chemistry")
        )
        recyclerAdapter.differ.submitList(list)
    }

    // Recycler view setup
    private fun setupRecyclerView(){
        recyclerAdapter = SubjectRecyclerAdapter()
        binding.apply {
            recyclerView.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }
    }

}