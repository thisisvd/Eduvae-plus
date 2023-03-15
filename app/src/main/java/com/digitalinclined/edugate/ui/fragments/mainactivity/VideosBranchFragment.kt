package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.BranchesListAdapter
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.constants.Constants.USER_CURRENT_COURSE
import com.digitalinclined.edugate.databinding.FragmentVideosBranchBinding
import com.digitalinclined.edugate.models.BranchListDataClass
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideosBranchFragment : Fragment() {

    // TAG
    private val TAG = "VideosBranchFragment"

    // view binding
    private var _binding: FragmentVideosBranchBinding? = null
    private val binding get() = _binding!!

    // toggle button
    private lateinit var toggle: ActionBarDrawerToggle

    // Adapters
    private lateinit var recyclerAdapter: BranchesListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVideosBranchBinding.inflate(inflater, container, false)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Select Branch/Course"
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

            // fetch branch/course data
            fetchBranchesListFromFirebase()

        }
    }

    // fetch branches list data from firebase
    private fun fetchBranchesListFromFirebase() {
        var branchList = ArrayList<BranchListDataClass>()
        lifecycleScope.launch(Dispatchers.IO) {
            if (!USER_CURRENT_COURSE.isNullOrEmpty()) {
                Firebase.firestore.collection("extraData/branchesDetails/${USER_CURRENT_COURSE}")
                    .get()
                    .addOnSuccessListener { documentResult ->
                        if (documentResult != null) {
                            Log.d(
                                TAG,
                                "DocumentSnapshot data size : ${documentResult.documents.size}"
                            )
                            for (document in documentResult) {
                                val dataClass = document.toObject(BranchListDataClass::class.java)!!
                                branchList.add(dataClass)
                            }
                            Log.d(TAG, "List size : ${branchList.size}")
                            if (branchList.isNotEmpty()) {
                                recyclerAdapter.differ.submitList(branchList)
                            } else {
                                Snackbar.make(
                                    binding.root,
                                    "No branches fetched!",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                            binding.progressBar.visibility = View.GONE
                        }
                    }.addOnFailureListener { e ->
                        Log.d(TAG, "Error adding document", e)
                        Snackbar.make(binding.root, "Error occurred!", Snackbar.LENGTH_LONG).show()
                        binding.progressBar.visibility = View.GONE
                    }
            }
        }
    }

    // Recycler view setup
    private fun setupRecyclerView(){
        recyclerAdapter = BranchesListAdapter()
        binding.apply {
            recyclerView.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }
        // on click listener
        recyclerAdapter.apply {
            setOnItemClickListener {
                val bundle = bundleOf(
                    "branchesData" to it
                )
                findNavController().navigate(R.id.action_videosBranchFragment_to_videosListFragment,bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}