package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.graphics.Color
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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.ClassroomRecyclerAdapter
import com.digitalinclined.edugate.adapter.PreviousYearsPaperAdapter
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentClassroomBinding
import com.digitalinclined.edugate.databinding.FragmentOpenClassroomBinding
import com.digitalinclined.edugate.models.ClassroomObjectsDataClass
import com.digitalinclined.edugate.models.DiscussionDataClass
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OpenClassroomFragment : Fragment() {

    // TAG
    private val TAG = "OpenClassroomFragment"

    // binding
    private var _binding: FragmentOpenClassroomBinding? = null
    private val binding get() = _binding!!

    // Adapters
    lateinit var recyclerAdapter: ClassroomRecyclerAdapter

    // toggle button
    private lateinit var toggle: ActionBarDrawerToggle

    // args
    private val args: OpenClassroomFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOpenClassroomBinding.inflate(layoutInflater,container,false)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Open Classroom"
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

            // set up UI
            setUpUi()

            // Recycler view setup
            setupRecyclerView()

            // fetch data
            fetchClassroomFromFirebase()
        }
    }

    // fetch list data from firebase
    private fun fetchClassroomFromFirebase() {
        var discussionsList = ArrayList<ClassroomObjectsDataClass>()
        lifecycleScope.launch(Dispatchers.IO) {
            Firebase.firestore.collection("classroom").get()
                .addOnSuccessListener { documentResult ->
                    if (documentResult != null) {
                        Log.d(TAG, "DocumentSnapshot data size : ${documentResult.documents.size}")
                        for (document in documentResult) {
                            val dataClass = document.toObject(ClassroomObjectsDataClass::class.java)!!
                            discussionsList.add(dataClass)
                        }
                        Log.d(TAG, "List size : ${discussionsList.size}")
                        if (discussionsList.isNotEmpty()) {
                            recyclerAdapter.differ.submitList(discussionsList)
                        } else {
                            Snackbar.make(binding.root,"No discussions posted!", Snackbar.LENGTH_LONG).show()
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                }.addOnFailureListener { e ->
                    Log.d(TAG, "Error adding document", e)
                    Snackbar.make(binding.root,"Error occurred!", Snackbar.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }
        }
    }

    // set up UI
    private fun setUpUi() {
        binding.apply {

            // color
            constraintLayout2.setBackgroundColor(Color.parseColor(args.classColor))

            // class room name
            firstTv.text = args.classroomName

            // due date
            firstLastUpdateTv.text = args.classDueDate

            // image
            imageView1.setImageResource(args.imageInt)

        }
    }

    // Recycler view setup
    private fun setupRecyclerView(){
        recyclerAdapter = ClassroomRecyclerAdapter()
        binding.apply {
            classroomDiscussionRecyclerView.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }
        // on click listener
        recyclerAdapter.apply {
            setClassroomOnItemClickListener { pdfLink ->
                val bundle = bundleOf(
                    "pdfLink" to pdfLink
                )
                findNavController().navigate(R.id.action_openClassroomFragment_to_PDFWebViewFragment,bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}