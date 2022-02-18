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
import com.digitalinclined.edugate.adapter.PreviousYearsPaperAdapter
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentPreviousYearPapersBinding
import com.digitalinclined.edugate.models.PreviousYearPapersDataClass
import com.digitalinclined.edugate.ui.fragments.MainActivity

class PreviousYearPapersFragment : Fragment(R.layout.fragment_previous_year_papers) {

    // TAG
    private val TAG = "PreviousYearPapersFragment"

    // viewBinding
    private lateinit var binding: FragmentPreviousYearPapersBinding

    // Adapters
    lateinit var recyclerAdapter: PreviousYearsPaperAdapter

    // toggle
    lateinit var toggle: ActionBarDrawerToggle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPreviousYearPapersBinding.bind(view)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Question Papers"

        // toggle setup
        toggle = (activity as MainActivity).toggle
        toggle.isDrawerIndicatorEnabled = false
        val drawable = requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
        toggle.setHomeAsUpIndicator(drawable)
        Constants.IS_BACK_TOOLBAR_BTN_ACTIVE = true

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
                        val bundle = bundleOf(
                            "previousFragment" to "PreviousYearPapersFragment",
                            "fragmentPdfName" to "syllabusTV"
                        )
                        findNavController().navigate(R.id.PDFFragment,bundle)
                    }else {
                        val bundle = bundleOf(
                            "previousFragment" to "PreviousYearPapersFragment",
                            "fragmentPdfName" to "paper"
                        )
                        findNavController().navigate(R.id.PDFFragment,bundle)
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