package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.DiscussionRecyclerAdapter
import com.digitalinclined.edugate.constants.Constants.FOLLOWING_USER_ID
import com.digitalinclined.edugate.data.viewmodel.LocalViewModel
import com.digitalinclined.edugate.databinding.FragmentDiscussionBinding
import com.digitalinclined.edugate.models.DiscussionDataClass
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiscussionFragment : Fragment(R.layout.fragment_discussion) {

    // TAG
    private val TAG = "DiscussionFragment"

    // viewBinding
    private lateinit var binding: FragmentDiscussionBinding

    // Adapters
    private lateinit var recyclerAdapter: DiscussionRecyclerAdapter

    // toggle button
    private lateinit var toggle: ActionBarDrawerToggle

    // Firebase
    private lateinit var firebaseAuth: FirebaseAuth

    // firebase db
    private val db = Firebase.firestore

    // enable the options menu in activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view,savedInstanceState)
        binding = FragmentDiscussionBinding.bind(view)

        // firebase init
        firebaseAuth = Firebase.auth

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Discussion Form"

        binding.apply { 
            
            // set up recycler view
            setupRecyclerView()

            // fab onClick listener
            fab.setOnClickListener {
                findNavController().navigate(R.id.action_discussionFragment_to_addDiscussionFragment)
            }

            // fetch list data from firebase
            fetchDiscussionsFromFirebase()

        }

    }

    // fetch list data from firebase
    private fun fetchDiscussionsFromFirebase() {
        var discussionsList = ArrayList<DiscussionDataClass>()
        lifecycleScope.launch(Dispatchers.IO) {
            db.collection("sharedNotes").get()
                .addOnSuccessListener { documentResult ->
                    if (documentResult != null) {
                        Log.d(TAG, "DocumentSnapshot data size : ${documentResult.documents.size}")
                        for (document in documentResult) {
                            val dataClass = document.toObject(DiscussionDataClass::class.java)!!
                            discussionsList.add(dataClass)
                        }
                        Log.d(TAG, "List size : ${discussionsList.size}")
                        if (discussionsList.isNotEmpty()) {
                            recyclerAdapter.differ.submitList(discussionsList)
                        } else {
                            Snackbar.make(binding.root,"No discussions in the lists!", Snackbar.LENGTH_LONG).show()
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                }.addOnFailureListener { e ->
                    Log.d(TAG, "Error adding document", e)
                    Snackbar.make(binding.root,"Error occurred!",Snackbar.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }
        }
    }

    // add following IDs
    private fun addFollowingIDs(userFirebaseID: String, view: TextView) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.collection("users").document(firebaseAuth.currentUser!!.uid)
                .update("following", FieldValue.arrayUnion(userFirebaseID))
                .addOnSuccessListener {
                    Log.d(TAG, "User Followed Successfully!")
                    (activity as MainActivity).fetchFirebaseUserData()
                    view.setTextColor(ContextCompat.getColor(requireContext(),R.color.green_color))
                    view.text = "Following"
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error in following user!", e)
                    Snackbar.make(binding.root,"Error in following user!",Snackbar.LENGTH_LONG).show()
                }
        }
    }

    // Recycler view setup
    private fun setupRecyclerView(){
        recyclerAdapter = DiscussionRecyclerAdapter(requireContext())
        binding.apply {
            recyclerView.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }

        // on click listener
        recyclerAdapter.apply {
            setOnFollowItemClickListener { discussionData, textView ->
                addFollowingIDs(discussionData.userID.toString(), textView)
            }
            setOnPdfItemClickListener { link ->
                if (link.isNotEmpty()) {
                    val bundle = bundleOf(
                        "pdfLink" to link
                    )
                    findNavController().navigate(
                        R.id.action_discussionFragment_to_PDFWebViewFragment,
                        bundle
                    )
                }
            }
        }
        // followers observers
        FOLLOWING_USER_ID.observe(viewLifecycleOwner) {
            Log.d(TAG, it.size.toString())
            if (it.isNotEmpty()) {
                recyclerAdapter.addFollowersInTheList(it)
            }
        }
    }

    // option selector for Circle layout profile menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.following -> {
                findNavController().navigate(R.id.action_discussionFragment_to_followingFragment)
//                viewModel.deleteAllPDF()
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // calling own menu for this fragment // (not required any more but not deleted because testing is not done)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.following_menu_item, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}