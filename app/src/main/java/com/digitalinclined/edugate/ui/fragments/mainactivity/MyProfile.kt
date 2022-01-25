package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.os.ParcelFileDescriptor.open
import android.system.Os.bind
import android.system.Os.open
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.ProgressButton
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.constants.Constants.INDIAN_CITY_DATA
import com.digitalinclined.edugate.constants.Constants.USER_CITY
import com.digitalinclined.edugate.constants.Constants.USER_COURSE
import com.digitalinclined.edugate.constants.Constants.USER_EMAIL
import com.digitalinclined.edugate.constants.Constants.USER_NAME
import com.digitalinclined.edugate.constants.Constants.USER_PHONE
import com.digitalinclined.edugate.constants.Constants.USER_YEAR
import com.digitalinclined.edugate.databinding.FragmentMyprofileBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.common.collect.Range.open
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream
import java.nio.channels.AsynchronousFileChannel.open
import java.nio.channels.AsynchronousServerSocketChannel.open
import java.nio.channels.FileChannel.open

class MyProfile: Fragment(R.layout.fragment_myprofile) {

    // TAG
    private val TAG = "MyProfile"

    // viewBinding
    private lateinit var binding: FragmentMyprofileBinding

    // ROUGH LIST
    private val roughCourseList = arrayListOf(
        "BBA", "MBA", "MCA", "B.TECH"
    )
    private val roughYearList = arrayListOf(
        "1st Year", "2nd Year", "3rd Year", "4th Year"
    )

    // shared Preferences
    private lateinit var sharedPreferences: SharedPreferences

    // Firebase
    private lateinit var firebaseAuth: FirebaseAuth

    // firebase db
    private val db = Firebase.firestore
    private val dbReference = db.collection("users")

    // progress Button
    private lateinit var progressButton: ProgressButton
    private lateinit var viewProgress: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyprofileBinding.bind(view)

        // firebase init
        firebaseAuth = Firebase.auth

        // init progress bar button
        viewProgress = binding.root.findViewById(R.id.saveButton)
        progressButton = ProgressButton(requireContext(),viewProgress)
        progressButton.setBtnOriginalName("Save Changes")

        // sharedPreferences init
        sharedPreferences = (requireActivity() as MainActivity).sharedPreferences

        binding.apply {

            // back button pressed
            back.setOnClickListener {
                findNavController().popBackStack()
            }

            // setting adapter method
            adapterForSpinners()

            // fetch/Load data from sharedPreferences
            getUserDataFromSharedPreferences()

            // activate listeners
            isEnteredValueTrue()

            // save button listener
            viewProgress.setOnClickListener {
                if(!isTextEmpty() && isEnteredValueTrue()) {
                    progressButton.buttonActivated("Saving...")
                    if (isValuesUpdated()) {
                        updateInAnAccount()
                    }
                }
            }

        }

    }

    // Update an account in fireStore
    private fun updateInAnAccount() {
        binding.apply {
            val user = hashMapOf(
                "name" to name.text.toString(),
                "email" to sharedPreferences.getString(USER_EMAIL,""),
                "phone" to phoneNumber.text.toString(),
                "course" to chooseCourseAutoTextView.text.toString(),
                "year" to yearAutoTextView.text.toString(),
                "city" to cityAutoTextView.text.toString(),
                "profilephotolink" to ""
            )

            // create db in fireStore
            dbReference.document(firebaseAuth.currentUser!!.uid)
                .set(user)
                .addOnSuccessListener {
                    Log.d(TAG, "Update in server successful!")
                    progressButton.buttonSuccessfullyFinished("Saved!")
                    (requireActivity() as MainActivity).fetchFirebaseUserData()
                    findNavController().popBackStack()
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "Error adding document", e)
                    progressButton.buttonSuccessfullyFinished("Failed to Save!")
                }
        }
    }

    // check if the values are updated or not
    private fun isValuesUpdated(): Boolean {
        var result = false

        binding.apply {

            if (phoneNumber.text.toString() != sharedPreferences.getString(USER_PHONE, "")) {
                result = true
            }

            if (chooseCourseAutoTextView.text.toString() != sharedPreferences.getString(USER_COURSE, "")) {
                result = true
            }

            if (cityAutoTextView.text.toString() != sharedPreferences.getString(USER_CITY, "")) {
                result = true
            }

            if (yearAutoTextView.text.toString() != sharedPreferences.getString(USER_YEAR, "")) {
                result = true
            }

            // ------ checks for image and name left ------
            // here!!!

        }

        return result
    }

    // fetch from sharedPreferences
    private fun getUserDataFromSharedPreferences() {
        binding.apply {

            name.text = sharedPreferences.getString(USER_NAME,"")
            if(sharedPreferences.getString(USER_CITY,"") != "") {
                cityUpperText.text = "${sharedPreferences.getString(USER_CITY, "")}, India"
            }
            phoneNumber.setText(sharedPreferences.getString(USER_PHONE, ""))
            chooseCourseAutoTextView.setText(sharedPreferences.getString(USER_COURSE, ""))
            cityAutoTextView.setText(sharedPreferences.getString(USER_CITY, ""))
            yearAutoTextView.setText(sharedPreferences.getString(USER_YEAR, ""))

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

            adapter = ArrayAdapter(
                requireContext(),
                R.layout.drop_down_list_view,
                INDIAN_CITY_DATA
            )

            // city spinner adapter
            cityAutoTextView.apply {
                setAdapter(
                    adapter
                )
//                setDropDownBackgroundResource(R.color.button_gradient_end_color);
            }

        }
    }

    // check for empty edit texts
    private fun isTextEmpty(): Boolean {
        var isTextEmpty = false

        binding.apply {

            if (phoneNumber.text.isNullOrEmpty()) {
                isTextEmpty = true
                phoneNumberLayout.error = "*Phone number can't be empty!"
            } else if(phoneNumber.text.toString().length < 10) {
                isTextEmpty = true
                phoneNumberLayout.error = "*Please enter a valid phone number!"
            }

            if(chooseCourseAutoTextView.text.isNullOrEmpty()) {
                isTextEmpty = true
            }

            if(yearAutoTextView.text.isNullOrEmpty()) {
                isTextEmpty = true
            }

            if(isTextEmpty) {
                // Snack bar on empty OTP
                Snackbar.make(binding.root ,
                    "Please enter all fields!",
                    Snackbar.LENGTH_SHORT
                ).show()
            }

            return isTextEmpty
        }

    }

    // check for present values exists or not
    private fun isEnteredValueTrue(): Boolean {
        var result = true
        var resultCourse = false
        var resultYear = false
        binding.apply {

            // Activate the edittext listener's
            phoneNumber.addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    phoneNumberLayout.error = "*Phone number can't be empty!"
                } else {
                    phoneNumberLayout.error = null
                }
            }

            cityAutoTextView.addTextChangedListener {
                if (!it.isNullOrEmpty()) {
                    cityUpperText.text = "$it, India"
                } else {
                    cityUpperText.text = "No City Selected!"
                }
            }

            // checking for correct COURSE values
            for (i in roughCourseList) {
                if (chooseCourseAutoTextView.text.toString() == i) {
                    resultCourse = true
                }
            }

            // checking for correct YEAR values
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