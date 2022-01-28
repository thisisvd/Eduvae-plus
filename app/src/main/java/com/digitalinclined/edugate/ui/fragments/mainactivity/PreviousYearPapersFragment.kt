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
import com.digitalinclined.edugate.adapter.PreviousYearsPaperAdapter
import com.digitalinclined.edugate.adapter.SubjectRecyclerAdapter
import com.digitalinclined.edugate.databinding.FragmentPreviousYearPapersBinding
import com.digitalinclined.edugate.models.PreviousYearPapersDataClass
import com.digitalinclined.edugate.models.SubjectRecyclerData
import com.digitalinclined.edugate.ui.fragments.MainActivity

class PreviousYearPapersFragment : Fragment(R.layout.fragment_previous_year_papers) {

    // viewBinding
    private lateinit var binding: FragmentPreviousYearPapersBinding

    // Adapters
    lateinit var recyclerAdapter: PreviousYearsPaperAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPreviousYearPapersBinding.bind(view)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Question Papers"

        // set up recycler view
        setupRecyclerView()

        // set up onClickListeners
        setUpOnClickListeners()

        // viewModels
        viewModelObservers()

    }

    // set up on Click listeners
    private fun setUpOnClickListeners() {
        binding.apply {
            // on click listener
            recyclerAdapter.apply {
                setOnItemClickListener { strLink, value ->

                    if (value == "syllabusTV") {
                        Toast.makeText(requireContext(), "Syllabus is clicked : $strLink", Toast.LENGTH_SHORT)
                            .show()
                    }else {
                        Toast.makeText(requireContext(), "View Paper is clicked : $strLink", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    // viewModel observers
    private fun viewModelObservers() {
        val list = arrayListOf(
            PreviousYearPapersDataClass(true,"Nov. 2019","3rd Sem History Paper (P-III)",
            19,90,80,"www.cbse.com","syllabus link/ screen",
            "English, Hindi"),
            PreviousYearPapersDataClass(true,"Nov. 2019","3rd Sem History Paper (P-III)",
                19,90,80,"www.cbse.com","syllabus link/ screen",
                "English, Hindi"),
            PreviousYearPapersDataClass(true,"Nov. 2019","3rd Sem History Paper (P-III)",
                19,90,80,"www.cbse.com","syllabus link/ screen",
                "English, Hindi"),
            PreviousYearPapersDataClass(true,"Nov. 2019","3rd Sem History Paper (P-III)",
                19,90,80,"www.cbse.com","syllabus link/ screen",
                "English, Hindi"),
            PreviousYearPapersDataClass(true,"Nov. 2019","3rd Sem History Paper (P-III)",
                19,90,80,"www.cbse.com","syllabus link/ screen",
                "English, Hindi"),)

        recyclerAdapter.differ.submitList(list)
    }

    // Recycler view setup
    private fun setupRecyclerView(){
        recyclerAdapter = PreviousYearsPaperAdapter()
        binding.apply {
            recyclerView.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }
    }

}