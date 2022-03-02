package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.SubjectRecyclerAdapter
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentSubjectsBinding
import com.digitalinclined.edugate.databinding.SubjectComponentLayoutBinding
import com.digitalinclined.edugate.models.SubjectRecyclerData
import com.digitalinclined.edugate.ui.fragments.MainActivity

class SubjectsFragment : Fragment(R.layout.fragment_subjects) {

    // viewBinding
    private lateinit var binding: FragmentSubjectsBinding

    // Adapters
    lateinit var recyclerAdapter: SubjectRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSubjectsBinding.bind(view)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Subjects"

        // getting the name
        binding.nameTV.text = (requireActivity() as MainActivity).sharedPreferences.getString(
            Constants.USER_NAME,"")

        // set up recycler view
        setupRecyclerView()

        // on click listener
        recyclerAdapter.apply {
            setOnItemClickListener { value ->
                Toast.makeText(requireContext(),value.textMain, Toast.LENGTH_SHORT).show()
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