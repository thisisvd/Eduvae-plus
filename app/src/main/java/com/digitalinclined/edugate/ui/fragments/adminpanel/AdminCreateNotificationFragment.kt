package com.digitalinclined.edugate.ui.fragments.adminpanel

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.FragmentAdminCreateNotificationBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminCreateNotificationFragment : Fragment() {

    // TAG
    private val TAG = "AdminCreateNotificationFragment"

    // binding
    private var _binding: FragmentAdminCreateNotificationBinding? = null
    private val binding get() = _binding!!

    // alert progress dialog
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAdminCreateNotificationBinding.inflate(layoutInflater, container, false)

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

            // back button
            backBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            // create btn
            postNotificationBtn.setOnClickListener {
                if (!notificationTitle.text.isNullOrEmpty() && !notificationContent.text.isNullOrEmpty()) {
                    postNotificationInServer(
                        notificationTitle.text.toString(),
                        notificationContent.text.toString()
                    )
                } else {
                    Snackbar.make(binding.root, "Enter all fields", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    // create notification & alert room in server
    private fun postNotificationInServer(title: String, content: String) {
        binding.apply {

            dialog.show()

            val id = System.currentTimeMillis().toString()

            val classRoom = hashMapOf(
                "content" to content,
                "timeStamp" to id,
                "title" to title
            )

            Firebase.firestore.collection("notificationsAndAlerts").document(id)
                .set(classRoom)
                .addOnSuccessListener {
                    Log.d(TAG, "Update in server successful!")
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "Notification posted!", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().popBackStack()
                }
                .addOnFailureListener { e ->
                    dialog.dismiss()
                    Log.w(TAG, "Error adding document", e)
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}