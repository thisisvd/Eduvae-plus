package com.digitalinclined.edugate.ui.fragments.setupactivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants.TEMP_CREATE_USER_EMAIL
import com.digitalinclined.edugate.constants.Constants.TEMP_CREATE_USER_NAME
import com.digitalinclined.edugate.databinding.FragmentCompleteProfileBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CompleteProfileFragment : Fragment(R.layout.fragment_complete_profile) {

    // TAG
    private val TAG = "CompleteProfileFragment"

    // ROUGH LIST -
    private var roughCourseList = ArrayList<String>()
    private var roughYearList = ArrayList<String>()

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

        // implement ROUGH DATA to spinners
        roughCourseList.add("BBA")
        roughCourseList.add("MBA")
        roughCourseList.add("MCA")
        roughCourseList.add("B.TECH")

        roughYearList.add("1st Year")
        roughYearList.add("2nd Year")
        roughYearList.add("3rd Year")
        roughYearList.add("4th Year")

        // setting adapter method
        adapterForSpinners()

        // autocomplete checks
//        autoCompleteTextsListeners()

        binding.apply {

            // navigate to login screen
            login.setOnClickListener {
                findNavController().navigate(R.id.action_completeProfileFragment_to_loginFragment)
            }

            // complete sign up button onClick listener
            completeButton.setOnClickListener {
                if(!isAutoCompleteEmpty() && isEnteredValueTrue()) {
//                    Toast.makeText(requireContext(),"Done",Toast.LENGTH_SHORT).show()
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
            )

            // create db in fireStore
            dbReference.document(firebaseUser!!.uid)
                .update(user as Map<String, Any>)
                .addOnSuccessListener {
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }
    }

    // Setting adapters for Spinner / AutoTextView
    private fun adapterForSpinners(){
        binding.apply {

            // adapter for course list
            var adapter = ArrayAdapter(
                requireContext(),
                R.layout.drop_down_list_view,
                roughCourseList
            )

            // course spinner adapter
            chooseCourseAutoTextView.apply {
                setAdapter(
                    adapter
                )
//                setDropDownBackgroundResource(R.color.button_gradient_end_color);
            }

            // adapter for year list
            adapter = ArrayAdapter(
                requireContext(),
                R.layout.drop_down_list_view,
                roughYearList
            )

            // course spinner adapter
            yearAutoTextView.apply {
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
        binding.apply {
            for (i in roughCourseList) {
                if (chooseCourseAutoTextView.text.toString() == i) {
                    resultCourse = true
                }
            }

            for (i in roughYearList) {
                if (yearAutoTextView.text.toString() == i) {
                    resultYear = true
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

        }

        return result
    }

}