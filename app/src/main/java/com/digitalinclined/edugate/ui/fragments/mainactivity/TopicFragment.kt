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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.SubjectRecyclerAdapter
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentNotesBinding
import com.digitalinclined.edugate.databinding.FragmentSyllabusBinding
import com.digitalinclined.edugate.databinding.FragmentTopicsBinding
import com.digitalinclined.edugate.models.SubjectRecyclerData
import com.digitalinclined.edugate.ui.fragments.MainActivity

class TopicFragment : Fragment(R.layout.fragment_topics) {

    // viewBinding
    private lateinit var binding: FragmentTopicsBinding

    // Adapters
    lateinit var recyclerAdapter: SubjectRecyclerAdapter

    // toggle
    lateinit var toggle: ActionBarDrawerToggle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTopicsBinding.bind(view)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Syllabus"

        // toggle setup
        toggle = (activity as MainActivity).toggle
        toggle.isDrawerIndicatorEnabled = false
        val drawable = requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
        toggle.setHomeAsUpIndicator(drawable)
        Constants.IS_BACK_TOOLBAR_BTN_ACTIVE = true

        // getting the name
        binding.name.text = (requireActivity() as MainActivity).sharedPreferences.getString(Constants.USER_NAME,"")

        // set up recycler view
        setupRecyclerView()

        // on click listener
        recyclerAdapter.apply {
            setOnItemClickListener { value ->
                Toast.makeText(requireContext(),value.textMain,Toast.LENGTH_SHORT).show()
            }
        }

        // viewModels
        viewModelObservers()

    }

    // viewModel observers
    private fun viewModelObservers() {
        val list = arrayListOf(
            SubjectRecyclerData("1","Topic-1"),
            SubjectRecyclerData("2","Topic-2"),
            SubjectRecyclerData("3","Topic-3"),
            SubjectRecyclerData("4","Topic-4")
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