package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentFeedbackBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hsalf.smileyrating.SmileyRating

class FeedbackFragment : Fragment() {

    // TAG
    private val TAG = "FeedbackFragment"

    // view binding
    private var _binding: FragmentFeedbackBinding? = null
    private val binding get() = _binding!!

    // toggle button
    private lateinit var toggle: ActionBarDrawerToggle

    // alert progress dialog
    private lateinit var dialog: Dialog

    // smile count
    private var smileCount = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedbackBinding.inflate(inflater, container, false)

        // change the title bar
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Feedback!"
        toggle = (activity as MainActivity).toggle
        toggle.isDrawerIndicatorEnabled = false
        val drawable = requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
        toggle.setHomeAsUpIndicator(drawable)
        Constants.IS_BACK_TOOLBAR_BTN_ACTIVE = true

        // init Loading Dialog
        dialog = Dialog(requireContext())
        dialog.apply {
            setContentView(R.layout.custom_dialog)
            setCancelable(false)
            if (window != null) {
                window!!.setBackgroundDrawable(ColorDrawable(0))
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            // on click listeners
            onClickListeners()

        }
    }

    // on click listeners
    private fun onClickListeners() {
        binding.apply {

            // smile Listener
            smileRating.setSmileySelectedListener { type ->

                when {
                    SmileyRating.Type.GREAT == type -> smileCount = 5
                    SmileyRating.Type.GOOD == type -> smileCount = 4
                    SmileyRating.Type.OKAY == type -> smileCount = 3
                    SmileyRating.Type.BAD == type -> smileCount = 2
                    SmileyRating.Type.TERRIBLE == type -> smileCount = 1
                }

            }

            // on submit click
            submitFeedback.setOnClickListener {
                if (!feedbackEt.text.isNullOrEmpty()) {
                    if (smileCount >= 0) {
                        dialog.show()
                        addFeedbackInServer(feedbackEt.text.toString())
                    } else {
                        Snackbar.make(
                            binding.root,
                            "Please select rating smile!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Snackbar.make(binding.root, "Empty feedback!", Snackbar.LENGTH_SHORT).show()
                }
            }

        }
    }

    // add add Feedback Server
    private fun addFeedbackInServer(feedbackInWords: String) {
        binding.apply {

            val feedbackData = hashMapOf(
                "feedbackInWords" to feedbackInWords,
                "starRating" to smileCount
            )

            Firebase.firestore.collection("userFeedbacks").document(Firebase.auth.currentUser!!.uid)
                .set(feedbackData)
                .addOnSuccessListener {
                    Log.d(TAG, "Feedback added successfully!")
                    dialog.dismiss()
                    showAlertDialogForConfirm()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error in adding feedback!", e)
                    dialog.dismiss()
                    Snackbar.make(binding.root, "Error occurred! Try again", Snackbar.LENGTH_LONG)
                        .show()
                }
        }
    }

    // delete classwork to classroom
    private fun showAlertDialogForConfirm() {
        AlertDialog.Builder(requireContext())
            .setPositiveButton("Close") { _, _ ->
                findNavController().popBackStack()
            }
            .setTitle("Thank you for providing feedback!")
            .setMessage("Your feedback has been submitted successfully.")
            .setCancelable(false)
            .create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}