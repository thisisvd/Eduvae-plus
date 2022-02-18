package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.constants.Constants.IS_BACK_TOOLBAR_BTN_ACTIVE
import com.digitalinclined.edugate.databinding.FragmentAddDiscussionBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class AddDiscussionFragment : Fragment() {

    // TAG
    private val TAG = "AddDiscussionFragment"

    // viewBinding
    private var _binding: FragmentAddDiscussionBinding? = null
    private val binding get() = _binding!!

    // Firebase
    private lateinit var firebaseAuth: FirebaseAuth

    // toggle button
    private lateinit var toggle: ActionBarDrawerToggle

    // comp object
    companion object {
        private const val PDF_SELECTION_CODE = 99
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddDiscussionBinding.inflate(inflater, container, false)

        // firebase init
        firebaseAuth = Firebase.auth

        // toggle btn toolbar setup
        toggle = (activity as MainActivity).toggle
        val drawable = requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
        toggle.setHomeAsUpIndicator(drawable)
        IS_BACK_TOOLBAR_BTN_ACTIVE = true

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            // submit discussion
            submitDiscussion.setOnClickListener {
                Toast.makeText(requireContext(),"Discussion Submitted!",Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }

            // attach a file
            attachFile.setOnClickListener{
                selectPDFFromStorage()
            }

        }
    }

    // select pdf from storage
    private fun selectPDFFromStorage() {
        val openStorageIntent = Intent(Intent.ACTION_GET_CONTENT)
        openStorageIntent.apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(Intent.createChooser(openStorageIntent, "Select PDF"), PDF_SELECTION_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PDF_SELECTION_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedPdfFromStorage = data.data

//            val fileName = File(selectedPdfFromStorage?.path).name
            val fileName = queryName(requireContext(),selectedPdfFromStorage!!)
            Toast.makeText(requireContext(),fileName.toString(),Toast.LENGTH_SHORT).show()

//            storageRef.child("images/${firebaseAuth.currentUser?.uid.toString()}.pdf")
//                .putFile(selectedPdfFromStorage!!)
//                .addOnSuccessListener {
//                    Log.d("TAGUI","Successfully Uploaded PDF")
//                    Toast.makeText(
//                        requireContext(),
//                        "Successfully Uploaded PDF!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//                .addOnFailureListener {
//                    Log.d("TAGUI","Failed to Upload PDF!")
//                    Toast.makeText(requireContext(), "Failed to Upload PDF!", Toast.LENGTH_SHORT)
//                        .show()
//                }
        }
    }

    // uri file name
    private fun queryName(context: Context, uri: Uri): String? {
        val returnCursor: Cursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}