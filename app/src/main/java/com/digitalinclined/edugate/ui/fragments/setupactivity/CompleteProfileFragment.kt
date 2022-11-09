package com.digitalinclined.edugate.ui.fragments.setupactivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants.COURSE_LIST
import com.digitalinclined.edugate.constants.Constants.SEMESTER_LIST
import com.digitalinclined.edugate.constants.Constants.YEAR_LIST
import com.digitalinclined.edugate.databinding.FragmentCompleteProfileBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CompleteProfileFragment : Fragment(R.layout.fragment_complete_profile) {

    // TAG
    private val TAG = "CompleteProfileFragment"

    // viewBinding
    private lateinit var binding: FragmentCompleteProfileBinding

    // Firebase
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var firebaseUser = firebaseAuth.currentUser

    // firebase db instances
    private val db = Firebase.firestore
    private val dbReference = db.collection("users")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCompleteProfileBinding.bind(view)

        // setting adapter method
        adapterForSpinners()

        // handle onBack pressed
        onBack()

        binding.apply {

            // complete sign up button onClick listener
            completeButton.setOnClickListener {
                if(!isAutoCompleteEmpty() && isEnteredValueTrue()) {
                    webViewProgressBar.visibility = View.VISIBLE
                    updateAnAccount()
                }
            }

        }
    }

    // update some values of account in fireStore
    private fun updateAnAccount() {
        binding.apply {
            val user = hashMapOf(
                "course" to chooseCourseAutoTextView.text.toString(),
                "year" to yearAutoTextView.text.toString(),
                "semester" to semesterAutoTextView.text.toString()
            )

            // create db in fireStore
            dbReference.document(firebaseUser!!.uid)
                .update(user as Map<String, Any>)
                .addOnSuccessListener {
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                }
                .addOnFailureListener { e ->
                    webViewProgressBar.visibility = View.GONE
                    Log.w(TAG, "Error adding document", e)
                }
        }
    }

    // Setting adapters for Spinner / AutoTextView
    private fun adapterForSpinners(){
        binding.apply {

            // adapter for course list
            var courseAdapter = ArrayAdapter(
                requireContext(),
                R.layout.drop_down_list_view,
                COURSE_LIST
            )

            // course spinner adapter
            chooseCourseAutoTextView.apply {
                setAdapter(
                    courseAdapter
                )
//                setDropDownBackgroundResource(R.color.button_gradient_end_color);
            }

            // adapter for year list
            var adapter = ArrayAdapter(
                requireContext(),
                R.layout.drop_down_list_view,
                YEAR_LIST
            )

            // course spinner adapter
            yearAutoTextView.apply {
                setAdapter(
                    adapter
                )
//                setDropDownBackgroundResource(R.color.button_gradient_end_color);
            }

            // adapter for year list
            adapter = ArrayAdapter(
                requireContext(),
                R.layout.drop_down_list_view,
                SEMESTER_LIST
            )

            // course spinner adapter
            semesterAutoTextView.apply {
                setAdapter(
                    adapter
                )
//                setDropDownBackgroundResource(R.color.button_gradient_end_color);
            }

        }
    }

    // Checking for autocomplete and correct text views
    private fun isAutoCompleteEmpty(): Boolean {
        var result = false

        binding.apply {

            if(chooseCourseAutoTextView.text.isNullOrEmpty()) {
                result = true
            }

            if(yearAutoTextView.text.isNullOrEmpty()) {
                result = true
            }

            if(semesterAutoTextView.text.isNullOrEmpty()) {
                result = true
            }
        }

        if(result) {
            // Snack bar on empty OTP
            Snackbar.make(binding.root ,
                "Please enter all fields!",
                Snackbar.LENGTH_SHORT
            ).show()
        }

        return result
    }

    // check for present values exists or not
    private fun isEnteredValueTrue(): Boolean {
        var result = true
        var resultCourse = false
        var resultYear = false
        var resultSemester = false

        binding.apply {
            for (i in COURSE_LIST) {
                if (chooseCourseAutoTextView.text.toString() == i) {
                    resultCourse = true
                }
            }

            for (i in YEAR_LIST) {
                if (yearAutoTextView.text.toString() == i) {
                    resultYear = true
                }
            }

            for (i in SEMESTER_LIST) {
                if (semesterAutoTextView.text.toString() == i) {
                    resultSemester = true
                }
            }

            if(!resultCourse) {
                chooseCourseAutoTextView.setText("")
                Snackbar.make(binding.root ,
                    "Please enter all fields!",
                    Snackbar.LENGTH_SHORT
                ).show()
                result = false
            }

            if(!resultYear) {
                yearAutoTextView.setText("")
                Snackbar.make(binding.root ,
                    "Please enter all fields!",
                    Snackbar.LENGTH_SHORT
                ).show()
                result = false
            }

            if(!resultSemester) {
                semesterAutoTextView.setText("")
                Snackbar.make(binding.root ,
                    "Please enter all fields!",
                    Snackbar.LENGTH_SHORT
                ).show()
                result = false
            }

        }

        return result
    }

    // handle onBack pressed
    private fun onBack() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event
            // Do nothing
        }
    }

}