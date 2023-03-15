package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.VideosListAdapter
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentVideosListBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.digitalinclined.edugate.ui.fragments.YoutubeVideoActivity
import com.digitalinclined.edugate.ui.viewmodel.MainViewModel
import com.digitalinclined.edugate.utils.Resource

class VideosListFragment : Fragment() {

    // TAG
    private val TAG = "VideosListFragment"

    // view binding
    private var _binding: FragmentVideosListBinding? = null
    private val binding get() = _binding!!

    // toggle button
    private lateinit var toggle: ActionBarDrawerToggle

    // view model
    private val viewModel: MainViewModel by activityViewModels()

    // args
    private val args: VideosListFragmentArgs by navArgs()

    // Adapters
    private lateinit var recyclerAdapter: VideosListAdapter

    // subject selection var
    private var selectedSubject = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVideosListBinding.inflate(inflater, container, false)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Recommended Videos"
        toggle = (activity as MainActivity).toggle
        toggle.isDrawerIndicatorEnabled = false
        val drawable = requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
        toggle.setHomeAsUpIndicator(drawable)
        Constants.IS_BACK_TOOLBAR_BTN_ACTIVE = true

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            // set up recycler view
            setupRecyclerView()

            // setup adapters
            setUpAdaptersForSpinners()

            // view model observers
            viewModelObservers()
        }
    }

    private fun setUpAdaptersForSpinners() {
        binding.apply {

            if (args != null && args.branchesData.branchCode!!.isNotEmpty()) {
                val subjectsLists = ArrayList<String>()
                for (item in args.branchesData.majorSubjects!!) {
                    subjectsLists.add(item)
                }

                // adapter for course list
                var courseAdapter = ArrayAdapter(
                    requireContext(),
                    R.layout.drop_down_list_view,
                    subjectsLists
                )

                // course spinner adapter
                chooseCourseAutoTextView.apply {
                    setAdapter(
                        courseAdapter
                    )

                    // on change listener
                    setOnItemClickListener { _, _, i, _ ->
                        selectedSubject = subjectsLists[i]
                        if (!selectedSubject.isNullOrEmpty()) {
                            viewModel.getYoutubeResult(selectedSubject,"IN")
                        }
                    }

                    // set default subject & default request
                    setText(subjectsLists[0],false)
                    selectedSubject = subjectsLists[0]

                    if (!selectedSubject.isNullOrEmpty()) {
                        viewModel.getYoutubeResult(selectedSubject,"IN")
                    }
                }
            }
        }
    }

    // view model observers
    private fun viewModelObservers() {
        binding.apply {

            // observe search youtube api
            viewModel.getYoutubeSearchResult.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let { videosList ->
                            if (!videosList.isNullOrEmpty()) {
                                recyclerAdapter.differ.submitList(videosList)
                            }
                        }
                        progressBar.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        response.message?.let { message ->
                            Log.e(TAG, "An error occurred : $message")
                        }
                        progressBar.visibility = View.GONE
                    }
                    is Resource.Loading -> {
                        Log.d(TAG, "Youtube search api Loading!")
                        progressBar.visibility = View.VISIBLE
                    }
                }
            }

        }
    }

    // Recycler view setup
    private fun setupRecyclerView(){
        recyclerAdapter = VideosListAdapter()
        binding.apply {
            recyclerView.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }
        // on click listener
        recyclerAdapter.apply {
            setOnItemClickListener {
                val intent = Intent(requireActivity(), YoutubeVideoActivity::class.java)
                intent.putExtra("videoItem", it)
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}