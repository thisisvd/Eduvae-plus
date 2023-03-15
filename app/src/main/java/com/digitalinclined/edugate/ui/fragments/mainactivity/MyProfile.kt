package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.adapter.ProgressButton
import com.digitalinclined.edugate.constants.Constants.COURSE_LIST
import com.digitalinclined.edugate.constants.Constants.INDIAN_CITY_DATA
import com.digitalinclined.edugate.constants.Constants.IS_BACK_TOOLBAR_BTN_ACTIVE
import com.digitalinclined.edugate.constants.Constants.SEMESTER_LIST
import com.digitalinclined.edugate.constants.Constants.USER_CITY
import com.digitalinclined.edugate.constants.Constants.USER_COURSE
import com.digitalinclined.edugate.constants.Constants.USER_CURRENT_COURSE
import com.digitalinclined.edugate.constants.Constants.USER_EMAIL
import com.digitalinclined.edugate.constants.Constants.USER_NAME
import com.digitalinclined.edugate.constants.Constants.USER_PHONE
import com.digitalinclined.edugate.constants.Constants.USER_PROFILE_PHOTO_LINK
import com.digitalinclined.edugate.constants.Constants.USER_SEMESTER
import com.digitalinclined.edugate.constants.Constants.USER_YEAR
import com.digitalinclined.edugate.constants.Constants.YEAR_LIST
import com.digitalinclined.edugate.databinding.FragmentMyprofileBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyProfile: Fragment(R.layout.fragment_myprofile) {

    // TAG
    private val TAG = "MyProfile"

    // viewBinding
    private lateinit var binding: FragmentMyprofileBinding

    // profile picture uri
    private lateinit var imageURI: Uri

    // shared Preferences
    private lateinit var sharedPreferences: SharedPreferences

    // Firebase
    private lateinit var firebaseAuth: FirebaseAuth

    // firebase db
    private val db = Firebase.firestore
    private val dbReference = db.collection("users")
    private val storageRef = Firebase.storage.reference

    // progress Button
    private lateinit var progressButton: ProgressButton
    private lateinit var viewProgress: View

    // toggle button
    private lateinit var toggle: ActionBarDrawerToggle

    // enable the options menu in activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyprofileBinding.bind(view)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "My Profile"
        toggle = (activity as MainActivity).toggle
        toggle.isDrawerIndicatorEnabled = false
        val drawable = requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
        toggle.setHomeAsUpIndicator(drawable)
        IS_BACK_TOOLBAR_BTN_ACTIVE = true

        // firebase init
        firebaseAuth = Firebase.auth

        // init progress bar button
        viewProgress = binding.root.findViewById(R.id.saveButton)
        progressButton = ProgressButton(requireContext(),viewProgress)
        progressButton.setBtnOriginalName("Save Changes")

        // sharedPreferences init
        sharedPreferences = (requireActivity() as MainActivity).sharedPreferences

        binding.apply {

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
                    } else {
                        progressButton.buttonActivated("Save Changes")
                        Toast.makeText(requireContext(),"Error occurred please check for values again!",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // profile picture onClick
            userProfileImage.setOnClickListener {
                choosePicture()
            }

        }

    }

    //choose profile picture
    private fun choosePicture(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1 && resultCode==RESULT_OK && data?.data!=null) {
            imageURI = data.data!!
            binding.apply {
                userProfileImage.setImageURI(imageURI)
                progressBar.visibility = View.VISIBLE
            }
            uploadPicture()
        }
    }

    // upload image in bac
    private fun uploadPicture() {

        lifecycleScope.launch(Dispatchers.IO) {
            // upload file
            storageRef.child("images/${firebaseAuth.currentUser?.uid.toString()}.jpg")
                .putFile(imageURI)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Successfully updated profile picture!",
                        Toast.LENGTH_SHORT
                    ).show()
                    (requireActivity() as MainActivity).fetchFirebaseUserData()
                    binding.progressBar.visibility = View.INVISIBLE
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to Upload Image!", Toast.LENGTH_SHORT)
                        .show()
                }

            // downloading image url
            storageRef.child("images/${firebaseAuth.currentUser?.uid.toString()}.jpg")
                .downloadUrl.addOnSuccessListener { uri ->
                    if (uri != null) {
                        dbReference.document(firebaseAuth.currentUser!!.uid)
                            .update("profilephotolink", uri.toString())
                            .addOnSuccessListener { Log.d(TAG, "Image Url uploaded Successfully!") }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error in uploaded url Successfully", e)
                            }
                    }
                }

        }

    }

    // Update an account in fireStore
    private fun updateInAnAccount() {
        binding.apply {
            val user = hashMapOf(
                "name" to nameTV.text.toString(),
                "email" to sharedPreferences.getString(USER_EMAIL,""),
                "phone" to phoneNumber.text.toString(),
                "course" to chooseCourseAutoTextView.text.toString(),
                "year" to yearAutoTextView.text.toString(),
                "city" to cityAutoTextView.text.toString(),
                "semester" to semesterAutoTextView.text.toString(),
                "profilephotolink" to sharedPreferences.getString(USER_PROFILE_PHOTO_LINK,"")
            )

            // create db in fireStore
            dbReference.document(firebaseAuth.currentUser!!.uid)
                .update(user as Map<String, Any>)
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

            if (semesterAutoTextView.text.toString() != sharedPreferences.getString(USER_SEMESTER, "")) {
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

            nameTV.text = sharedPreferences.getString(USER_NAME,"")
            if(sharedPreferences.getString(USER_CITY,"") != "") {
                cityUpperText.text = "${sharedPreferences.getString(USER_CITY, "")}, India"
            }
            phoneNumber.setText(sharedPreferences.getString(USER_PHONE, ""))
            chooseCourseAutoTextView.setText(sharedPreferences.getString(USER_COURSE, ""))
            cityAutoTextView.setText(sharedPreferences.getString(USER_CITY, ""))
            yearAutoTextView.setText(sharedPreferences.getString(USER_YEAR, ""))
            semesterAutoTextView.setText(sharedPreferences.getString(USER_SEMESTER, ""))

            // setting profile image
            val requestOptions = RequestOptions()
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
            requestOptions.centerCrop()
            if(sharedPreferences.getString(USER_PROFILE_PHOTO_LINK,"").toString() != null &&
                sharedPreferences.getString(USER_PROFILE_PHOTO_LINK,"").toString() != "") {
                Glide.with(root)
                    .load(sharedPreferences.getString(USER_PROFILE_PHOTO_LINK,"").toString())
                    .apply(requestOptions)
                    .into(userProfileImage)
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
                COURSE_LIST
            )

            // course spinner adapter
            chooseCourseAutoTextView.apply {
                setAdapter(
                    adapter
                )
//                setDropDownBackgroundResource(R.color.button_gradient_end_color);
            }

            // adapter for year list
            val yearAdapter = ArrayAdapter(
                requireContext(),
                R.layout.drop_down_list_view,
                YEAR_LIST
            )

            // course spinner adapter
            yearAutoTextView.apply {
                setAdapter(
                    yearAdapter
                )
//                setDropDownBackgroundResource(R.color.button_gradient_end_color);
            }

            val cityAdapter = ArrayAdapter(
                requireContext(),
                R.layout.drop_down_list_view,
                INDIAN_CITY_DATA
            )

            // city spinner adapter
            cityAutoTextView.apply {
                setAdapter(
                    cityAdapter
                )
//                setDropDownBackgroundResource(R.color.button_gradient_end_color);
            }

            val semesterAdapter = ArrayAdapter(
                requireContext(),
                R.layout.drop_down_list_view,
                SEMESTER_LIST
            )

            // city spinner adapter
            semesterAutoTextView.apply {
                setAdapter(
                    semesterAdapter
                )
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

            if(semesterAutoTextView.text.isNullOrEmpty()) {
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
        var resultSemester = false

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
            for (i in COURSE_LIST) {
                if (chooseCourseAutoTextView.text.toString() == i) {
                    resultCourse = true
                }
            }

            // checking for correct YEAR values
            for (i in YEAR_LIST) {
                if (yearAutoTextView.text.toString() == i) {
                    resultYear = true
                }
            }

            // checking for correct SEMESTER values
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

    // option selector for Circle layout profile menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.following -> {
                findNavController().navigate(R.id.action_myProfile_to_followingFragment)
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