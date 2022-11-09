package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.NotesRecyclerAdapter
import com.digitalinclined.edugate.adapter.PreviousYearsPaperAdapter
import com.digitalinclined.edugate.adapter.SubjectRecyclerAdapter
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.constants.Constants.NOTES_TEMPORARY_LIST
import com.digitalinclined.edugate.data.viewmodel.LocalViewModel
import com.digitalinclined.edugate.databinding.FragmentNotesBinding
import com.digitalinclined.edugate.models.QuestionsNotesDataClass
import com.digitalinclined.edugate.models.SubjectRecyclerData
import com.digitalinclined.edugate.restapi.models.notes.Note
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.digitalinclined.edugate.ui.viewmodel.MainViewModel
import com.digitalinclined.edugate.utils.Resource
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class NotesFragment : Fragment(R.layout.fragment_notes) {

    // TAG
    private val TAG = "NotesFragment"

    // viewBinding
    private lateinit var binding: FragmentNotesBinding

    // Adapters
    lateinit var recyclerAdapter: PreviousYearsPaperAdapter

    // toggle
    lateinit var toggle: ActionBarDrawerToggle

    // Shared Preference
    private lateinit var sharedPreferences: SharedPreferences

    // course variable
    private var course: String? = ""

    // semester variable
    private var semester: String? = ""

    // firebase db
    private val db = Firebase.firestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNotesBinding.bind(view)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Notes"

        // shared preferences
        sharedPreferences = (activity as MainActivity).sharedPreferences
        course = sharedPreferences.getString(Constants.USER_COURSE,"")!!.lowercase()
        semester = sharedPreferences.getString(Constants.USER_SEMESTER,"")

        // toggle btn toolbar setup
        toggle = (activity as MainActivity).toggle
        toggle.isDrawerIndicatorEnabled = false
        val drawable = requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
        toggle.setHomeAsUpIndicator(drawable)
        Constants.IS_BACK_TOOLBAR_BTN_ACTIVE = true

        // set up recycler view
        setupRecyclerView()

        // fetch list data from firebase
        fetchQuestionsFromFirebase()

    }

    // fetch list data from firebase
    private fun fetchQuestionsFromFirebase() {
        var questionsList = ArrayList<QuestionsNotesDataClass>()
        lifecycleScope.launch(Dispatchers.IO) {
            db.collection("notes").get()
                .addOnSuccessListener { documentResult ->
                    if (documentResult != null) {
                        Log.d(TAG, "DocumentSnapshot data size : ${documentResult.documents.size}")
                        for (document in documentResult) {
                            val dataClass = document.toObject(QuestionsNotesDataClass::class.java)!!
                            Log.d(TAG, "List size : ${questionsList.size}")
                            questionsList.add(dataClass)
                        }
//                        Log.d(TAG, "List size : ${questionsList.size}")
                        if (questionsList.isNotEmpty()) {
                            recyclerAdapter.differ.submitList(questionsList)
                            binding.progressBar.visibility = View.GONE
                        } else {
                            Snackbar.make(binding.root,"No discussions in the lists!", Snackbar.LENGTH_LONG).show()
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }.addOnFailureListener { e ->
                    Log.d(TAG, "Error adding document", e)
                    Snackbar.make(binding.root,"Error occurred!", Snackbar.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }
        }
    }

    // Recycler view setup
    private fun setupRecyclerView(){
        recyclerAdapter = PreviousYearsPaperAdapter("notes")
        binding.apply {
            recyclerView.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }
        // on click listener
        recyclerAdapter.apply {
            setOnItemClickListener { strLink, value ->
                val bundle = bundleOf(
                    "pdfLink" to strLink
                )
                findNavController().navigate(R.id.action_notesFragment_to_PDFWebViewFragment,bundle)
            }
        }
    }

}