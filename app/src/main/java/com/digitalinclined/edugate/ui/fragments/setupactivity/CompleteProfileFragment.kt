package com.digitalinclined.edugate.ui.fragments.setupactivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
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

        roughYearList.add("First Year")
        roughYearList.add("Second Year")
        roughYearList.add("Third Year")
        roughYearList.add("Fourth Year")

        // setting adapter method
        adapterForSpinners()

        // autocomplete checks
        checksForAutoCompleteTexts()

        binding.apply {

            // navigate to login screen
            login.setOnClickListener {
                findNavController().navigate(R.id.action_completeProfileFragment_to_loginFragment)
            }

            // complete sign up button onClick listener
            completeButton.setOnClickListener {
                updateAnAccount()
            }

        }
    }

    // update some values of account in fireStore
    private fun updateAnAccount() {
        val user = hashMapOf(
            "course" to "MBA",
            "year" to "Third Year",
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
    private fun checksForAutoCompleteTexts() {
        binding.apply {
            chooseCourseAutoTextView.onFocusChangeListener =
                View.OnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        binding.chooseCourseAutoTextView.setText("MBA")
                    }
                }
            yearAutoTextView.onFocusChangeListener =
                View.OnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        binding.yearAutoTextView.setText("First Year")
                    }
                }
        }
    }

}