package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.SubjectRecyclerAdapter
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentSyllabusBinding
import com.digitalinclined.edugate.databinding.FragmentYearOfPapersBinding
import com.digitalinclined.edugate.models.SubjectRecyclerData
import com.digitalinclined.edugate.ui.fragments.MainActivity

class YearOfPapersFragment : Fragment(R.layout.fragment_year_of_papers) {

    // viewBinding
    private lateinit var binding: FragmentYearOfPapersBinding

    // Adapters
    lateinit var recyclerAdapter: SubjectRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentYearOfPapersBinding.bind(view)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Year of Papers"

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
            SubjectRecyclerData("1","Year-1"),
            SubjectRecyclerData("2","Year-2"),
            SubjectRecyclerData("3","Year-3"),
            SubjectRecyclerData("4","Year-4"),
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