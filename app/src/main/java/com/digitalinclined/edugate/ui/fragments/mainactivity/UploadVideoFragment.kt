package com.digitalinclined.edugate.ui.fragments.mainactivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
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
import com.digitalinclined.edugate.constants.Constants
import com.digitalinclined.edugate.constants.Constants.IS_BACK_TOOLBAR_BTN_ACTIVE
import com.digitalinclined.edugate.constants.Constants.USER_COURSE
import com.digitalinclined.edugate.constants.Constants.USER_SEMESTER
import com.digitalinclined.edugate.databinding.FragmentUploadVideoBinding
import com.digitalinclined.edugate.ui.fragments.MainActivity
import com.digitalinclined.edugate.ui.viewmodel.MainViewModel
import com.digitalinclined.edugate.utils.Resource
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

    // Shared Preference
    private lateinit var sharedPreferences: SharedPreferences

    // course variable
    private var course: String? = ""

    // semester variable
    private var semester: String? = ""

    // pdf filename
    private var filename: String? = ""

    // base64 string filename
    private var base64String: String? = ""

    // viewModel
    private val viewModel: MainViewModel by activityViewModels()

    // arrayList
    private val uploadList = arrayListOf("Question Paper", "Notes", "Sample Paper")

    // comp object
    companion object {
        private const val PDF_SELECTION_CODE = 99
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUploadVideoBinding.bind(view)
        binding.apply {

            // change the title bar
            (activity as MainActivity).findViewById<TextView>(R.id.toolbarTitle).text = "Upload A Video"

            // shared preferences
            sharedPreferences = (activity as MainActivity).sharedPreferences
            course = sharedPreferences.getString(USER_COURSE,"")!!.lowercase()
            semester = sharedPreferences.getString(USER_SEMESTER,"")

            // toggle btn toolbar setup
            toggle = (activity as MainActivity).toggle
            toggle.isDrawerIndicatorEnabled = false
            val drawable = requireActivity().getDrawable(R.drawable.ic_baseline_arrow_back_ios_new_24)
            toggle.setHomeAsUpIndicator(drawable)
            IS_BACK_TOOLBAR_BTN_ACTIVE = true

            // adapter init
            adapterForSpinners()

            // view Model Observers
            viewModelObservers()

            // upload
            upload.setOnClickListener {
                if(upload.text == "Upload!") {
                    if (!course.isNullOrEmpty() && !semester.isNullOrEmpty()) {
                        viewModel.addNotes(course!!,semester!!,filename!!,base64String!!)
                        progressBar.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Please add an appropriate course and semester in your profile!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }else {
                    selectPDFFromStorage()
                }
            }

        }
    }

    // viewModel observers
    private fun viewModelObservers() {

        // notes added view model observer
        viewModel.addNotesDetail.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { message ->
                        if(message.message == "added") {
                            findNavController().popBackStack()
                            Toast.makeText(requireContext(),"Successfully uploaded!",Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility = View.INVISIBLE
                        }
                    }
                }
                is Resource.Error -> {
                    Log.d(TAG, "Error occurred while loading data! ${response.message}")
                }
                is Resource.Loading -> {
                    Log.d(TAG, "Loading!")
                }
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

        if(requestCode == PDF_SELECTION_CODE && resultCode == Activity.RESULT_OK && data != null) {

            // refreshing file name and base64 string
            filename = ""
            base64String = ""

            val selectedPdfFromStorage = data.data

            // file name from uri
            val fileNameTemp = queryName(requireContext(),selectedPdfFromStorage!!)
            Toast.makeText(requireContext(),fileNameTemp.toString(),Toast.LENGTH_SHORT).show()

            // get file
            try {
                // getting input stream and convert it into bytes
                val iStream: InputStream? = requireActivity().contentResolver.openInputStream(selectedPdfFromStorage)
                val inputData: ByteArray = getBytes(iStream!!)

                // encoding file to base64 string
                var getBase64String = Base64.encodeToString(inputData, Base64.NO_WRAP)

                if(!fileNameTemp.isNullOrEmpty()) {
                    filename = fileNameTemp
                }

                if(!getBase64String.isNullOrEmpty()) {
                    base64String = getBase64String
                }

                Log.d(TAG,"$fileNameTemp : $getBase64String")

                if(!filename.isNullOrEmpty() && !base64String.isNullOrEmpty()) {
                    binding.apply {
                        upload.text = "Upload!"
                        questionTimeMarkTV.text = "File Attached : ${if(filename!!.length > 33) "${filename!!.take(33)}..." else filename}"
                    }
                }

            } catch (e : Exception) {
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
    private fun queryName(context: Context, uri: Uri): String? {
        val returnCursor: Cursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    // Setting adapters for Spinner / AutoTextView
    private fun adapterForSpinners(){
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
                Log.d(TAG,uploadList[position])

                if(uploadList[position] == "Notes") {
                    selectPDFFromStorage()
                }

            }

        }
    }

}