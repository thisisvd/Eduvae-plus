package com.digitalinclined.edugate.ui.fragments.adminpanel

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.databinding.FragmentAdminCreateClassroomBinding
import com.digitalinclined.edugate.databinding.FragmentOpenClassroomBinding
import com.digitalinclined.edugate.dialogs.CreateClassroomIconDialog
import com.digitalinclined.edugate.models.ClassroomDetailsClass
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminCreateClassroomFragment : Fragment() {

    // TAG
    private val TAG = "OpenClassroomFragment"

    // binding
    private var _binding: FragmentAdminCreateClassroomBinding? = null
    private val binding get() = _binding!!

    // init default icon
    private var defaultIcon = "https://firebasestorage.googleapis.com/v0/b/fitme-minor-project.appspot.com/o/images%2F6NMEfmmnwjPEoIA8mYCC1678567312786.jpg?alt=media&token=6feb83b6-db5e-437e-964b-9767650dd305"

    // alert progress dialog
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAdminCreateClassroomBinding.inflate(layoutInflater,container,false)

        // init Loading Dialog
        dialog = Dialog(requireContext())
        dialog.apply {
            setContentView(R.layout.custom_dialog)
            setCancelable(false)
            if(window != null){
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

            // create classroom btn
            createClassroomBtn.setOnClickListener {
                if (!newClassroomName.text.isNullOrEmpty()) {
                    dialog.show()
                    createClassRoomInServer(newClassroomName.text.toString())
                    newClassroomNameLayout.error = null
                } else {
                    newClassroomNameLayout.error = "Enter classroom name"
                }
            }

            // icon selector
            imageViewLayout.setOnClickListener{
                var dialog = CreateClassroomIconDialog()
                dialog.show(parentFragmentManager,"Create Classroom Dialog")
                dialog.setOnItemClickListener { resourceID, link ->
                    classroomImageIcon.setImageResource(resourceID)
                    defaultIcon = link
                }
            }

        }
    }

    // create class room in server
    private fun createClassRoomInServer(classroomName: String) {
        binding.apply {

            val classRoom = hashMapOf(
                "classDueDate" to System.currentTimeMillis().toString(),
                "classroomName" to classroomName,
                "imageInt" to defaultIcon.toString(),
                "classroomID" to ""
            )

            Firebase.firestore.collection("classroom")
                .add(classRoom)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")

                    Firebase.firestore.collection("classroom").document(documentReference.id)
                        .update("classroomID",documentReference.id)
                        .addOnSuccessListener {
                            Log.d(TAG, "Update in server successful!")
                            dialog.dismiss()
                            findNavController().popBackStack()
                        }
                        .addOnFailureListener { e ->
                            dialog.dismiss()
                            Log.d(TAG, "Error adding document", e)
                        }

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