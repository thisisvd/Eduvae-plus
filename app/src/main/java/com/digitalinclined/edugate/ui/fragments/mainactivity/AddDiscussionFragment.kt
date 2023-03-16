package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Base64.NO_WRAP
import android.util.Base64.encodeToString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.constants.Constants.IS_BACK_TOOLBAR_BTN_ACTIVE
import com.digitalinclined.edugate.data.model.PDFDataRoom
import com.digitalinclined.edugate.data.viewmodel.LocalViewModel
import com.digitalinclined.edugate.databinding.FragmentAddDiscussionBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.*
import java.util.*

class AddDiscussionFragment : Fragment() {

    // TAG
    private val TAG = "AddDiscussionFragment"

    // viewBinding
    private var _binding: FragmentAddDiscussionBinding? = null
    private val binding get() = _binding!!

    // view models
    private val viewModel: LocalViewModel by viewModels()

    // Firebase
    private lateinit var firebaseAuth: FirebaseAuth

    // firebase db
    private val db = Firebase.firestore
    private val dbReference = db.collection("sharedNotes")

    // shared Preferences
    private lateinit var sharedPreferences: SharedPreferences

    // toggle button
    private lateinit var toggle: ActionBarDrawerToggle

    // alert progress dialog
    private lateinit var dialog: Dialog

    // comp object
    companion object {
        private const val PDF_SELECTION_CODE = 99
    }

    // pdf file
    private var pdfBase64FileName: String = ""
    private var pdfBase64FileData: String = ""
    private var pdfFileStorageLocation: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddDiscussionBinding.inflate(inflater, container, false)

        // sharedPreferences init
        sharedPreferences = (requireActivity() as MainActivity).sharedPreferences

        // firebase init
        firebaseAuth = Firebase.auth

        // toggle btn toolbar setup
        toggle = (activity as MainActivity).toggle
        (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text =
            "Add a Discussion"
        val drawable = requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
        toggle.setHomeAsUpIndicator(drawable)
        IS_BACK_TOOLBAR_BTN_ACTIVE = true

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

            // submit discussion
            submitDiscussion.setOnClickListener {
                if (verifyDataFromUser(
                        discussionTitle.text.toString(),
                        discussionContentTitle.text.toString(),
                        pdfBase64FileName
                    )
                ) {
                    dialog.show()
                    uploadingPDFAndAddToServer()
                } else {
                    Snackbar.make(it, "Please fill all fields!", Snackbar.LENGTH_LONG).show()
                }
            }

            // attach a file
            attachFile.setOnClickListener {
                selectPDFFromStorage()
            }

        }
    }

    // uploading pdf file
    private fun uploadingPDFAndAddToServer() {
        binding.apply {
            viewModel.apply {
                if (pdfBase64FileData.isNotEmpty()) {
                    val currentPDFIDName = System.currentTimeMillis().toString()
                    insertData(
                        PDFDataRoom(
                            0,
                            currentPDFIDName,
                            pdfBase64FileName,
                            pdfBase64FileData
                        )
                    )

                    val timer = object : CountDownTimer(10000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            if ((millisUntilFinished / 1000) == 7L) {
                                getSelectedPdf(currentPDFIDName).observe(viewLifecycleOwner) { pdfId ->
                                    addToServer(
                                        discussionTitle.text.toString(),
                                        discussionContentTitle.text.toString(),
                                        pdfBase64FileName,
                                        pdfId.uniqueID
                                    )
                                }
                            }
                        }

                        override fun onFinish() {
                            dialog.dismiss()
                        }
                    }
                    timer!!.start()
                }
            }
        }
    }

    // add to server
    private fun addToServer(
        title: String,
        description: String,
        pdfBaseName: String,
        pdfFileLink: String
    ) {
        binding.apply {
            val discussionData = hashMapOf(
                "name" to sharedPreferences.getString(Constants.USER_NAME, ""),
                "course" to sharedPreferences.getString(Constants.USER_COURSE, ""),
                "courseYear" to sharedPreferences.getString(Constants.USER_YEAR, ""),
                "publishedDate" to System.currentTimeMillis().toString(),
                "userImage" to sharedPreferences.getString(Constants.USER_PROFILE_PHOTO_LINK, ""),
                "title" to title,
                "content" to description,
                "pdfName" to pdfBaseName,
                "pdfFile" to pdfFileLink,
                "likes" to 4,
                "comment" to 10,
                "userID" to firebaseAuth.currentUser?.uid.toString()
            )

            // create db in fireStore
            dbReference.document(System.currentTimeMillis().toString())
                .set(discussionData)
                .addOnSuccessListener {
                    Log.d(TAG, "Update in server successful!")
                    Snackbar.make(binding.root, "Discussion Submitted!", Snackbar.LENGTH_LONG)
                        .show()
                    findNavController().popBackStack()
                    dialog.dismiss()
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "Error adding document", e)
                    Snackbar.make(
                        binding.discussionTitle,
                        "Error occurred please try again!",
                        Snackbar.LENGTH_LONG
                    ).show()
                    dialog.dismiss()
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
        startActivityForResult(
            Intent.createChooser(openStorageIntent, "Select PDF"),
            PDF_SELECTION_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PDF_SELECTION_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedPdfFromStorage = data.data

            // file name from uri
            val fileName = queryName(requireContext(), selectedPdfFromStorage!!)
            Toast.makeText(requireContext(), fileName.toString(), Toast.LENGTH_SHORT).show()
            pdfBase64FileName = fileName.toString()
            pdfFileStorageLocation = selectedPdfFromStorage

            pdfBase64FileData = getBase64FromPath(selectedPdfFromStorage)

            Log.d("TAGU", selectedPdfFromStorage.path.toString())
            Log.d("TAGU", pdfBase64FileName)

            // file name view
            binding.attachFile.visibility = View.GONE
            binding.attachFileDone.visibility = View.VISIBLE
        }
    }

    // get base64 string
    private fun getBase64FromPath(uri: Uri): String {

        val iStream: InputStream? = requireActivity().contentResolver.openInputStream(uri)
        val inputData: ByteArray = getBytes(iStream!!)

        var getBase64String = encodeToString(inputData, NO_WRAP)
        Log.d(TAG, getBase64String)

        return getBase64String
    }

    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len = 0
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
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

    // check for data validation
    private fun verifyDataFromUser(
        title: String,
        description: String,
        pdfFileName: String
    ): Boolean {
        return if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
            false
        } else !(title.isEmpty() || description.isEmpty() || pdfFileName.isEmpty())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}