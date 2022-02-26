package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.DiscussionRecyclerAdapter
import com.digitalinclined.edugate.databinding.FragmentDiscussionBinding
import com.digitalinclined.edugate.models.DiscussionDataClass
import com.digitalinclined.edugate.ui.fragments.MainActivity

class DiscussionFragment : Fragment(R.layout.fragment_discussion) {

    // viewBinding
    private lateinit var binding: FragmentDiscussionBinding

    // Adapters
    private lateinit var recyclerAdapter: DiscussionRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view,savedInstanceState)
        binding = FragmentDiscussionBinding.bind(view)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Discussion Form"

        binding.apply { 
            
            // set up recycler view
            setupRecyclerView()

            // fab onClick listener
            fab.setOnClickListener {
                findNavController().navigate(R.id.action_discussionFragment_to_addDiscussionFragment)
            }

            // view model init
            viewModelObservers()
            
        }

    }

    // viewModel observers
    private fun viewModelObservers() {
        val list = arrayListOf(
            DiscussionDataClass(
                "Aman Sahu",
                "Follow",
                "BA",
                "3rd Year",
                "3 Jan 2021",
                "https://images.unsplash.com/photo-1484515991647-c5760fcecfc7?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=449&q=80",
                "Question paper of Psychology (2019)",
                "Hey’s guys I would like to share the Nov. 2019 question paper of Psychology (Planning, Recruitment and Selection)",
                "Psychology Nov 2019.pdf",
                16,
                16
            ),
            DiscussionDataClass(
                "Sam Curran",
                "Follow",
                "B TECH",
                "3rd Year",
                "15 Jan 2021",
                "https://media.gettyimages.com/photos/england-all-rounder-sam-curran-pictured-at-the-fortress-hotel-ahead-picture-id1065723320?s=2048x2048",
                "Question paper of DBMS (2021)",
                "Hey’s guys I would like to share the Nov. 2021 question paper of Database and Management Systems (DBMS) paper.",
                "DBMS Nov 2021.pdf",
                26,
                8
            ),
            DiscussionDataClass(
                "Shubman Gill",
                "Follow",
                "BTECH",
                "2rd Year",
                "8 Jan 2021",
                "https://media.gettyimages.com/photos/shubman-gill-of-india-celebrates-his-century-during-the-icc-u19-cup-picture-id911936774?s=2048x2048",
                "Question paper of ADA (2020)",
                "Hey’s guys I would like to share the Nov. 2020 question paper of Analysis and Design of Algorithms(ADA).",
                "ADA Nov 2010.pdf",
                33,
                12
            )
        )
        recyclerAdapter.differ.submitList(list)
    }

    // Recycler view setup
    private fun setupRecyclerView(){
        recyclerAdapter = DiscussionRecyclerAdapter()
        binding.apply {

            val linearLayoutManager = LinearLayoutManager(activity)

            recyclerView.apply {
                adapter = recyclerAdapter
                layoutManager = linearLayoutManager
            }
        }
    }
    
}