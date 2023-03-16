package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.ScoreBoardRecyclerAdapter
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentFollowingBinding
import com.digitalinclined.edugate.models.UserFollowingProfile
import com.digitalinclined.edugate.models.quizzesmodel.ClassWorkSubmissionDataClass
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScoreBoardFragment : Fragment() {

    // TAG
    private val TAG = "ScoreBoardFragment"

    // viewBinding
    private var _binding: FragmentFollowingBinding? = null
    private val binding get() = _binding!!

    // Adapters
    lateinit var recyclerAdapter: ScoreBoardRecyclerAdapter

    // toggle button
    private lateinit var toggle: ActionBarDrawerToggle

    // Firebase
    private var firebaseAuth = Firebase.auth

    // args
    private val args: ScoreBoardFragmentArgs by navArgs()

    // TEMP List of userFollowingProfile
    private var followedUsersList = arrayListOf<UserFollowingProfile>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFollowingBinding.inflate(inflater, container, false)
        binding.apply {

            // change the title bar
            (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text =
                "Score Board"

            // toggle btn toolbar setup
            toggle = (activity as MainActivity).toggle
            val drawable =
                requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
            toggle.setHomeAsUpIndicator(drawable)
            Constants.IS_BACK_TOOLBAR_BTN_ACTIVE = true

            // setting up recycler view
            setupRecyclerView()

            // fetch data
            if (!args.classroomID.isNullOrEmpty()) {
                fetchScoreBoardFromFirebase(args.classroomID)
            }

        }
        return binding.root
    }

    // fetch scoreboard
    private fun fetchScoreBoardFromFirebase(classID: String) {
        val scoreProfileList = ArrayList<ClassWorkSubmissionDataClass>()
        lifecycleScope.launch(Dispatchers.Main) {
            Firebase.firestore.collection("classroom").document(args.classroomID)
                .collection("classworkSubmissions")
                .orderBy("userTimeStamp", Query.Direction.DESCENDING).get()
                .addOnSuccessListener { documentResult ->
                    if (documentResult != null) {
                        for (document in documentResult) {
                            val dataClass =
                                document.toObject(ClassWorkSubmissionDataClass::class.java)
                            scoreProfileList.add(dataClass)
                        }

                        if (scoreProfileList.isNotEmpty()) {
                            recyclerAdapter.differ.submitList(scoreProfileList)
                        }

                    } else {
                        Log.d(TAG, "Error in processing!")
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(binding.root, "Error in processing!", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    binding.progressBar.visibility = View.GONE
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                    Snackbar.make(binding.root, "Error in processing!", Snackbar.LENGTH_SHORT)
                        .show()
                    binding.progressBar.visibility = View.GONE
                }
        }
    }

    // Recycler view setup
    private fun setupRecyclerView() {
        recyclerAdapter = ScoreBoardRecyclerAdapter()
        binding.apply {
            recyclerView.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }
    }

    // on view destroy
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}