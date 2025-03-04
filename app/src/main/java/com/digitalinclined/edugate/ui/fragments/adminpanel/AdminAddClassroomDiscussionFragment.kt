package com.digitalinclined.edugate.ui.fragments.adminpanel

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
import android.util.Base64.NO_WRAP
import android.util.Base64.encodeToString
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.constants.Constants.ADMIN_USER_NAME
import com.digitalinclined.edugate.data.model.PDFDataRoom
import com.digitalinclined.edugate.data.viewmodel.LocalViewModel
import com.digitalinclined.edugate.databinding.FragmentAddClassroomDiscussionBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.util.*

class AdminAddClassroomDiscussionFragment : Fragment() {

    // TAG
    private val TAG = "AddClassDiscussionFragment"

    // view binding
    private var _binding: FragmentAddClassroomDiscussionBinding? = null
    private val binding get() = _binding!!

    // view models
    private val viewModel: LocalViewModel by viewModels()

    // Firebase
    private lateinit var firebaseAuth: FirebaseAuth

    // nav-args
    private val args: AdminAddClassroomDiscussionFragmentArgs by navArgs()

    // firebase db
    private val db = Firebase.firestore
    private lateinit var dbReference: CollectionReference
    private val dbReferenceUpdateTime = db.collection("classroom")
    private val storageRef = Firebase.storage.reference

    // alert progress dialog
    private lateinit var dialog: Dialog

    // comp object
    companion object {
        private const val PDF_SELECTION_CODE = 99
        private const val IMAGE_SELECTION_CODE = 9943
    }

    // pdf file
    private var pdfBase64FileName: String = ""
    private var pdfBase64FileData: String = ""
    private var pdfFileStorageLocation: Uri? = null
    private var imageLink: String = ""
    private var isImageAttached: Boolean = false
    private lateinit var imageURI: Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddClassroomDiscussionBinding.inflate(inflater, container, false)

        binding.onlyForAdminLayout.visibility = View.VISIBLE
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
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

        // referencing db
        dbReference = db.collection("/classroom/${args.classroomID}/discussionsmaterial/")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            // submit discussion
            submitDiscussion.setOnClickListener {
                if (!discussionContentTitle.text.toString().isNullOrEmpty()) {
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

            // attach image
            attachAnImageFile.setOnClickListener {
                choosePicture()
            }
        }
    }

    // uploading pdf file
    private fun uploadingPDFAndAddToServer() {
        binding.apply {
            viewModel.apply {
                if (pdfBase64FileData.isNotEmpty() && isImageAttached) {
                    Log.d("JOSHA", "1")
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
                                    uploadPicture(
                                        discussionContentTitle.text.toString(),
                                        pdfId.uniqueID
                                    )
                                }
                            }
                        }

                        override fun onFinish() {
                            dialog.dismiss()
                        }
                    }
                    timer.start()
                } else if (pdfBase64FileData.isNotEmpty()) {
                    Log.d("JOSHA", "2")
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
                    timer.start()
                } else if (isImageAttached) {
                    Log.d("JOSHA", "3")
                    uploadPicture(discussionContentTitle.text.toString())
                } else {
                    Log.d("JOSHA", "4")
                    addToServer(discussionContentTitle.text.toString(), pdfBase64FileName, "")
                }
            }
        }
    }

    // add to server
    private fun addToServer(description: String, pdfBaseName: String, pdfId: String) {
        binding.apply {
            val discussionData = hashMapOf(
                "userName" to ADMIN_USER_NAME,
                "userImage" to "",
                "timestamp" to System.currentTimeMillis().toString(),
                "description" to description,
                "pdfNameStored" to pdfBaseName,
                "imageLink" to imageLink,
                "pdfId" to pdfId
            )

            // create db in fireStore
            dbReference.document(System.currentTimeMillis().toString())
                .set(discussionData)
                .addOnSuccessListener {

                    dbReferenceUpdateTime.document(args.classroomID)
                        .update("classDueDate", System.currentTimeMillis().toString())
                        .addOnSuccessListener {
                            Log.d(TAG, "Update in server successful!")
                            Snackbar.make(
                                binding.root,
                                "Discussion added to class!",
                                Snackbar.LENGTH_LONG
                            ).show()
                            findNavController().popBackStack()
                            dialog.dismiss()
                        }
                        .addOnFailureListener { e ->
                            Log.d(TAG, "Error in updating time", e)
                            findNavController().popBackStack()
                            dialog.dismiss()
                        }
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "Error adding discussion", e)
                    Snackbar.make(
                        binding.root,
                        "Error occurred please try again!",
                        Snackbar.LENGTH_LONG
                    ).show()
                    dialog.dismiss()
                }
        }
    }

    // choose picture to upload
    private fun choosePicture() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE_SELECTION_CODE)
    }

    // upload image in bac
    private fun uploadPicture(description: String, pdfId: String = "") {

        // show dialog
        dialog.show()

        val currentTS = System.currentTimeMillis().toString()

        lifecycleScope.launch(Dispatchers.IO) {
            // upload file
            storageRef.child("images/${firebaseAuth.currentUser?.uid.toString()}${currentTS}.jpg")
                .putFile(imageURI)
                .addOnSuccessListener {
                    // downloading image url
                    storageRef.child("images/${firebaseAuth.currentUser?.uid.toString()}${currentTS}.jpg")
                        .downloadUrl.addOnSuccessListener { uri ->
                            if (uri != null) {
                                imageLink = uri.toString()
                                addToServer(
                                    description,
                                    pdfBase64FileName,
                                    pdfId
                                )
                            }
                        }.addOnFailureListener {
                            dialog.dismiss()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to fetch Image!", Toast.LENGTH_SHORT)
                        .show()
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

        // pdf selection
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

        // image selection
        if (requestCode == IMAGE_SELECTION_CODE && resultCode == Activity.RESULT_OK && data?.data != null) {
            imageURI = data.data!!
            isImageAttached = true
            binding.apply {
                imageAttachedPreview.apply {
                    visibility = View.VISIBLE
                    setImageURI(imageURI)
                }
                // file name view
                attachAnImageFile.visibility = View.GONE
                attachImageDone.visibility = View.VISIBLE

                attachedIconView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_check_24
                    )
                )
            }
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
    private fun queryName(context: Context, uri: Uri): String {
        val returnCursor: Cursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}