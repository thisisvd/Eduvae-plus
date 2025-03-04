package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.FollowingRecyclerAdapter
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.constants.Constants.FOLLOWING_USER_ID
import com.digitalinclined.edugate.databinding.FragmentFollowingBinding
import com.digitalinclined.edugate.models.UserFollowingProfile
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FollowingFragment : Fragment() {

    // TAG
    private val TAG = "FollowingFragment"

    // viewBinding
    private var _binding: FragmentFollowingBinding? = null
    private val binding get() = _binding!!

    // Adapters
    lateinit var recyclerAdapter: FollowingRecyclerAdapter

    // toggle button
    private lateinit var toggle: ActionBarDrawerToggle

    // Firebase
    private var firebaseAuth = Firebase.auth

    // firebase db
    private val db = Firebase.firestore
    private val dbReference = db.collection("users")

    // TEMP List of userFollowingProfile
    private var followedUsersList = arrayListOf<UserFollowingProfile>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowingBinding.inflate(inflater, container, false)
        binding.apply {

            // change the title bar
            (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Followings"

            // toggle btn toolbar setup
            toggle = (activity as MainActivity).toggle
            val drawable =
                requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
            toggle.setHomeAsUpIndicator(drawable)
            Constants.IS_BACK_TOOLBAR_BTN_ACTIVE = true

            // setting up recycler view
            setupRecyclerView()

            // viewModel observers for fetching clients
            viewModelObservers()

        }
        return binding.root
    }

    // viewModel Observers
    private fun viewModelObservers() {

        // following users list observer
        FOLLOWING_USER_ID.observe(viewLifecycleOwner) { users ->
            if (users.isNotEmpty()) {
                Log.d(TAG, "Triggered list size : ${users.size}")
                followedUsersList.clear()
                for (userID in users) {
                    fetchFollowingUsersFromFirebase(userID)
                }
            } else {
                binding.progressBar.visibility = View.GONE
                followedUsersList.clear()
                recyclerAdapter.differ.submitList(followedUsersList)
            }
        }

    }

    // fetch particular users from firebase by using their UID
    private fun fetchFollowingUsersFromFirebase(userID: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            dbReference.document(userID).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userProfile = document.toObject(UserFollowingProfile::class.java)!!
                        userProfile.userID = userID
                        Log.d(TAG, userProfile.name.toString())
                        if (userProfile.name != null) {
                            followedUsersList.add(userProfile)
                            recyclerAdapter.differ.submitList(followedUsersList)
                            recyclerAdapter.notifyDataSetChanged()
                            binding.progressBar.visibility = View.GONE
                        }
                    } else {
                        Log.d(TAG, "No such document")
                        binding.progressBar.visibility = View.GONE
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                    binding.progressBar.visibility = View.GONE
                }
        }
        hideProgressBar()
    }

    // add following IDs
    private fun deleteFollowingUserIDs(userFirebaseID: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            dbReference.document(firebaseAuth.currentUser!!.uid)
                .update("following", FieldValue.arrayRemove(userFirebaseID))
                .addOnSuccessListener {
                    Log.d(TAG, "User UnFollowed Successfully!")
                    (activity as MainActivity).fetchFirebaseUserData()
                    binding.progressBar.visibility = View.GONE
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error in following user Successfully", e)
                    binding.progressBar.visibility = View.GONE
                }
        }
    }

    // Recycler view setup
    private fun setupRecyclerView() {
        recyclerAdapter = FollowingRecyclerAdapter()
        binding.apply {
            recyclerView.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }

        // on click listener
        recyclerAdapter.setOnItemClickListener { userProfile ->
            if (!userProfile.userID.isNullOrEmpty()) {
                showAlertForUnFollowUser(userProfile.name!!.split(" ")[0], userProfile.userID!!)
            }
        }
    }

    // show alert before deletion of users
    private fun showAlertForUnFollowUser(name: String, userID: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Unfollow $name!")
            .setMessage("Are you sure you want to unfollow $name from your following list?")
            .setPositiveButton("Yes") { _, _ ->
                deleteFollowingUserIDs(userID)
                showProgressBar()
            }
            .setNegativeButton("No", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    // show progress bar
    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    // hide progress bar
    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    // on view destroy
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}