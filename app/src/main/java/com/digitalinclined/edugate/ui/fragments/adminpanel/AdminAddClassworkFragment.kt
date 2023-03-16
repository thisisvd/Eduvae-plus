package com.digitalinclined.edugate.ui.fragments.adminpanel

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentAdminAddClassworkBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminAddClassworkFragment : Fragment() {

    // TAG
    private val TAG = "AdminAddClassworkFragment"

    // binding
    private var _binding: FragmentAdminAddClassworkBinding? = null
    private val binding get() = _binding!!

    // args
    private val args: AdminAddClassroomDiscussionFragmentArgs by navArgs()

    // alert progress dialog
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAdminAddClassworkBinding.inflate(layoutInflater, container, false)

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
            setupOnClickListeners()

        }
    }

    // on click listeners
    private fun setupOnClickListeners() {
        binding.apply {

            // back
            backBtn.setOnClickListener {
                showAlertForBack()
            }

            // delete classwork
            addWorkBtn.setOnClickListener {
                createClassWork()
            }

        }
    }

    // create class work
    private fun createClassWork() {
        binding.apply {

            if (!questionEt.text.isNullOrEmpty()) {

                if (!isQuizOptionsEmpty()) {
                    if (!quizAnswer.text.isNullOrEmpty() && (quizAnswer.text.toString()
                            .toInt() in 1..4)
                    ) {
                        createClassworkRoomInServer()
                    } else {
                        Snackbar.make(
                            binding.root,
                            "Enter correct answer number",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Snackbar.make(binding.root, "Enter all options", Snackbar.LENGTH_SHORT).show()
                }
            } else {
                Snackbar.make(binding.root, "Please enter question", Snackbar.LENGTH_SHORT).show()
            }

        }
    }

    // create class room in server
    private fun createClassworkRoomInServer() {
        binding.apply {

            dialog.show()

            val classRoom = hashMapOf(
                "answer" to quizAnswer.text.toString().toInt() - 1,
                "options" to FieldValue.arrayUnion(
                    returnOptionValue(quizOption1.text.toString()),
                    returnOptionValue(quizOption2.text.toString()),
                    returnOptionValue(quizOption3.text.toString()),
                    returnOptionValue(quizOption4.text.toString())
                ),
                "question" to questionEt.text.toString()
            )

            Firebase.firestore.collection("classroom").document(args.classroomID)
                .collection("classwork").document(System.currentTimeMillis().toString())
                .set(classRoom)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ")
                    clearAllTvs()

                    // update in f-store
                    Firebase.firestore.collection("classroom")
                        .document(args.classroomID)
                        .update("hasClassWork", true)

                    dialog.dismiss()
                }
                .addOnFailureListener { e ->
                    dialog.dismiss()
                    Log.w(TAG, "Error adding document", e)
                    Snackbar.make(binding.root, "Error occurred", Snackbar.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
        }
    }

    // is quiz options empty
    private fun isQuizOptionsEmpty(): Boolean {
        binding.apply {
            return (TextUtils.isEmpty(quizOption1.text.toString()) ||
                    TextUtils.isEmpty(quizOption2.text.toString()) ||
                    TextUtils.isEmpty(quizOption3.text.toString()) ||
                    TextUtils.isEmpty(quizOption4.text.toString()))
        }
    }

    // option element
    private fun returnOptionValue(option: String): String {
        return if (option.isNullOrEmpty()) "null" else option
    }

    // clear all things
    private fun clearAllTvs() {
        binding.apply {
            // reset btn
            addWorkBtn.text = "Add another question"

            // reset question et
            questionEt.setText("")

            // reset options
            quizOption1.setText("")
            quizOption2.setText("")
            quizOption3.setText("")
            quizOption4.setText("")

            // reset answers
            quizAnswer.setText("")
        }
    }

    // back to classroom
    private fun showAlertForBack() {
        AlertDialog.Builder(requireContext())
            .setPositiveButton("Yes") { _, _ ->
                updateWorkSubmission()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            .setTitle("Are you sure?")
            .setMessage("You wouldn't be able to add more quiz. To add more you have to start overAll!")
            .create().show()
    }

    // update class work submission collection
    private fun updateWorkSubmission() {
        Firebase.firestore.collection("classroom/${args.classroomID}/classworkSubmissions")
            .get().addOnSuccessListener { documentReference ->
                if (documentReference != null) {
                    for (document in documentReference) {
                        // delete one by one
                        Firebase.firestore.collection("classroom/${args.classroomID}/classworkSubmissions")
                            .document(document.id).delete()
                    }
                    findNavController().popBackStack()
                }
            }.addOnFailureListener {
                Log.d(TAG, "Error deleting submission work")
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}