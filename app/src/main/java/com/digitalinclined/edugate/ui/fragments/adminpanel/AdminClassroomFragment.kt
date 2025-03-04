package com.digitalinclined.edugate.ui.fragments.adminpanel

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.ClassroomRecyclerAdapter
import com.digitalinclined.edugate.databinding.FragmentClassroomBinding
import com.digitalinclined.edugate.models.ClassroomDetailsClass
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminClassroomFragment : Fragment() {

    // TAG
    private val TAG = "ClassroomFragment"

    // binding
    private var _binding: FragmentClassroomBinding? = null
    private val binding get() = _binding!!

    // recycler adapter
    private lateinit var recyclerAdapter: ClassroomRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentClassroomBinding.inflate(layoutInflater, container, false)

        binding.onlyForAdminLayout.visibility = View.VISIBLE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            // back
            backBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            // setup recycler
            setupRecyclerView()

            // fetch classrooms data
            fetchClassroomFromFirebase()
        }
    }

    // fetch classrooms data from firebase
    private fun fetchClassroomFromFirebase() {
        var classroomList = ArrayList<ClassroomDetailsClass>()
        lifecycleScope.launch(Dispatchers.IO) {
            Firebase.firestore.collection("classroom").get()
                .addOnSuccessListener { documentResult ->
                    if (documentResult != null) {
                        Log.d(TAG, "DocumentSnapshot data size : ${documentResult.documents.size}")
                        for (document in documentResult) {
                            val dataClass = document.toObject(ClassroomDetailsClass::class.java)!!
                            classroomList.add(dataClass)
                        }

                        if (classroomList.isNotEmpty()) {
                            recyclerAdapter.differ.submitList(classroomList)
                        } else {
                            Snackbar.make(
                                binding.root,
                                "No Classroom available!",
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

    // Recycler view setup
    private fun setupRecyclerView() {
        recyclerAdapter = ClassroomRecyclerAdapter()
        binding.apply {
            classroomRecyclerView.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }

        // on click listener
        recyclerAdapter.apply {
            setClassroomOnItemClickListener { dataClass, classColor, iconColor ->
                val bundle = bundleOf(
                    "classColor" to classColor,
                    "iconColor" to iconColor,
                    "classroomDetailsClass" to dataClass
                )
                findNavController().navigate(
                    R.id.action_adminClassroomFragment_to_adminOpenClassroomFragment,
                    bundle
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}