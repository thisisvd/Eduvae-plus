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
import com.digitalinclined.edugate.databinding.FragmentNotesBinding
import com.digitalinclined.edugate.models.SubjectRecyclerData
import com.digitalinclined.edugate.ui.fragments.MainActivity

class NotesFragment : Fragment(R.layout.fragment_notes) {

    // TAG
    private val TAG = "NotesFragment"

    // viewBinding
    private lateinit var binding: FragmentNotesBinding

    // Adapters
    lateinit var recyclerAdapter: SubjectRecyclerAdapter

    // toggle
    lateinit var toggle: ActionBarDrawerToggle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNotesBinding.bind(view)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Notes"

        // toggle btn toolbar setup
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
            setOnItemClickListener {
                val bundle = bundleOf(
                    "previousFragment" to "NotesFragment"
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