package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants.IS_BACK_TOOLBAR_BTN_ACTIVE
import com.digitalinclined.edugate.data.model.PDFDataRoom
import com.digitalinclined.edugate.data.viewmodel.LocalViewModel
import com.digitalinclined.edugate.databinding.FragmentUploadVideoBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

class UploadVideoFragment : Fragment(R.layout.fragment_upload_video) {

    // TAG
    private val TAG = "UploadVideoFragment"

    // viewBinding
    private lateinit var binding: FragmentUploadVideoBinding

    // toggle
    lateinit var toggle: ActionBarDrawerToggle

    // selected Document Type
    private var selectedDocumentType: String? = ""

    // firebase db
    private val db = Firebase.firestore

    // pdf filename
    private var filename: String? = ""

    // base64 string filename
    private var base64String: String? = ""

    // alert progress dialog
    private lateinit var dialog: Dialog

    // viewModel
    private val localViewModel: LocalViewModel by activityViewModels()

    // arrayList
    private val uploadList = arrayListOf("Question Paper", "Notes")
    private val uploadListMap = mapOf(
        0 to "questionPapers",
        1 to "notes",
    )

    // comp object
    companion object {
        private const val PDF_SELECTION_CODE = 99
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUploadVideoBinding.bind(view)
        binding.apply {

            if (Firebase.auth.currentUser != null) {
                // change the title bar
                (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text =
                    "Upload to Contribute"

                // toggle btn toolbar setup
                toggle = (activity as MainActivity).toggle
                toggle.isDrawerIndicatorEnabled = false
                val drawable =
                    requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
                toggle.setHomeAsUpIndicator(drawable)
                IS_BACK_TOOLBAR_BTN_ACTIVE = true

            }
            // init Loading Dialog
            dialog = Dialog(requireContext())
            dialog.apply {
                setContentView(R.layout.custom_dialog)
                setCancelable(false)
                if (window != null) {
                    window!!.setBackgroundDrawable(ColorDrawable(0))
                }
            }

            // adapter init
            adapterForSpinners()

            // upload
            upload.setOnClickListener {
                if (!selectedDocumentType.isNullOrEmpty() && !filename.isNullOrEmpty() && !base64String.isNullOrEmpty()) {
                    addQuestionToLocalServer(selectedDocumentType!!, filename!!, base64String!!)
                    dialog.show()
                } else {
                    Snackbar.make(
                        binding.root,
                        "Please re-add appropriate pdf file!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

    // add file in local server
    private fun addQuestionToLocalServer(docType: String, pdfName: String, pdfBase64: String) {
        binding.apply {
            val currentPDFIDName = System.currentTimeMillis().toString()
            localViewModel.insertData(PDFDataRoom(0, currentPDFIDName, pdfName, pdfBase64))

            val timer = object : CountDownTimer(10000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    if ((millisUntilFinished / 1000) == 7L) {
                        localViewModel.getSelectedPdf(currentPDFIDName)
                            .observe(viewLifecycleOwner) { pdfId ->
                                addQuestionInFirebaseServer(
                                    currentPDFIDName,
                                    docType,
                                    pdfName,
                                    currentPDFIDName
                                )
                            }
                    }
                }

                override fun onFinish() {
                    dialog.dismiss()
                }
            }
            timer.start()
        }
    }

    // add file in firebase server
    private fun addQuestionInFirebaseServer(
        currentTime: String,
        docType: String,
        pdfName: String,
        pdfId: String
    ) {
        binding.apply {
            val questionData = hashMapOf(
                "paperName" to pdfName,
                "pdfFileId" to pdfId,
                "timestamp" to currentTime
            )

            // create db in fireStore
            db.collection(docType).document(currentTime)
                .set(questionData)
                .addOnSuccessListener {
                    Log.d(TAG, "PDF uploaded successful!")
                    Snackbar.make(binding.root, "PDF uploaded successfully!", Snackbar.LENGTH_LONG)
                        .show()
                    findNavController().popBackStack()
                    dialog.dismiss()
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "Error adding document", e)
                    Snackbar.make(
                        binding.root,
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

            // refreshing file name and base64 string
            filename = ""
            base64String = ""

            val selectedPdfFromStorage = data.data

            // file name from uri
            val fileNameTemp = queryName(requireContext(), selectedPdfFromStorage!!)
            Toast.makeText(requireContext(), fileNameTemp.toString(), Toast.LENGTH_SHORT).show()

            // get file
            try {
                // getting input stream and convert it into bytes
                val iStream: InputStream? =
                    requireActivity().contentResolver.openInputStream(selectedPdfFromStorage)
                val inputData: ByteArray = getBytes(iStream!!)

                // encoding file to base64 string
                var getBase64String = Base64.encodeToString(inputData, Base64.NO_WRAP)

                if (!fileNameTemp.isNullOrEmpty()) {
                    filename = fileNameTemp
                }

                if (!getBase64String.isNullOrEmpty()) {
                    base64String = getBase64String
                }

                Log.d(TAG, "$fileNameTemp : $getBase64String")

                if (!filename.isNullOrEmpty() && !base64String.isNullOrEmpty()) {
                    binding.apply {
                        upload.isEnabled = true
                        questionTimeMarkTV.text =
                            "File Attached : ${if (filename!!.length > 33) "${filename!!.take(33)}..." else filename}"
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
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
    private fun queryName(context: Context, uri: Uri): String {
        val returnCursor: Cursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    // Setting adapters for Spinner / AutoTextView
    private fun adapterForSpinners() {
        binding.apply {

            // adapter for course list
            var adapter = ArrayAdapter(
                requireContext(),
                R.layout.drop_down_list_view,
                uploadList
            )

            // course spinner adapter
            chooseAutoTextView.apply {
                setAdapter(
                    adapter
                )
            }

            // text view listener
            chooseAutoTextView.setOnItemClickListener { parent, view, position, id ->
                Log.d(TAG, uploadList[position])
                selectedDocumentType = uploadListMap[position]
                Log.d(TAG, selectedDocumentType.toString())
                selectPDFFromStorage()
            }

        }
    }

}